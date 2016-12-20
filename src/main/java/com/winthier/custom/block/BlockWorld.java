package com.winthier.custom.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter @RequiredArgsConstructor
class BlockWorld {
    final BlockManager blockManager;
    final World world;
    final Map<BlockRegion.Vector, BlockRegion> regions = new HashMap<>();
    final Map<BlockChunk.Vector, BlockChunk> chunks = new HashMap<>();
    final Set<BlockChunk.Vector> playerChunks = new HashSet<>();
    final Set<BlockChunk.Vector> chunksToLoad = new HashSet<>();

    BlockRegion getBlockRegion(BlockRegion.Vector position) {
        BlockRegion result = regions.get(position);
        if (result == null) {
            result = new BlockRegion(this, position);
            result.load();
            regions.put(position, result);
        }
        return result;
    }

    BlockChunk getBlockChunk(Block block) {
        return getBlockChunk(BlockChunk.Vector.of(block));
    }

    BlockChunk getBlockChunk(Chunk chunk) {
        return getBlockChunk(BlockChunk.Vector.of(chunk));
    }

    BlockChunk getBlockChunk(BlockChunk.Vector position) {
        BlockChunk result = chunks.get(position);
        if (result == null) {
            result = getBlockRegion(BlockRegion.Vector.of(position)).getBlockChunk(position);
            chunks.put(position, result);
            result.load();
        }
        return result;
    }

    BlockWatcher getBlockWatcher(Block block) {
        return getBlockChunk(block).getBlockWatcher(block);
    }

    void setBlockWatcher(Block block, BlockWatcher blockWatcher) {
        getBlockChunk(block).setBlockWatcher(block, blockWatcher);
    }

    void tick(List<Player> players) {
        playerChunks.clear();
        chunksToLoad.clear();
        final int RADIUS = 5;
        for (Player player: players) {
            if (!player.getWorld().equals(world)) continue;
            playerChunks.add(BlockChunk.Vector.of(player.getLocation().getBlock()));
        }
        for (BlockChunk.Vector vec: playerChunks) {
            for (int z = -RADIUS; z <= RADIUS; ++z) {
                for (int x = -RADIUS; x <= RADIUS; ++x) {
                    chunksToLoad.add(vec.relative(x, z));
                }
            }
        }
        for (BlockChunk.Vector vec: chunksToLoad) {
            getBlockChunk(vec).setUsed();
        }
        // Unload obsolete chunks
        long now = System.currentTimeMillis();
        for (BlockChunk chunk: new ArrayList<>(chunks.values())) {
            if (chunk.lastUsed + 1000*60 < now) {
                chunk.unload();
                chunks.remove(chunk.position);
            }
        }
    }
}

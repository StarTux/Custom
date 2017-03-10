package com.winthier.custom.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter @RequiredArgsConstructor
class BlockWorld {
    private final BlockManager blockManager;
    private final World world;
    private final Map<BlockRegion.Vector, BlockRegion> regions = new HashMap<>();
    private final Map<BlockChunk.Vector, BlockChunk> chunks = new HashMap<>();
    private final Set<BlockChunk.Vector> playerChunks = new HashSet<>();
    private final Set<BlockChunk.Vector> chunksToLoad = new HashSet<>();

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
        }
        return result;
    }

    void tick(List<Player> players) {
        playerChunks.clear();
        chunksToLoad.clear();
        final int radius = 5;
        for (Player player: players) {
            if (!player.getWorld().equals(world)) continue;
            playerChunks.add(BlockChunk.Vector.of(player.getLocation().getBlock()));
        }
        for (BlockChunk.Vector vec: playerChunks) {
            for (int z = -radius; z <= radius; ++z) {
                for (int x = -radius; x <= radius; ++x) {
                    chunksToLoad.add(vec.relative(x, z));
                }
            }
        }
        long now = System.currentTimeMillis();
        for (BlockChunk.Vector vec: chunksToLoad) {
            getBlockChunk(vec).getBlockWatchers();
            getBlockChunk(vec).setLastUsed(now);
        }
        // Unload obsolete chunks
        for (BlockChunk chunk: new ArrayList<>(chunks.values())) {
            if (chunk.getLastUsed() + 1000 * 60 < now) {
                chunk.unload();
                chunks.remove(chunk.getPosition());
            }
        }
    }
}

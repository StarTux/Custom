package com.winthier.custom.block;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
class BlockWorld {
    final BlockManager blockManager;
    final World world;
    final Map<BlockRegion.Vector, BlockRegion> regions = new HashMap<>();
    final Map<BlockChunk.Vector, BlockChunk> chunks = new HashMap<>();

    BlockRegion getBlockRegion(Block block) {
        BlockRegion.Vector position = BlockRegion.Vector.of(block);
        BlockRegion result = regions.get(position);
        if (result == null) {
            result = new BlockRegion(this, position);
            result.load();
            regions.put(position, result);
        }
        return result;
    }

    BlockChunk getBlockChunk(Block block) {
        BlockChunk.Vector position = BlockChunk.Vector.of(block);
        BlockChunk result = chunks.get(position);
        if (result == null) {
            result = getBlockRegion(block).getBlockChunk(position);
            chunks.put(position, result);
        }
        return result;
    }

    BlockWatcher getBlockWatcher(Block block) {
        return getBlockChunk(block).getBlockWatcher(block);
    }

    void setBlockWatcher(Block block, BlockWatcher blockWatcher) {
        getBlockChunk(block).setBlockWatcher(block, blockWatcher);
    }
}

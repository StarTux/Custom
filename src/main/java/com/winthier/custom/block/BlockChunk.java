package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

@Getter
@RequiredArgsConstructor
final class BlockChunk {
    @Value
    static final class Vector {
        private final int x, z;

        static int ofBlock(int v) {
            if (v < 0) {
                return (v + 1) / 16 - 1;
            } else {
                return v / 16;
            }
        }

        static Vector of(Block block) {
            return new Vector(ofBlock(block.getX()), ofBlock(block.getZ()));
        }

        static Vector of(Chunk chunk) {
            return new Vector(chunk.getX(), chunk.getZ());
        }

        Vector relative(int dx, int dz) {
            if (dx == 0 && dz == 0) return this;
            return new Vector(x + dx, z + dz);
        }
    }

    private final BlockWorld blockWorld;
    private final BlockRegion blockRegion;
    final Vector position;
    final Map<BlockVector, CustomConfig> configs = new HashMap<>();
    private Map<Block, BlockWatcher> blocks = null;
    long lastUsed = 0;

    Map<Block, BlockWatcher> getBlocks() {
        if (blocks == null) {
            blocks = new HashMap<>();
            for (Map.Entry<BlockVector, CustomConfig> entry: configs.entrySet()) {
                BlockVector bv = entry.getKey();
                Block block = blockWorld.world.getBlockAt(bv.getX(), bv.getY(), bv.getZ());
                CustomConfig config = entry.getValue();
                CustomBlock customBlock = CustomPlugin.getInstance().getBlockManager().getCustomBlock(config);
                if (customBlock == null) customBlock = new DefaultCustomBlock(config.getCustomId());
                BlockWatcher blockWatcher = customBlock.createBlockWatcher(block, config);
                if (blockWatcher == null) blockWatcher = new DefaultBlockWatcher(block, customBlock, config);
                blocks.put(block, blockWatcher);
            }
        }
        return blocks;
    }

    BlockWatcher getBlockWatcher(Block block) {
        return getBlocks().get(block);
    }

    void setBlockWatcher(Block block, BlockWatcher blockWatcher) {
        getBlocks().put(block, blockWatcher);
        configs.put(BlockVector.of(block), blockWatcher.getCustomConfig());
    }

    void load() {
        for (BlockWatcher blockWatcher: getBlocks().values()) {
            CustomPlugin.getInstance().getEventManager().registerEvents(blockWatcher);
            blockWatcher.didDiscoverBlock();
        }
    }

    void unload() {
        if (blocks == null) return;
        for (BlockWatcher blockWatcher: blocks.values()) {
            blockWatcher.blockWillUnload();
            CustomPlugin.getInstance().getEventManager().unregisterEvents(blockWatcher);
        }
        blocks = null;
    }
}

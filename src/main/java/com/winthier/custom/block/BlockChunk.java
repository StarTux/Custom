package com.winthier.custom.block;

import com.winthier.custom.CustomPlugin;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
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

    @Data
    static final class BlockData {
        private final String customId;
        private final Object data;
    }

    private final BlockWorld blockWorld;
    private final BlockRegion blockRegion;
    private final Vector position;
    // Filled by the owning BlockRegion via setBlockData().
    private final Map<BlockVector, BlockData> dataMap = new HashMap<>();
    private Map<Block, BlockWatcher> blockWatchers;
    @Setter private long lastUsed = 0;

    /**
     * Only called by BlockRegion during loading!
     */
    void setBlockData(BlockVector vector, String customId, Object customData) {
        dataMap.put(vector, new BlockData(customId, customData));
    }

    Map<Block, BlockWatcher> getBlockWatchers() {
        if (blockWatchers == null) {
            blockWatchers = new HashMap<>();
            for (Map.Entry<BlockVector, BlockData> entry: dataMap.entrySet()) {
                BlockVector bv = entry.getKey();
                Block block = blockWorld.getWorld().getBlockAt(bv.getX(), bv.getY(), bv.getZ());
                BlockData blockData = entry.getValue();
                String customId = blockData.getCustomId();
                CustomBlock customBlock = CustomPlugin.getInstance().getBlockManager().getCustomBlock(customId);
                if (customBlock == null) {
                    CustomPlugin.getInstance().getLogger().warning("Encountered unknown block '" + customId + "' in " + blockWorld.getWorld().getName() + " " + bv.getX() + "," + bv.getY() + "," + bv.getZ() + ". Using default implementation.");
                    customBlock = CustomPlugin.getInstance().getBlockManager().registerDefaultCustomBlock(customId);
                }
                BlockWatcher blockWatcher = customBlock.createBlockWatcher(block);
                blockWatchers.put(block, blockWatcher);
                blockWatcher.getCustomBlock().blockWasLoaded(blockWatcher);
            }
        }
        return blockWatchers;
    }

    BlockWatcher getBlockWatcher(Block block) {
        return getBlockWatchers().get(block);
    }

    void setBlockWatcher(Block block, BlockWatcher blockWatcher) {
        removeBlockWatcher(block);
        getBlockWatchers().put(block, blockWatcher);
        dataMap.put(BlockVector.of(block), new BlockData(blockWatcher.getCustomBlock().getCustomId(), null));
    }

    void removeBlockWatcher(Block block) {
        BlockWatcher blockWatcher = getBlockWatchers().remove(block);
        dataMap.remove(BlockVector.of(block));
        if (blockWatcher != null) {
            blockWatcher.getCustomBlock().blockWasRemoved(blockWatcher);
        }
    }

    void unload() {
        if (blockWatchers == null) return;
        for (BlockWatcher blockWatcher: blockWatchers.values()) {
            blockWatcher.getCustomBlock().blockWillUnload(blockWatcher);
        }
        blockWatchers = null;
    }
}

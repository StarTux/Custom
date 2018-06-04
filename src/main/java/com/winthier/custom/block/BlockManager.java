package com.winthier.custom.block;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.event.CustomTickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class BlockManager {
    private final CustomPlugin plugin;
    private final Map<String, CustomBlock> customBlockMap = new HashMap<>();
    private final Map<UUID, BlockWorld> worlds = new HashMap<>();
    private final TreeSet<BlockRegion> regionSaveList = new TreeSet<>(BlockRegion.LAST_SAVE_COMPARATOR);
    private BukkitRunnable task = null;
    private int ticks = 0;

    // Block getters

    public CustomBlock getCustomBlock(String customId) {
        return customBlockMap.get(customId);
    }

    public BlockWatcher getBlockWatcher(Block block) {
        return getBlockWorld(block.getWorld()).getBlockChunk(block).getBlockWatcher(block);
    }

    /**
     * Get all loaded BlockWatcher instances within a world.
     */
    public List<BlockWatcher> getBlockWatchers(World world, CustomBlock customBlock) {
        List<BlockWatcher> result = new ArrayList<>();
        BlockWorld blockWorld = getBlockWorld(world);
        if (blockWorld == null) return result;
        for (BlockChunk chunk: blockWorld.getChunks().values()) {
            for (BlockWatcher watcher: chunk.getBlockWatchers().values()) {
                if (watcher.getCustomBlock() == customBlock) {
                    result.add(watcher);
                }
            }
        }
        return result;
    }

    /**
     * Get all loaded BlockWatcher instances.
     */
    public List<BlockWatcher> getBlockWatchers(CustomBlock customBlock) {
        List<BlockWatcher> result = new ArrayList<>();
        for (BlockWorld world: worlds.values()) {
            for (BlockChunk chunk: world.getChunks().values()) {
                for (BlockWatcher watcher: chunk.getBlockWatchers().values()) {
                    if (watcher.getCustomBlock() == customBlock) {
                        result.add(watcher);
                    }
                }
            }
        }
        return result;
    }

    <T extends BlockWatcher> T getBlockWatcher(Block block, Class<T> clazz) {
        BlockWatcher result = getBlockWatcher(block);
        if (result == null) return null;
        if (!clazz.isInstance(result)) return null;
        return clazz.cast(result);
    }

    // Block setters.

    public BlockWatcher setBlock(Block block, String customId) {
        CustomBlock customBlock = getCustomBlock(customId);
        if (customBlock == null) throw new IllegalArgumentException("Custom block not found: " + customId);
        customBlock.setBlock(block);
        BlockWatcher blockWatcher = customBlock.createBlockWatcher(block);
        addBlockWatcher(blockWatcher);
        return blockWatcher;
    }

    public BlockWatcher wrapBlock(Block block, String customId) {
        CustomBlock customBlock = getCustomBlock(customId);
        if (customBlock == null) throw new IllegalArgumentException("Custom block not found: " + customId);
        BlockWatcher blockWatcher = customBlock.createBlockWatcher(block);
        addBlockWatcher(blockWatcher);
        return blockWatcher;
    }

    // Saving and loading block data

    public void removeBlockWatcher(BlockWatcher blockWatcher) {
        Block block = blockWatcher.getBlock();
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.removeBlockWatcher(block);
        regionSaveList.add(blockChunk.getBlockRegion());
    }

    public void saveBlockData(BlockWatcher blockWatcher, Object data) {
        Block block = blockWatcher.getBlock();
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.saveBlockData(blockWatcher, data);
        regionSaveList.add(blockChunk.getBlockRegion());
    }

    public Object loadBlockData(BlockWatcher blockWatcher) {
        Block block = blockWatcher.getBlock();
        return getBlockWorld(block.getWorld()).getBlockChunk(block).loadBlockData(blockWatcher);
    }

    public List<CustomBlock> getRegisteredBlocks() {
        return new ArrayList<>(customBlockMap.values());
    }

    // Internal use methods

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomBlock customBlock: event.getBlocks()) {
            String customId = customBlock.getCustomId();
            if (customBlockMap.containsKey(customId)) {
                plugin.getLogger().warning("Block Manager: Duplicate block id: " + customId);
            } else {
                customBlockMap.put(customId, customBlock);
                plugin.getEventManager().registerEvents(customBlock);
            }
        }
    }

    CustomBlock registerDefaultCustomBlock(String customId) {
        CustomBlock customBlock = new DefaultCustomBlock(customId);
        customBlockMap.put(customId, customBlock);
        plugin.getEventManager().registerEvents(customBlock);
        return customBlock;
    }

    public void onEnable() {
        task = new BukkitRunnable() {
            @Override public void run() {
                try {
                    tick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.runTaskTimer(plugin, 1, 1);
    }

    public void onDisable() {
        saveAll();
        try {
            task.cancel();
        } catch (IllegalStateException ise) { }
        task = null;
    }

    void tick() {
        int showTicks = this.ticks++;
        CustomTickEvent.Type.WILL_TICK_BLOCKS.call(showTicks);
        for (World world: plugin.getServer().getWorlds()) {
            List<Player> players = world.getPlayers();
            if (players.isEmpty()) continue;
            getBlockWorld(world).tick(players);
        }
        CustomTickEvent.Type.DID_TICK_BLOCKS.call(showTicks);
        saveOldest();
    }

    BlockWorld getBlockWorld(World world) {
        BlockWorld result = worlds.get(world.getUID());
        if (result == null) {
            result = new BlockWorld(this, world);
            worlds.put(world.getUID(), result);
        }
        return result;
    }

    public void addBlockWatcher(BlockWatcher blockWatcher) {
        Block block = blockWatcher.getBlock();
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.setBlockWatcher(block, blockWatcher);
        regionSaveList.add(blockChunk.getBlockRegion());
    }

    public void setDirty(Block block) {
        BlockRegion blockRegion = getBlockWorld(block.getWorld()).getBlockChunk(block).getBlockRegion();
        regionSaveList.add(blockRegion);
    }

    /**
     * Incremental save function.
     */
    boolean saveOldest() {
        if (regionSaveList.isEmpty()) return false;
        BlockRegion region = regionSaveList.first();
        long now = System.currentTimeMillis();
        if (region.getLastSave() + 10000 > now) return false;
        regionSaveList.remove(region);
        region.save();
        region.setLastSave(now);
        return true;
    }

    /**
     * Internal use only!
     *
     * Save all regions to disk.
     */
    public void saveAll() {
        long now = System.currentTimeMillis();
        for (BlockRegion region: regionSaveList) {
            region.save();
            region.setLastSave(now);
        }
        regionSaveList.clear();
    }

    /**
     * Internal use only!
     */
    public void onWorldUnload(World world) {
        BlockWorld blockWorld = worlds.get(world.getUID());
        if (blockWorld != null) {
            saveAll();
            worlds.remove(world.getUID());
        }
    }
}

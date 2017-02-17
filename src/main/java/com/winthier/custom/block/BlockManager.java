package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter @RequiredArgsConstructor
public final class BlockManager {
    private final CustomPlugin plugin;
    private final Map<String, CustomBlock> customBlockMap = new HashMap<>();
    private final Map<String, BlockWorld> worlds = new HashMap<>();
    private final TreeSet<BlockRegion> regionSaveList = new TreeSet<>(BlockRegion.LAST_SAVE_COMPARATOR);
    private BukkitRunnable task = null;

    // Public use methods

    public CustomBlock getCustomBlock(String id) {
        return customBlockMap.get(id);
    }

    public CustomBlock getCustomBlock(CustomConfig config) {
        return getCustomBlock(config.getCustomId());
    }

    public BlockWatcher getBlockWatcher(Block block) {
        return getBlockWorld(block.getWorld()).getBlockChunk(block).getBlockWatcher(block);
    }

    <T extends BlockWatcher> T getBlockWatcher(Block block, Class<T> clazz) {
        BlockWatcher result = getBlockWatcher(block);
        if (result == null) return null;
        if (!clazz.isInstance(result)) return null;
        return clazz.cast(result);
    }

    public BlockWatcher setBlock(Block block, CustomConfig config) {
        CustomBlock customBlock = getCustomBlock(config);
        if (customBlock == null) return null;
        BlockWatcher blockWatcher = customBlock.createBlockWatcher(block, config);
        if (blockWatcher == null) blockWatcher = new DefaultBlockWatcher(block, customBlock, config);
        addBlockWatcher(blockWatcher);
        return blockWatcher;
    }

    public BlockWatcher setBlock(Block block, String customId) {
        return setBlock(block, new CustomConfig(customId, (String)null));
    }

    public BlockWatcher wrapBlock(Block block, CustomConfig config) {
        CustomBlock customBlock = getCustomBlock(config);
        if (customBlock == null) return null;
        BlockWatcher blockWatcher = customBlock.createBlockWatcher(block, config);
        if (blockWatcher == null) blockWatcher = new DefaultBlockWatcher(block, customBlock, config);
        addBlockWatcher(blockWatcher);
        return blockWatcher;
    }

    public BlockWatcher wrapBlock(Block block, String customId) {
        return wrapBlock(block, new CustomConfig(customId, (String)null));
    }

    public void removeBlock(Block block) {
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.removeBlockWatcher(block);
        regionSaveList.add(blockChunk.getBlockRegion());
    }

    // Internal use methods

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomBlock block: event.getBlocks()) {
            String id = block.getCustomId();
            if (customBlockMap.containsKey(id)) {
                plugin.getLogger().warning("Block Manager: Duplicate block ID: " + id);
            } else {
                customBlockMap.put(id, block);
                plugin.getLogger().info("Registered block: " + id);
            }
        }
    }

    public void onEnable() {
        task = new BukkitRunnable() {
            @Override public void run() {
                tick();
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
        for (World world: plugin.getServer().getWorlds()) {
            List<Player> players = world.getPlayers();
            if (players.isEmpty()) continue;
            getBlockWorld(world).tick(players);
        }
        saveOldest();
    }

    BlockWorld getBlockWorld(World world) {
        BlockWorld result = worlds.get(world.getName());
        if (result == null) {
            result = new BlockWorld(this, world);
            worlds.put(world.getName(), result);
        }
        return result;
    }

    public void addBlockWatcher(BlockWatcher blockWatcher) {
        Block block = blockWatcher.getBlock();
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.setBlockWatcher(block, blockWatcher);
        regionSaveList.add(blockChunk.getBlockRegion());
    }

    public void removeBlockWatcher(BlockWatcher blockWatcher) {
        Block block = blockWatcher.getBlock();
        BlockChunk blockChunk = getBlockWorld(block.getWorld()).getBlockChunk(block);
        blockChunk.removeBlockWatcher(block);
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
        if (region.lastSave + 10000 > now) return false;
        regionSaveList.remove(region);
        region.save();
        region.lastSave = now;
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
            region.lastSave = now;
        }
        regionSaveList.clear();
    }
}

package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
public class BlockManager {
    final CustomPlugin plugin;
    final Map<String, CustomBlock> customBlockMap = new HashMap<>();
    final Map<String, BlockWorld> worlds = new HashMap<>();
    final TreeSet<BlockRegion> regionSaveList = new TreeSet<>(BlockRegion.LAST_SAVE_COMPARATOR);

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

    BlockWorld getBlockWorld(World world) {
        BlockWorld result = worlds.get(world.getName());
        if (result == null) {
            result = new BlockWorld(this, world);
            worlds.put(world.getName(), result);
        }
        return result;
    }

    public BlockWatcher getBlockWatcher(Block block) {
        return getBlockWorld(block.getWorld()).getBlockWatcher(block);
    }

    public CustomBlock getCustomBlock(String id) {
        return customBlockMap.get(id);
    }

    public CustomBlock getCustomBlock(CustomConfig config) {
        return getCustomBlock(config.getCustomId());
    }

    public void setBlockWatcher(Block block, BlockWatcher blockWatcher) {
        getBlockWorld(block.getWorld()).setBlockWatcher(block, blockWatcher);
    }

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

    void saveAll() {
        long now = System.currentTimeMillis();
        for (BlockRegion region: regionSaveList) {
            region.save();
            region.setLastSave(now);
        }
        regionSaveList.clear();
    }
}

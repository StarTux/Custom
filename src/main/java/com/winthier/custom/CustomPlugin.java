package com.winthier.custom;

import com.winthier.custom.block.BlockManager;
import com.winthier.custom.entity.EntityFinder;
import com.winthier.custom.entity.EntityManager;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.event.EventManager;
import com.winthier.custom.inventory.InventoryManager;
import com.winthier.custom.item.ItemManager;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CustomPlugin extends JavaPlugin implements Listener {
    @Getter private static CustomPlugin instance = null;
    private EventManager eventManager = new EventManager(this);
    private final InventoryManager inventoryManager = new InventoryManager(this);
    private ItemManager itemManager;
    private EntityManager entityManager;
    private BlockManager blockManager;
    private boolean reloadScheduled;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("custom").setExecutor(new CustomCommand(this));
        getServer().getPluginManager().registerEvents(new EntityFinder(this), this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);
        getServer().getPluginManager().registerEvents(this, this);
        scheduleReload();
    }

    @Override
    public void onDisable() {
        unload();
        instance = null;
    }

    void unload() {
        inventoryManager.onDisable();
        eventManager.clear();
        if (itemManager != null) itemManager.onDisable();
        if (entityManager != null) entityManager.onDisable();
        if (blockManager != null) blockManager.onDisable();
    }

    void reload() {
        getLogger().info("Reloading...");
        unload();
        itemManager = new ItemManager(this);
        entityManager = new EntityManager(this);
        blockManager = new BlockManager(this);
        CustomRegisterEvent event = new CustomRegisterEvent();
        getServer().getPluginManager().callEvent(event);
        itemManager.onCustomRegister(event);
        entityManager.onCustomRegister(event);
        blockManager.onCustomRegister(event);
        getServer().resetRecipes();
        for (Runnable task: event.getTasks()) {
            task.run();
        }
        itemManager.onEnable();
        entityManager.onEnable();
        blockManager.onEnable();
    }

    void scheduleReload() {
        if (reloadScheduled) return;
        getServer().getScheduler().runTask(this, () -> {
                if (reloadScheduled) {
                    reloadScheduled = false;
                    reload();
                }
            });
        reloadScheduled = true;
    }

    @EventHandler
    void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getDepend().contains(getName())
            || event.getPlugin().getDescription().getSoftDepend().contains(getName())) {
            scheduleReload();
        }
    }

    @EventHandler
    void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getDepend().contains(getName())
            || event.getPlugin().getDescription().getSoftDepend().contains(getName())) {
            scheduleReload();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (blockManager != null) blockManager.onWorldUnload(event.getWorld());
    }
}

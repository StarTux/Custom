package com.winthier.custom;

import com.winthier.custom.block.BlockManager;
import com.winthier.custom.entity.EntityFinder;
import com.winthier.custom.entity.EntityManager;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.event.EventManager;
import com.winthier.custom.inventory.InventoryManager;
import com.winthier.custom.item.ItemFinder;
import com.winthier.custom.item.ItemManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public final class CustomPlugin extends JavaPlugin {
    @Getter private static CustomPlugin instance = null;
    private EventManager eventManager = new EventManager(this);
    private final InventoryManager inventoryManager = new InventoryManager(this);
    private ItemManager itemManager;
    private EntityManager entityManager;
    private BlockManager blockManager;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("custom").setExecutor(new CustomCommand(this));
        new BukkitRunnable() {
            @Override public void run() {
                reload();
            }
        }.runTask(this);
        getServer().getPluginManager().registerEvents(new EntityFinder(this), this);
        getServer().getPluginManager().registerEvents(new ItemFinder(this), this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);
    }

    @Override
    public void onDisable() {
        unload();
        inventoryManager.onDisable();
        instance = null;
    }

    void unload() {
        eventManager.clear();
        if (entityManager != null) entityManager.onDisable();
        if (blockManager != null) blockManager.onDisable();
    }

    void reload() {
        unload();
        itemManager = new ItemManager(this);
        entityManager = new EntityManager(this);
        blockManager = new BlockManager(this);
        CustomRegisterEvent event = new CustomRegisterEvent();
        getServer().getPluginManager().callEvent(event);
        itemManager.onCustomRegister(event);
        entityManager.onCustomRegister(event);
        blockManager.onCustomRegister(event);
        entityManager.onEnable();
        blockManager.onEnable();
    }
}

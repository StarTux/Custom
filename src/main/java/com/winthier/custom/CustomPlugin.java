package com.winthier.custom;

import com.winthier.custom.block.BlockManager;
import com.winthier.custom.entity.EntityManager;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.event.EventManager;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class CustomPlugin extends JavaPlugin {
    @Getter static CustomPlugin instance = null;
    EventManager eventManager = new EventManager(this);
    ItemManager itemManager;
    EntityManager entityManager;
    BlockManager blockManager;
    
    @Override
    public void onEnable() {
        instance = this;
        getCommand("custom").setExecutor(new CustomCommand(this));
        new BukkitRunnable() {
            @Override public void run() {
                reload();
            }
        }.runTask(this);
    }

    @Override
    public void onDisable() {
        unload();
    }

    void unload() {
        eventManager.clear();
        if (entityManager != null) entityManager.onDisable();
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
    }
}

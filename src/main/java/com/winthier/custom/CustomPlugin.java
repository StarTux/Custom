package com.winthier.custom;

import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.event.EventManager;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemRegistry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class CustomPlugin extends JavaPlugin {
    @Getter static CustomPlugin instance = null;
    @Getter ItemRegistry itemRegistry;
    @Getter EventManager eventManager = new EventManager(this);
    
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

    void reload() {
        itemRegistry = new ItemRegistry(this);
        getServer().getPluginManager().registerEvents(itemRegistry, this);
        CustomRegisterEvent event = new CustomRegisterEvent();
        getServer().getPluginManager().callEvent(event);
    }
}

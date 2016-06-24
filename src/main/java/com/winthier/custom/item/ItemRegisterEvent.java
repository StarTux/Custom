package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called so all plugins interested in registering
 * custom items can listen to it and use it to do so.
 */
public class ItemRegisterEvent extends Event {
    // Event Stuff 
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // My Stuff

    public void registerItem(Item item) {
        if (!CustomPlugin.getInstance().getItemRegistry().registerItem(item)) {
            throw new RuntimeException("Duplicate custom item ID: " + item.getId());
        }
    }

    static void call() {
        Bukkit.getServer().getPluginManager().callEvent(new ItemRegisterEvent());
    }
}


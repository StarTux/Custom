package com.winthier.custom.entity;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public interface EntityWatcher {
    /**
     * Get the Bukkit entity.  This must always return the same
     * instance.
     */
    Entity getEntity();

    /**
     * Get the Custom entity. This must always return the same
     * instance.
     */
    CustomEntity getCustomEntity();

    default void handleMessage(CommandSender sender, String[] msgs) { }

    default void handleEvent(Event event, EntityContext context) { }
}

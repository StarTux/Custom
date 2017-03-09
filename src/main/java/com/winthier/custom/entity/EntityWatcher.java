package com.winthier.custom.entity;

import org.bukkit.entity.Entity;

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
}

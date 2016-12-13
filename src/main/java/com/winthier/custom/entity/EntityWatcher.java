package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.entity.CustomEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public interface EntityWatcher extends Listener {
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

    /**
     * Get the configuration associated with this entity.
     */
    CustomConfig getCustomConfig();

    /**
     * Called right after this entity was discovered in the wild,
     * as opposed to spawned in.
     */
    void didDiscoverEntity();

    /**
     * Called right before an entity is unloaded, whilt it is
     * still valid.
     */
    void willUnloadEntity();
}

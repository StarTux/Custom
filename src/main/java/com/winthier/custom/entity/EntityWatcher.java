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
     * Called right after this entity was created.  It has been
     * registered with the framework and is ready to operate.
     */
    void didSpawnEntity();

    /**
     * Called right after this entity was discovered in the wild,
     * as opposed to spawned in.  It has been registered with the
     * framework and is ready to operate.
     */
    void didDiscoverEntity();

    /**
     * Called right before an entity is unloaded.  The Entity it
     * is still valid.  This method is not guaranteed to be called
     * every time the Entity disappears from the world.  It will
     * be omitted if an entity despawns, dies, or is removed by a
     * plugin.
     */
    void entityWillUnload();

    /**
     * Called when the entity has been unloaded.  The
     * EntityWatcher is no longer registered with EntityManager.
     * The Entity is not necessarily valid.
     */
    void entityDidUnload();
}

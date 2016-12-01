package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface CustomEntity extends Listener {
    /**
     * Custom identifier to this Framework.
     */
    String getCustomId();

    /**
     * Spawn a new entity of this type at the given location, with
     * the given configuration.  This function is not expected to
     * actually store the config in the entity.  The caller is
     * responsible to save the config and call watchEntity().
     */
    Entity spawnEntity(Location location, CustomConfig config);

    /**
     * Wrap around a discovered or created entity with the given
     * config.  This function should do nothing but create a new
     * EntityWatcher and then return it.  Return null to use the
     * DefaultEntityWatcher instead.  The caller is responsible
     * for registering the result with EntityManager.
     *
     * This function is called by the framework discovers an
     * existing entity with a valid CustomConfig in the world, or
     * when a new entity is created.
     */
    EntityWatcher watchEntity(Entity entity, CustomConfig config);
}

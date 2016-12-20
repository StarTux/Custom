package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public interface CustomEntity {
    /**
     * Custom identifier to this Framework.
     */
    String getCustomId();

    /**
     * Spawn a new entity of this type at the given location, with
     * the given configuration.  This function shall not actually
     * store the config in the entity, wrap it via
     * createEntityWatcher(), or register the entity with the
     * framework.  Leave these tasks to the caller!
     */
    Entity spawnEntity(Location location, CustomConfig config);

    /**
     * Wrap around a discovered or created entity with the given
     * config.  That is, return a new EntityWatcher with the given
     * entity, CustomEntity, and CustomConfig.  This function
     * shall not register the result with the framework.  Leave
     * this task to the caller!
     *
     * This function is called by the framework discovers an
     * existing entity with a valid CustomConfig in the world, or
     * when a new entity is created.
     */
    EntityWatcher createEntityWatcher(Entity entity, CustomConfig config);
}

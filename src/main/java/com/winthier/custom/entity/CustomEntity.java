package com.winthier.custom.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public interface CustomEntity extends Listener {
    /**
     * Custom identifier to this Framework.
     */
    String getCustomId();

    /**
     * Spawn a new entity of this type at the given location.  This
     * function shall not actually register the entity with the
     * framework.  Leave these tasks to the caller!
     */
    Entity spawnEntity(Location location);

    /**
     * Wrap around a discovered or created entity.  That is, return a
     * new EntityWatcher with the given Entity and CustomEntity.  This
     * function shall not register the result with the framework.
     * Leave this task to the caller!
     *
     * This function is called by the framework discovers an existing
     * entity in the world, or when a new entity is created.
     */
    default EntityWatcher createEntityWatcher(Entity entity) {
        final CustomEntity customEntity = this;
        return new EntityWatcher() {
            @Override public Entity getEntity() {
                return entity;
            }
            @Override public CustomEntity getCustomEntity() {
                return customEntity;
            }
        };
    }

    /**
     * Called right after this entity was created.  It has been
     * registered with the framework and is ready to operate.
     */
    default void entityWasSpawned(EntityWatcher watcher) { }

    /**
     * Called right after this entity was discovered in the wild,
     * as opposed to spawned in.  It has been registered with the
     * framework and is ready to operate.
     */
    default void entityWasDiscovered(EntityWatcher watcher) { }

    /**
     * Called right before an entity is unloaded.  The Entity it
     * is still valid.  This method is not guaranteed to be called
     * every time the Entity disappears from the world.  It will
     * be omitted if an entity despawns, dies, or is removed by a
     * plugin.
     */
    default void entityWillUnload(EntityWatcher watcher) { }

    /**
     * Called when the entity has been unloaded.  The
     * EntityWatcher is no longer registered with EntityManager.
     * The Entity is not necessarily valid.
     */
    default void entityDidUnload(EntityWatcher watcher) { }

    /**
     * Called right after an EntityWatcher was registered with
     * EntityManager and is therefore valid.
     */
    default void entityWatcherDidRegister(EntityWatcher water) { }

    /**
     * Called right before an EntityManager will be unregistered from
     * EntityManager and is therefore invalid.
     */
    default void entityWatcherWillUnregister(EntityWatcher water) { }
}

package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public final class EntityManager {
    final CustomPlugin plugin;
    private final EntityCrawler entityCrawler = new EntityCrawler(this);
    final Map<String, CustomEntity> customEntityMap = new HashMap<>();
    final Map<UUID, EntityWatcher> entityWatcherMap = new HashMap<>();

    // Public use methods

    /**
     * Find the corresponding EntityWatcher for any entity.  If
     * the entity is customized but the EntityWatcher does not
     * exist yet, it will be created and all the appropriate hooks
     * called.  If the entity is not customized, nothing happens
     * and null is returned.
     *
     * @return The corresponding EntityWatcher or null if the
     * entity is not customized.
     */
    public EntityWatcher getEntityWatcher(Entity entity) {
        UUID uuid = entity.getUniqueId();
        EntityWatcher result;
        result = entityWatcherMap.get(uuid);
        if (result != null) return result;
        CustomConfig config = CustomConfig.of(entity);
        if (config != null) {
            Location loc = entity.getLocation();
            plugin.getLogger().info(String.format("Discovered custom entity '%s' at %s %d %d %d", config.getCustomId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            CustomEntity customEntity = getCustomEntity(config);
            if (customEntity == null) {
                plugin.getLogger().warning("Encountered unknown custom entity '" + config.getCustomId() + "'");
                customEntity = new DefaultCustomEntity(config.getCustomId());
            }
            customEntityMap.put(customEntity.getCustomId(), customEntity);
            EntityWatcher watcher = customEntity.createEntityWatcher(entity, config);
            if (watcher == null) watcher = new DefaultEntityWatcher(entity, customEntity, config);
            watchEntity(watcher);
            watcher.didDiscoverEntity();
            return watcher;
        }
        return null;
    }

    /**
     * Get a CustomEntity registerd under the ID in the config.
     *
     * @return The CustomEntity with the ID in the config, or null
     * if none exists.
     */
    public CustomEntity getCustomEntity(CustomConfig config) {
        return getCustomEntity(config.getCustomId());
    }

    /**
     * Get a CustomEntity registerd under the ID.
     *
     * @return The CustomEntity with the given ID, or null if none
     * exists.
     */
    public CustomEntity getCustomEntity(String id) {
        return customEntityMap.get(id);
    }

    public EntityWatcher wrapEntity(Entity entity, CustomConfig config) {
        CustomEntity customEntity = getCustomEntity(config);
        if (customEntity == null) throw new IllegalArgumentException("Unknown custom entity: " + config.getCustomId());
        EntityWatcher entityWatcher = customEntity.createEntityWatcher(entity, config);
        if (entityWatcher == null) entityWatcher = new DefaultEntityWatcher(entity, customEntity, config);
        watchEntity(entityWatcher);
        return entityWatcher;
    }

    public EntityWatcher wrapEntity(Entity entity, String customId) {
        return wrapEntity(entity, new CustomConfig(customId, (String)null));
    }

    public EntityWatcher spawnEntity(Location location, CustomConfig config) {
        CustomEntity customEntity = getCustomEntity(config);
        if (customEntity == null) throw new IllegalArgumentException("Unknown custom entity: " + config.getCustomId());
        Entity entity = customEntity.spawnEntity(location, config);
        if (entity == null) return null;
        EntityWatcher entityWatcher = customEntity.createEntityWatcher(entity, config);
        if (entityWatcher == null) entityWatcher = new DefaultEntityWatcher(entity, customEntity, config);
        watchEntity(entityWatcher);
        return entityWatcher;
    }

    public EntityWatcher spawnEntity(Location location, String customId) {
        return spawnEntity(location, new CustomConfig(customId, (String)null));
    }

    /**
     * Remove an EntityWatcher from the framework.
     */
    public void removeEntity(EntityWatcher watcher) {
        entityWatcherMap.remove(watcher.getEntity().getUniqueId());
        plugin.getEventManager().unregisterEvents(watcher);
    }

    // Internal use methods

    /**
     * Internal use only!
     *
     * Call the CustomRegisterEvent to give all clients a chance
     * to register their custom entities, then give it to this
     * function.
     */
    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomEntity customEntity: event.getEntities()) {
            if (customEntityMap.containsKey(customEntity.getCustomId())) {
                plugin.getLogger().warning("Entity Manager: Duplicate entity ID: " + customEntity.getCustomId());
            } else {
                customEntityMap.put(customEntity.getCustomId(), customEntity);
                plugin.getLogger().info("Registered entity: " + customEntity.getCustomId());
            }
        }
    }

    /**
     * Internal use only!
     *
     * Call this once, after entities have been registered via
     * onCustomRegister(), so this manager instance can do its
     * work.
     */
    public void onEnable() {
        entityCrawler.checkAll();
        entityCrawler.start();
    }

    /**
     * Internal use only!
     *
     * Call this once when the task of this instance is over.
     */
    public void onDisable() {
        entityCrawler.stop();
    }
    /**
     * Internal use only!
     *
     * Register an EntityWatcher with the framework.  It will be
     * returned by getEntityWatcher() and listen to all handled
     * events until it is unregistered with the function below.
     */
    public void watchEntity(EntityWatcher watcher) {
        entityWatcherMap.put(watcher.getEntity().getUniqueId(), watcher);
        plugin.getEventManager().registerEvents(watcher);
    }
}

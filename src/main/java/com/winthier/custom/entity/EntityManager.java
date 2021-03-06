package com.winthier.custom.entity;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public final class EntityManager {
    private final CustomPlugin plugin;
    private final EntityCrawler entityCrawler = new EntityCrawler(this);
    private final Map<String, CustomEntity> customEntityMap = new HashMap<>();
    private final Map<UUID, EntityWatcher> entityWatcherMap = new HashMap<>();
    private static final String KEY_CUSTOM_ID = "Winthier.Custom.ID=";

    // Public use methods

    public String getCustomId(Entity entity) {
        String tag = null;
        for (String aTag: entity.getScoreboardTags()) {
            if (aTag.startsWith(KEY_CUSTOM_ID)) {
                tag = aTag;
                break;
            }
        }
        if (tag == null) return null;
        String[] tokens = tag.split("=", 2);
        return tokens[1];
    }

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
        String customId = getCustomId(entity);
        if (customId == null) return null;
        Location loc = entity.getLocation();
        CustomEntity customEntity = getCustomEntity(customId);
        if (customEntity == null) {
            plugin.getLogger().warning("Encountered unknown entity '" + customId + "'. Using default implementation.");
            customEntity = new DefaultCustomEntity(customId);
            customEntityMap.put(customId, customEntity);
            plugin.getEventManager().registerEvents(customEntity);
        }
        EntityWatcher watcher = customEntity.createEntityWatcher(entity);
        watchEntity(watcher);
        customEntity.entityWasDiscovered(watcher);
        return watcher;
    }

    public CustomEntity getCustomEntity(Entity entity) {
        EntityWatcher entityWatcher = getEntityWatcher(entity);
        if (entityWatcher == null) return null;
        return entityWatcher.getCustomEntity();
    }

    public EntityWatcher getEntityWatcher(UUID uuid) {
        return entityWatcherMap.get(uuid);
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

    public List<EntityWatcher> getEntityWatchers(CustomEntity customEntity) {
        List<EntityWatcher> result = new ArrayList<>();
        for (EntityWatcher watcher: entityWatcherMap.values()) {
            if (watcher.getCustomEntity() == customEntity) {
                result.add(watcher);
            }
        }
        return result;
    }

    public EntityWatcher wrapEntity(Entity entity, String customId) {
        CustomEntity customEntity = getCustomEntity(customId);
        if (customEntity == null) throw new IllegalArgumentException("Unknown custom entity: " + customId);
        EntityWatcher entityWatcher = customEntity.createEntityWatcher(entity);
        storeCustomId(entity, customId);
        watchEntity(entityWatcher);
        return entityWatcher;
    }

    public EntityWatcher spawnEntity(Location location, String customId, Object conf) {
        CustomEntity customEntity = getCustomEntity(customId);
        if (customEntity == null) throw new IllegalArgumentException("Unknown custom entity: " + customId);
        Entity entity;
        if (conf != null) {
            entity = customEntity.spawnEntity(location, conf);
        } else {
            entity = customEntity.spawnEntity(location);
        }
        if (entity == null) return null;
        storeCustomId(entity, customId);
        EntityWatcher entityWatcher = customEntity.createEntityWatcher(entity);
        watchEntity(entityWatcher);
        customEntity.entityWasSpawned(entityWatcher);
        return entityWatcher;
    }

    public EntityWatcher spawnEntity(Location location, String customId) {
        return spawnEntity(location, customId, null);
    }

    /**
     * Remove an EntityWatcher from the framework.
     */
    public void removeEntityWatcher(EntityWatcher entityWatcher) {
        entityWatcher.getCustomEntity().entityWatcherWillUnregister(entityWatcher);
        entityWatcherMap.remove(entityWatcher.getEntity().getUniqueId());
        Entity entity = entityWatcher.getEntity();
        if (entity.isValid()) removeCustomId(entityWatcher.getEntity());
    }

    public List<CustomEntity> getRegisteredEntities() {
        return new ArrayList<>(customEntityMap.values());
    }

    // Utility for entity data stored in scoreboard tags

    public static Map<String, Object> loadEntityData(Entity entity, String key) {
        key = key + ":";
        Map<String, Object> result = new HashMap<>();
        for (String str: entity.getScoreboardTags()) {
            if (str.startsWith(key)) {
                String sub = str.substring(key.length());
                String[] toks = sub.split("=", 2);
                if (toks.length != 2) continue;
                String mapKey = toks[0];
                Object mapValue = Msg.fromJsonString(toks[1]);
                result.put(mapKey, mapValue);
            }
        }
        return result;
    }

    public static int removeEntityData(Entity entity, String key) {
        key = key + ":";
        List<String> removes = new ArrayList<>();
        for (String str: entity.getScoreboardTags()) {
            if (str.startsWith(key)) removes.add(str);
        }
        for (String remove: removes) {
            entity.removeScoreboardTag(remove);
        }
        return removes.size();
    }

    public static void saveEntityData(Entity entity, String key, Map<String, Object> map) {
        removeEntityData(entity, key);
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            entity.addScoreboardTag(key + ":" + entry.getKey() + "=" + Msg.toJsonString(entry.getValue()));
        }
    }

    // Internal use methods

    List<UUID> getWatchedEntities() {
        return new ArrayList<>(entityWatcherMap.keySet());
    }

    private void storeCustomId(Entity entity, String customId) {
        removeCustomId(entity);
        entity.addScoreboardTag(KEY_CUSTOM_ID + customId);
    }

    private void removeCustomId(Entity entity) {
        for (String tag: new ArrayList<>(entity.getScoreboardTags())) {
            if (tag.startsWith(KEY_CUSTOM_ID)) {
                entity.removeScoreboardTag(tag);
                break;
            }
        }
    }

    /**
     * Internal use only!
     *
     * Register an EntityWatcher with the framework.  It will be
     * returned by getEntityWatcher() and listen to all handled
     * events until it is unregistered with the function below.
     */
    public void watchEntity(EntityWatcher entityWatcher) {
        entityWatcherMap.put(entityWatcher.getEntity().getUniqueId(), entityWatcher);
        entityWatcher.getCustomEntity().entityWatcherDidRegister(entityWatcher);
    }

    /**
     * Internal use only!
     *
     * Forget about an entity, without attempting to delete its
     * customId.
     */
    void unwatchEntity(EntityWatcher entityWatcher) {
        entityWatcher.getCustomEntity().entityWatcherWillUnregister(entityWatcher);
        entityWatcherMap.remove(entityWatcher.getEntity().getUniqueId());
    }

    /**
     * Internal use only!
     *
     * Call the CustomRegisterEvent to give all clients a chance
     * to register their custom entities, then give it to this
     * function.
     */
    public void onCustomRegister(CustomRegisterEvent event) {
        customEntityMap.put(SimpleScriptEntity.CUSTOM_ID, new SimpleScriptEntity());
        for (CustomEntity customEntity: event.getEntities()) {
            if (customEntityMap.containsKey(customEntity.getCustomId())) {
                plugin.getLogger().warning("Entity Manager: Duplicate entity ID: " + customEntity.getCustomId());
            } else {
                customEntityMap.put(customEntity.getCustomId(), customEntity);
                plugin.getEventManager().registerEvents(customEntity);
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
        for (EntityWatcher entityWatcher: new ArrayList<>(entityWatcherMap.values())) {
            entityWatcher.getCustomEntity().entityWatcherWillUnregister(entityWatcher);
        }
        entityWatcherMap.clear();
    }
}

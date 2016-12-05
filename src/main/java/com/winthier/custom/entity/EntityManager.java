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
public class EntityManager {
    final CustomPlugin plugin;

    final Map<String, CustomEntity> customEntityMap = new HashMap<>();
    final Map<UUID, EntityWatcher> entityWatcherMap = new HashMap<>();

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomEntity customEntity: event.getEntities()) {
            if (customEntityMap.containsKey(customEntity.getCustomId())) {
                plugin.getLogger().warning("Entity Manager: Duplicate entity ID: " + customEntity.getCustomId());
            } else {
                customEntityMap.put(customEntity.getCustomId(), customEntity);
                plugin.getEventManager().registerEvents(customEntity);
                plugin.getLogger().info("Registered entity: " + customEntity.getCustomId());
            }
        }
    }

    public EntityWatcher getEntityWatcher(Entity entity) {
        UUID uuid = entity.getUniqueId();
        EntityWatcher result;
        result = entityWatcherMap.get(uuid);
        if (result != null) {
            if (!result.getEntity().isValid()) {
                entityWatcherMap.remove(uuid);
                // TODO call hook or event?
                return null;
            }
            return result;
        }
        CustomConfig config = CustomConfig.of(entity);
        if (config != null) {
            Location loc = entity.getLocation();
            plugin.getLogger().info(String.format("Found custom entity '%s' at %s,%d,%d,%d", config.getCustomId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            CustomEntity customEntity = findEntity(config);
            if (customEntity == null) {
                plugin.getLogger().warning("Encountered unknown custom entity '" + config.getCustomId() + "'");
                customEntity = new DefaultCustomEntity(config.getCustomId());
                customEntityMap.put(config.getCustomId(), customEntity);
                plugin.getEventManager().registerEvents(customEntity);
            }
            EntityWatcher watcher = customEntity.watchEntity(entity, config);
            if (watcher == null) watcher = new DefaultEntityWatcher(entity, customEntity, config);
            entityWatcherMap.put(uuid, watcher);
            return watcher;
        }
        return null;
    }

    public CustomEntity findEntity(CustomConfig config) {
        return findEntity(config.getCustomId());
    }

    public CustomEntity findEntity(String id) {
        return customEntityMap.get(id);
    }

    public void watchEntity(EntityWatcher watcher) {
        entityWatcherMap.put(watcher.getEntity().getUniqueId(), watcher);
    }
}

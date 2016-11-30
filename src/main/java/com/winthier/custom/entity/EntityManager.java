package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class EntityManager implements Listener {
    final CustomPlugin plugin;

    final Map<String, CustomEntity> customEntityMap = new HashMap<>();
    final Map<UUID, EntityWatcher> entityWatcherMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
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
            CustomEntity customEntity = customEntityMap.get(config.getCustomId());
            if (customEntity == null) {
                plugin.getLogger().warning("Encountered unknown custom entity with ID '" + config.getCustomId() + "'");
                customEntity = new DefaultCustomEntity(customEntity.getCustomId());
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
}

package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.item.CustomItem;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Listen for one specific event with one priority.
 */
@Getter @RequiredArgsConstructor
class EventDispatcher implements Listener, EventExecutor {
    final EventManager eventManager;
    final Class<? extends Event> event;
    final EventPriority priority;

    final Map<String, HandlerCaller> items = new HashMap<>();
    final Map<UUID, HandlerCaller> entities = new HashMap<>();
    ItemEventCaller itemEventCaller = null;
    EntityEventCaller entityEventCaller = null;

    void enable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(event, this, priority, this, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (!this.event.isInstance(event)) return; // Happens sometimes
        if (!items.isEmpty()) {
            if (itemEventCaller == null) itemEventCaller = ItemEventCaller.of(this, event);
            itemEventCaller.call(event);
        }
        if (!entities.isEmpty()) {
            if (entityEventCaller == null) entityEventCaller = EntityEventCaller.of(this, event);
            entityEventCaller.call(event);
        }
    }

    void registerEvent(Listener listener, Method method, boolean ignoreCancelled) {
        HandlerCaller caller = new HandlerCaller(event, listener, method, ignoreCancelled);
        if (listener instanceof CustomItem) {
            CustomItem customItem = (CustomItem)listener;
            items.put(customItem.getCustomId(), caller);
        }
        if (listener instanceof EntityWatcher) {
            EntityWatcher entityWatcher = (EntityWatcher)listener;
            entities.put(entityWatcher.getEntity().getUniqueId(), caller);
        }
    }

    void unregisterEvent(Listener listener) {
        if (listener instanceof EntityWatcher) {
            EntityWatcher entityWatcher = (EntityWatcher)listener;
            entities.remove(entityWatcher.getEntity().getUniqueId());
        }
    }

    void clear() {
        items.clear();
        entities.clear();
        itemEventCaller = null;
        entityEventCaller = null;
    }
}

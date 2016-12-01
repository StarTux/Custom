package com.winthier.custom.event;

import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.item.CustomItem;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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

    final List<HandlerCaller> itemCallers = new ArrayList<>();
    final List<HandlerCaller> entityCallers = new ArrayList<>();
    final List<HandlerCaller> systemCallers = new ArrayList<>(); // No custom thing
    ItemEventCaller itemEventCaller = null;
    EntityEventCaller entityEventCaller = null;

    void enable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(event, this, priority, this, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (!this.event.isInstance(event)) return; // Happens sometimes
        if (!itemCallers.isEmpty()) {
            if (itemEventCaller == null) itemEventCaller = ItemEventCaller.of(this, event);
            itemEventCaller.call(event);
        }
        if (!entityCallers.isEmpty()) {
            if (entityEventCaller == null) entityEventCaller = EntityEventCaller.of(this, event);
            entityEventCaller.call(event);
        }
        if (!systemCallers.isEmpty()) {
            for (HandlerCaller caller: systemCallers) caller.call(event);
        }
    }

    void registerEvent(Listener listener, Method method, boolean ignoreCancelled) {
        HandlerCaller caller = new HandlerCaller(event, listener, method, ignoreCancelled);
        if (listener instanceof CustomItem) {
            itemCallers.add(caller);
        } else if (listener instanceof CustomEntity) {
            entityCallers.add(caller);
        // } else if (listener instanceof CustomBlock) {
        } else {
            systemCallers.add(caller);
        }
    }

    void clear() {
        itemCallers.clear();
        entityCallers.clear();
        systemCallers.clear();
        itemEventCaller = null;
        entityEventCaller = null;
    }
}

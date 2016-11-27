package com.winthier.custom.event;

import com.winthier.custom.item.CustomItem;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
@RequiredArgsConstructor
class EventDispatcher implements Listener, EventExecutor {
    final EventManager manager;
    final Class<? extends Event> event;
    final EventPriority priority;

    final List<HandlerCaller> itemCallers = new ArrayList<>();
    ItemEventCaller itemEventCaller = null;

    void enable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(event, this, priority, this, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (!this.event.isInstance(event)) return; // Happens sometimes
        if (!itemCallers.isEmpty()) {
            if (itemEventCaller == null) itemEventCaller = ItemEventCaller.of(this, event);
            itemEventCaller.callEvent(event);
        }
    }

    void registerEvent(Listener listener, Method method, boolean ignoreCancelled) {
        HandlerCaller caller = new HandlerCaller(event, listener, method, ignoreCancelled);
        if (listener instanceof CustomItem) {
            itemCallers.add(caller);
        }
    }

    void clear() {
        itemCallers.clear();
    }
}

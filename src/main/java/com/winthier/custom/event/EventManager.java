package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.ItemContext;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Getter @RequiredArgsConstructor
public final class EventManager {
    private final CustomPlugin plugin;
    private final Map<Class<? extends Event>, Map<EventPriority, EventDispatcher>> eventMap = new HashMap<>();
    private final Map<Event, ItemContext> itemContextMap = new WeakHashMap<>();

    private EventDispatcher createEventDispatcher(Class<? extends Event> event, EventPriority priority) {
        EventDispatcher result = new EventDispatcher(this, event, priority);
        if (result != null) {
            result.enable(plugin);
        }
        return result;
    }

    /**
     * @return The dispatcher for a given event and priority,
     * or null one does not exist and cannot be created.
     */
    public EventDispatcher getEventDispatcher(Class<? extends Event> event, EventPriority priority) {
        Map<EventPriority, EventDispatcher> priorityMap;
        priorityMap = eventMap.get(event);
        if (priorityMap == null) {
            priorityMap = new EnumMap<EventPriority, EventDispatcher>(EventPriority.class);
            eventMap.put(event, priorityMap);
        }
        EventDispatcher dispatcher;
        dispatcher = priorityMap.get(priority);
        if (dispatcher == null) {
            dispatcher = createEventDispatcher(event, priority);
            if (dispatcher != null) priorityMap.put(priority, dispatcher);
        }
        return dispatcher;
    }

    public void registerEvents(Listener listener) {
        Class<?> clazz = listener.getClass();
        for (Method method : clazz.getMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) continue;
            // Get event class.
            Class<? extends Event> event;
            event = getEvent(method);
            if (event == null) {
                plugin.getLogger().warning("Bad event handler " + clazz.getName() + "." + method.getName() + "().");
                continue;
            }
            // Get dispatcher.
            EventDispatcher dispatcher = getEventDispatcher(event, annotation.priority());
            if (dispatcher == null) {
                plugin.getLogger().warning("Event dispatcher for " + event.getSimpleName() + " could not be created for " + listener.getClass().getSimpleName() + ".");
                continue;
            }
            dispatcher.registerEvent(listener, method, annotation.ignoreCancelled());
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Event> getEvent(Method method) {
        if (Void.TYPE != method.getReturnType()) return null;
        Class<?>[] params = method.getParameterTypes();
        if (params.length < 1) return null;
        if (!Event.class.isAssignableFrom(params[0])) return null;
        return (Class<? extends Event>)params[0];
    }

    public void clear() {
        for (Map<EventPriority, EventDispatcher> priorityMap : eventMap.values()) {
            for (EventDispatcher dispatcher : priorityMap.values()) {
                dispatcher.clear();
            }
        }
    }
}

package com.winthier.custom.event;

import java.lang.reflect.Method;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Call one EventHandler routine.
 */
final class HandlerCaller<L extends Listener> {
    final Class<? extends Event> eventClass;
    final L listener;
    final Method method;
    final boolean ignoreCancelled;
    final boolean withContextArg;

    HandlerCaller(Class<? extends Event> eventClass, L listener, Method method, boolean ignoreCancelled) {
        this.eventClass = eventClass;
        this.listener = listener;
        this.method = method;
        this.ignoreCancelled = ignoreCancelled;
        this.withContextArg = method.getParameterTypes().length >= 2;
    }

    void call(Event event, Object context) {
        if (ignoreCancelled && event instanceof Cancellable && ((Cancellable)event).isCancelled()) return;
        try {
            if (withContextArg) {
                method.invoke(listener, event, context);
            } else {
                method.invoke(listener, event);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void call(Event event) {
        call(event, null);
    }
}

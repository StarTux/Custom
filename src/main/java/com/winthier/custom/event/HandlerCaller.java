package com.winthier.custom.event;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Call one EventHandler routine.
 */
@RequiredArgsConstructor
public class HandlerCaller {
    final Class<? extends Event> event;
    final Listener listener;
    final Method method;
    final boolean ignoreCancelled;

    public void call(Event event) {
        try {
            method.invoke(listener, this.event.cast(event));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

package com.winthier.custom.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public final class CustomTickEvent extends Event {
    // Event Stuff
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // My Stuff

    public enum Type {
        WILL_TICK_ITEMS,
        DID_TICK_ITEMS,
        WILL_TICK_BLOCKS,
        DID_TICK_BLOCKS,
        WILL_TICK_ENTITIES,
        DID_TICK_ENTITIES;

        public CustomTickEvent call(int ticks) {
            CustomTickEvent event = new CustomTickEvent(this, ticks);
            Bukkit.getServer().getPluginManager().callEvent(event);
            return event;
        }
    }

    @Getter
    private final Type type;
    @Getter
    private final int ticks;
}

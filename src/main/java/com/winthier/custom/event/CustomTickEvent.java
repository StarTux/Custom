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
        WILL_TICK_BLOCKS,
        DID_TICK_BLOCKS;

        public CustomTickEvent call() {
            CustomTickEvent event = new CustomTickEvent(this);
            Bukkit.getServer().getPluginManager().callEvent(event);
            return event;
        }
    }

    @Getter
    private final Type type;
}

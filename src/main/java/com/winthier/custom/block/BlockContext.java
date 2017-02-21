package com.winthier.custom.block;

import com.winthier.custom.CustomPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;

@RequiredArgsConstructor
public final class BlockContext {
    public enum Position {
        /**
         * BlockEvent.getBlock()
         */
        BLOCK,
        /**
         * BlockFromToEvent.getToBlock()
         */
        TO,
        /**
         * *ExplodeEvent.blockList()
         */
        EXPLODE_LIST,
    }

    public final Position position;

    /**
     * Internal use only!
     */
    public void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getBlockContextMap().put(event, this);
    }

    /**
     * Internal use only!
     */
    public void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getBlockContextMap().remove(event);
    }

    /**
     * For public use.
     */
    public static BlockContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getBlockContextMap().get(event);
    }
}

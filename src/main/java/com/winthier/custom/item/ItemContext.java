package com.winthier.custom.item;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class ItemContext {
    public enum Position {
        HAND,
        OFF_HAND,
        ITEM, // Main item of the event
        ANVIL_LEFT,
        ANVIL_RIGHT,
        CRAFTING_MATRIX,
    }

    public final Player player;
    public final CustomItem customItem;
    public final ItemStack itemStack;
    public final Position position;
    public final CustomConfig config;

    /**
     * Internal use only!
     */
    public void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getItemContextMap().put(event, this);
    }

    /**
     * Internal use only!
     */
    public void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getItemContextMap().remove(event);
    }

    /**
     * For public use.
     */
    public static ItemContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getItemContextMap().get(event);
    }

    /**
     * For public use.
     */
    public static ItemContext of(ItemStack item) {
        return CustomPlugin.getInstance().getItemManager().getItemContext(item);
    }
}

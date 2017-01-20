package com.winthier.custom.item;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Getter @Setter @RequiredArgsConstructor
public class ItemContext {
    public static enum Position {
        HAND,
        OFF_HAND,
        ITEM, // Main item of the event
        ANVIL_LEFT,
        ANVIL_RIGHT,
        CRAFTING_MATRIX,
    }

    final Player player;
    final CustomItem customItem;
    final ItemStack itemStack;
    final Position position;
    final CustomConfig config;

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

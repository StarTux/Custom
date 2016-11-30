package com.winthier.custom.event;

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
public class ItemEventContext {
    final Player player;
    final ItemStack item;
    final EquipmentSlot hand;
    final CustomConfig config;

    void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getItemContextMap().put(event, this);
    }

    void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getItemContextMap().remove(event);
    }

    public static ItemEventContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getItemContextMap().get(event);
    }
}

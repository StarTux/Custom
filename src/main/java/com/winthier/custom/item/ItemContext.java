package com.winthier.custom.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter @RequiredArgsConstructor
public final class ItemContext {
    public enum Position {
        HAND,
        OFF_HAND,
        ITEM, // Main item of the event
        ANVIL_LEFT,
        ANVIL_RIGHT,
        CRAFTING_MATRIX,
    }

    private final ItemStack itemStack;
    private final CustomItem customItem;
    private final Player player;
    private final Position position;
}

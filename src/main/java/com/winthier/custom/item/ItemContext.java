package com.winthier.custom.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter @RequiredArgsConstructor
public final class ItemContext {
    public enum Position {
        ITEM, // Main item of the event
        HAND,
        OFF_HAND(40),
        HELMET(39),
        CHESTPLATE(38),
        LEGGINGS(37),
        BOOTS(36),
        ANVIL_LEFT,
        ANVIL_RIGHT,
        CRAFTING_MATRIX;

        public final int slot;

        Position() {
            this.slot = 0;
        }

        Position(int slot) {
            this.slot = slot;
        }

        static Position fromPlayerInventorySlot(int slot) {
            switch (slot) {
            case 36: return BOOTS;
            case 37: return LEGGINGS;
            case 38: return CHESTPLATE;
            case 39: return HELMET;
            case 40: return OFF_HAND;
            default: return ITEM;
            }
        }
    }

    private final ItemStack itemStack;
    private final CustomItem customItem;
    private final Player player;
    private final Position position;
    private final int slot;
}

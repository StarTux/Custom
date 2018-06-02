package com.winthier.custom.item;

import org.bukkit.inventory.ItemStack;

public interface UpdatableItem {
    ItemStack updateItem(ItemStack item);

    /**
     * Return the current version this item should have.  If and only
     * if the version stored differs, the updateItem() method is
     * called, and the stored version updated.
     */
    default int getUpdateVersion(ItemStack item) {
        return 0;
    }
}

package com.winthier.custom.item;

/**
 * CustomItem instances implementing this interface will have their
 * onTick() method called once per tick for every instance of
 * this item being found in a player inventory.
 */
public interface TickableItem {
    void onTick(ItemContext itemContext, int ticks);
}

package com.winthier.custom.item;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents a custom(ized) item type. it is used to
 * create item stacks of the type for in-game use, and respond to
 * Bukkit events pertaining to this type of item.
 *
 * Implement this interface, listen to ItemRegisterEvent and call
 * registerItem() with an instance of your class.
 *
 * It is highly recommended to subclass AbstractItem instead of
 * this interface, and only override the required methods.
 */
public interface Item {
    /**
     * Identifier to this Framework.  It will be put in the JSON
     * and then hidden in the lore.  It is recommended to leave
     * the heavy lifting to AbstractItem.getJson() and
     * AbstractItem.spawnItemStack().
     */
    String getId();
    
    // Creation

    /**
     * Create a new item stack with the given amount and JSON
     * information.
     */
    ItemStack spawnItemStack(int amount, ItemJson json);

    /**
     * Convenience functions for the above.
     */
    ItemStack spawnItemStack(int amount);
    ItemStack spawnItemStack();

    // Evant Handling

    /**
     * An event happened involving an item of this type.
     *
     * @return true If this event was handled by this
     * instance. The framework will not cancel it but rely on this
     * instance to have handled it properly.
     *
     * @return false If this event was not handled in any way. It
     * will be cancelled by the calling listener if possible.
     */
    boolean handleEvent(Event event, ItemContext itemContext);
}

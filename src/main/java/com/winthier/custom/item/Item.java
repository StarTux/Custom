package com.winthier.custom.item;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
    // Identifier to this Framework

    String getId();
    
    // Creation
    
    ItemStack spawnItemStack();

    // Appearance

    String getDisplayName();
    Material getMaterial();
    short getDurability();
    String getDescription();
    List<String> getLore();
    Map<Enchantment, Integer> getEnchantments();
    Map<String, Object> getJson();

    // Use

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

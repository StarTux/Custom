package com.winthier.custom.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

/**
 * Convenience interface to deny all crafty item manipulation events.
 * Add this interface to your CustomItem so players will be denied
 * from changing the name, enchantments, or crafting it into something
 * else, breaking the item in the process.
 */
public interface UncraftableItem {
    @EventHandler
    default void onEnchantItem(EnchantItemEvent event, ItemContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onPrepareItemEnchant(PrepareItemEnchantEvent event, ItemContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onCraftItem(CraftItemEvent event, ItemContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onPrepareAnvil(PrepareAnvilEvent event, ItemContext context) {
        event.setResult(null);
    }

    @EventHandler
    default void PrepareItemCraft(PrepareItemCraftEvent event, ItemContext context) {
        event.getInventory().setResult(null);
    }
}

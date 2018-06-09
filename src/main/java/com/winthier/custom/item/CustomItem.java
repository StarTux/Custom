package com.winthier.custom.item;

import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents a custom(ized) item type.  It is used to
 * create item stacks of the type for in-game use, and respond to
 * Bukkit events pertaining to this type of item.
 */
public interface CustomItem extends Listener {
    /**
     * Identifier to this Framework.
     */
    String getCustomId();

    // Creation

    /**
     * Create a new item stack with the given amount and JSON
     * information.  This method is not expected to register the item.
     * Instead the job is to be left to the caller.
     */
    ItemStack spawnItemStack(int amount);

    default void setItemData(ItemStack item, Map<String, Object> data) { }

    default void itemStackWasSpawned(ItemStack itemStack) { }

    default void handleMessage(ItemStack item, CommandSender sender, String[] msgs) { }
}

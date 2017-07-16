package com.winthier.custom.util;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Items {
    private Items() { };

    public static Item give(ItemStack itemStack, Player player) {
        Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
        if (item != null) {
            item.setPickupDelay(0);
            Msg.consoleCommand("minecraft:entitydata %s {Owner:%s}", item.getUniqueId(), player.getName());
        }
        return item;
    }
}

package com.winthier.custom.inventory;

import com.winthier.custom.CustomPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public interface CustomInventory {
    Inventory getInventory();
    default void onInventoryOpen(InventoryOpenEvent event) { }
    default void onInventoryClose(InventoryCloseEvent event) { }
    default void onInventoryInteract(InventoryInteractEvent event) { }
    default void onInventoryClick(InventoryClickEvent event) { }
    default void onInventoryDrag(InventoryDragEvent event) { }

    static void closeInventory(final Player player) {
        new BukkitRunnable() {
            @Override public void run() {
                if (!player.isValid()) return;
                player.closeInventory();
            }
        }.runTask(CustomPlugin.getInstance());
    }
}

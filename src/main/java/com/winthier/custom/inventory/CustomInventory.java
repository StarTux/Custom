package com.winthier.custom.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public interface CustomInventory {
    Inventory getInventory();
    void onInventoryOpen(InventoryOpenEvent event);
    void onInventoryClose(InventoryCloseEvent event);
    void onInventoryInteract(InventoryInteractEvent event);
    void onInventoryClick(InventoryClickEvent event);
    void onInventoryDrag(InventoryDragEvent event);
}

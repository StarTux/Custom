package com.winthier.custom.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class AbstractCustomInventory implements CustomInventory {
    @Override
    public void onInventoryOpen(InventoryOpenEvent event) {}

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {}

    @Override
    public void onInventoryInteract(InventoryInteractEvent event) {}

    @Override
    public void onInventoryClick(InventoryClickEvent event) {}

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {}
}

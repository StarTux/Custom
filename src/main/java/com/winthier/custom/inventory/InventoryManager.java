package com.winthier.custom.inventory;

import com.winthier.custom.CustomPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * The custom inventory package is a simple layer to open an
 * inventory on behalf of a player, listen for relevant events
 * while it is opened, and notice when it is closed.
 * CustomInventory instances are rudimentary and do not require
 * registration.
 */
@RequiredArgsConstructor
public final class InventoryManager implements Listener {
    private final CustomPlugin plugin;
    private final Map<UUID, CustomInventory> inventories = new HashMap<>();
    private final Map<UUID, CustomInventory> pending = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    void onInventoryOpen(InventoryOpenEvent event) {
        CustomInventory inv = pending.remove(event.getPlayer().getUniqueId());
        if (inv == null) return;
        inventories.put(event.getPlayer().getUniqueId(), inv);
        inv.onInventoryOpen(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    void onInventoryClose(InventoryCloseEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        CustomInventory inv = inventories.get(uuid);
        if (inv == null) return;
        try {
            inv.onInventoryClose(event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        inventories.remove(uuid);
    }

    @EventHandler(priority = EventPriority.LOW)
    void onInventoryInteract(InventoryInteractEvent event) {
        CustomInventory inv = inventories.get(event.getWhoClicked().getUniqueId());
        if (inv == null) return;
        event.setCancelled(true);
        inv.onInventoryInteract(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    void onInventoryClick(InventoryClickEvent event) {
        CustomInventory inv = inventories.get(event.getWhoClicked().getUniqueId());
        if (inv == null) return;
        event.setCancelled(true);
        inv.onInventoryClick(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    void onInventoryDrag(InventoryDragEvent event) {
        CustomInventory inv = inventories.get(event.getWhoClicked().getUniqueId());
        if (inv == null) return;
        event.setCancelled(true);
        inv.onInventoryDrag(event);
    }

    public void onDisable() {
        for (Map.Entry<UUID, CustomInventory> entry: inventories.entrySet()) {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            if (player != null) player.closeInventory();
        }
        inventories.clear();
    }

    public void openInventory(Player player, CustomInventory inv) {
        pending.put(player.getUniqueId(), inv);
        player.openInventory(inv.getInventory());
    }
}

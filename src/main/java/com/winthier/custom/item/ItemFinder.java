package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class ItemFinder implements Listener {
    private final CustomPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isValid() || !player.isOnline()) return;
                updateInventory(event.getPlayer().getInventory());
                updateInventory(event.getPlayer().getEnderChest());
            }
        }.runTask(plugin);
    }

    void updateInventory(Inventory inventory) {
        for (ItemStack itemStack: inventory) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            CustomItem customItem = plugin.getItemManager().getCustomItem(itemStack);
            if (customItem == null) continue;
            if (customItem instanceof UpdatableItem) {
                ((UpdatableItem)customItem).updateItem(itemStack);
            }
        }
    }
}

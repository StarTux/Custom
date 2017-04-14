package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class ItemCrawler extends BukkitRunnable {
    private final ItemManager itemManager;

    void start() {
        try {
            runTaskTimer(CustomPlugin.getInstance(), 1, 1);
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    void stop() {
        try {
            cancel();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (Player player: Bukkit.getServer().getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            int heldItemSlot = inv.getHeldItemSlot();
            for (int i = 0; i < inv.getSize(); i += 1) {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getAmount() == 0) continue;
                CustomItem customItem = itemManager.getCustomItem(item);
                if (customItem == null) continue;
                if (customItem instanceof TickableItem) {
                    ItemContext.Position position;
                    if (i == heldItemSlot) {
                        position = ItemContext.Position.HAND;
                    } else {
                        position = ItemContext.Position.fromPlayerInventorySlot(i);
                    }
                    ItemContext context = new ItemContext(item, customItem, player, position, i);
                    ((TickableItem)customItem).onTick(context);
                }
            }
        }
    }
}

package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomTickEvent;
import com.winthier.custom.util.Dirty;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class ItemCrawler extends BukkitRunnable {
    private final ItemManager itemManager;
    private int ticks = 0;

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
        CustomTickEvent.Type.WILL_TICK_ITEMS.call(ticks);
        for (Player player: Bukkit.getServer().getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();
            int heldItemSlot = inv.getHeldItemSlot();
            for (int i = 0; i < inv.getSize(); i += 1) {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getAmount() == 0) continue;
                CustomItem customItem = itemManager.getCustomItem(item);
                if (customItem == null) continue;
                if (customItem instanceof UpdatableItem) {
                    UpdatableItem updatable = (UpdatableItem)customItem;
                    int updateVersion = updatable.getUpdateVersion(item);
                    if (updateVersion > 0) {
                        Dirty.TagWrapper conf = Dirty.TagWrapper.getItemConfigOf(item);
                        int itemVersion = conf.getInt("UpdateVersion");
                        if (itemVersion != updateVersion) {
                            updatable.updateItem(item);
                            conf.setInt("UpdateVersion", updateVersion);
                        }
                    }
                }
                if (customItem instanceof TickableItem) {
                    ItemContext.Position position;
                    if (i == heldItemSlot) {
                        position = ItemContext.Position.HAND;
                    } else {
                        position = ItemContext.Position.fromPlayerInventorySlot(i);
                    }
                    ItemContext context = new ItemContext(item, customItem, player, position, i);
                    ((TickableItem)customItem).onTick(context, ticks);
                }
            }
        }
        ticks += 1;
        CustomTickEvent.Type.DID_TICK_ITEMS.call(ticks);
    }
}

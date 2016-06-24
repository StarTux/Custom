package com.winthier.custom;

import com.winthier.custom.item.Item;
import com.winthier.custom.item.ItemListener;
import com.winthier.custom.item.ItemRegistry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class CustomPlugin extends JavaPlugin {
    @Getter static CustomPlugin instance = null;
    final ItemRegistry itemRegistry = new ItemRegistry();
    
    @Override
    public void onEnable() {
        instance = this;
        getCommand("custom").setExecutor(new CustomCommand());
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        new BukkitRunnable() {
            @Override public void run() {
                itemRegistry.reload();
            }
        }.runTask(this);
    }

    public Item findItem(String id) {
        return getItemRegistry().findItem(id);
    }

    public ItemStack spawnItem(String id) {
        Item item = getItemRegistry().findItem(id);
        if (item == null) return null;
        return item.spawnItemStack();
    }

    public boolean giveItem(Player player, String itemId) {
        Item item = getItemRegistry().findItem(itemId);
        if (item == null) return false;
        for (ItemStack drop: player.getInventory().addItem(item.spawnItemStack()).values()) {
            player.getWorld().dropItem(player.getEyeLocation(), drop).setPickupDelay(0);
        }
        return true;
    }
}

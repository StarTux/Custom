package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {
    final static int ANVIL_INPUT_SLOT_1 = 0;
    final static int ANVIL_INPUT_SLOT_2 = 1;
    final static int ANVIL_OUTPUT_SLOT = 2;

    Boolean handleEvent(Event event, ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemContext itemContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(itemStack);
        if (itemContext == null) return null;
        boolean result = itemContext.item.handleEvent(event, itemContext);
        if (!result && event instanceof Cancellable) {
            ((Cancellable)event).setCancelled(true);
        }
        return result;
    }

    ItemStack getItem(Player player, EquipmentSlot slot) {
        switch (slot) {
        case HAND: return player.getInventory().getItemInMainHand();
        case OFF_HAND: return player.getInventory().getItemInOffHand();
        default: return null;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        handleEvent(event, getItem(event.getPlayer(), event.getHand()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        handleEvent(event, getItem(event.getPlayer(), event.getHand()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchantItem(EnchantItemEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        Boolean ret;
        ret = handleEvent(event, event.getInventory().getItem(ANVIL_INPUT_SLOT_1));
        if (ret != null && ret == false) event.setResult(null);
        ret = handleEvent(event, event.getInventory().getItem(ANVIL_INPUT_SLOT_2));
        if (ret != null && ret == false) event.setResult(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        handleEvent(event, ((Player)event.getDamager()).getInventory().getItemInMainHand());
    }
}

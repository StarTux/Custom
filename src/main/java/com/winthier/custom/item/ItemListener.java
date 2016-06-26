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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
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

    ItemContext handleEvent(Event event, ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemContext itemContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(itemStack);
        if (itemContext == null) return null;
        itemContext.setHandled(itemContext.item.handleEvent(event, itemContext));
        if (!itemContext.isHandled() && event instanceof Cancellable) {
            ((Cancellable)event).setCancelled(true);
        }
        return itemContext;
    }

    void setItem(Player player, EquipmentSlot slot, ItemStack item) {
        switch (slot) {
        case HAND:
            player.getInventory().setItemInMainHand(item);
            return;
        case OFF_HAND:
            player.getInventory().setItemInOffHand(item);
            return;
        }
    }

    ItemStack getItem(Player player, EquipmentSlot slot) {
        switch (slot) {
        case HAND: return player.getInventory().getItemInMainHand();
        case OFF_HAND: return player.getInventory().getItemInOffHand();
        default: return null;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemContext itemContext = handleEvent(event, event.getItem());
        if (itemContext != null && itemContext.isHandled()) {
            if (itemContext.getReplaceItem() != null) {
                setItem(event.getPlayer(), event.getHand(), itemContext.getReplaceItem());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        handleEvent(event, getItem(event.getPlayer(), event.getHand()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        handleEvent(event, getItem(event.getPlayer(), event.getHand()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchantItem(EnchantItemEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        handleEvent(event, event.getItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemContext ret;
        ret = handleEvent(event, event.getInventory().getItem(ANVIL_INPUT_SLOT_1));
        if (ret != null && !ret.isHandled()) {
            event.setResult(null);
            return;
        }
        ret = handleEvent(event, event.getInventory().getItem(ANVIL_INPUT_SLOT_2));
        if (ret != null && !ret.isHandled()) {
            event.setResult(null);
            return;
        }
    }

    // Apparently it is enough to set the result in this event,
    // and the subsequent crafting event will produce it. Problem
    // is, the prepare event is only called when a recipe has been
    // recognized, and recipes can only take material and
    // durability; not lore and other data.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        for (CraftingRecipe craftingRecipe: CustomPlugin.getInstance().getItemRegistry().getRegisteredRecipes()) {
            if (craftingRecipe.matches(event.getInventory().getMatrix())) {
                event.getInventory().setResult(craftingRecipe.getResultItem().spawnItemStack(1));
                return;
            }
        }
        // Just cancel if a custom item is found in the crafting matrix
        for (ItemStack itemStack: event.getInventory().getMatrix()) {
            if (itemStack == null) continue;
            ItemContext itemContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(itemStack);
            if (itemContext == null) continue;
            event.getInventory().setResult(null);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        handleEvent(event, ((Player)event.getDamager()).getInventory().getItemInMainHand());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        handleEvent(event, event.getBow());
    }
}

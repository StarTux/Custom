package com.winthier.custom.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Default version of CustomItem that cancels all relevant
 * events.  Use this and overwrite any event that you would like
 * to handle yourself or let pass through.
 */
@Getter @RequiredArgsConstructor
public class DefaultCustomItem implements CustomItem {
    private final String customId;

    @Override
    public ItemStack spawnItemStack(int amount) {
        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDamage(BlockDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShootBow(EntityShootBowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        event.setResult(null);
    }
}

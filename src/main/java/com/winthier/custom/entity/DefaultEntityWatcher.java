package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

@Getter @RequiredArgsConstructor
public class DefaultEntityWatcher implements EntityWatcher {
    final Entity entity;
    final CustomEntity customEntity;
    final CustomConfig customConfig;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityPortal(EntityPortalEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityTeleport(EntityTeleportEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onCreeperPower(CreeperPowerEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityCombust(EntityCombustEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDismount(EntityDismountEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityInteract(EntityInteractEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityMount(EntityMountEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityShootBow(EntityShootBowEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityTame(EntityTameEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityUnleash(EntityUnleashEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPigZap(PigZapEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSheepDyeWool(SheepDyeWoolEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSheepRegrowWool(SheepRegrowWoolEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSlimeSplit(SlimeSplitEvent event) {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent event) {
    }
}

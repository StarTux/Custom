package com.winthier.custom.entity;

import com.winthier.custom.CustomPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

/**
 * An instance of this class will attempt to find new entities in
 * the wild.  It does so by listening to all conceivable
 * EntityEvents and PlayerEvents with the lowest priority, and
 * ChunkLoadEvent with monitor priority.
 *
 * Furthermore, it will try to identify discovered entities which
 * are about to be unloaded and call the appropriate hooks, by
 * listening to the ChunkUnloadEvent.
 */
@Getter @RequiredArgsConstructor
public class EntityFinder implements Listener {
    final CustomPlugin plugin;

    private void findEntity(Entity entity) {
        if (entity == null) return;
        EntityManager entityManager = plugin.getEntityManager();
        entityManager.getEntityWatcher(entity);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        findEntity(event.getRightClicked());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        findEntity(event.getRightClicked());
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        findEntity(event.getEntity());
        findEntity(event.getDamager());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityPortal(EntityPortalEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityTeleport(EntityTeleportEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onCreeperPower(CreeperPowerEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityCombust(EntityCombustEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityInteract(EntityInteractEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityMount(EntityMountEvent event) {
        findEntity(event.getMount());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityDismount(EntityDismountEvent event) {
        findEntity(event.getDismounted());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityShootBow(EntityShootBowEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityTame(EntityTameEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetEvent event) {
        findEntity(event.getEntity());
        findEntity(event.getTarget());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onEntityUnleash(EntityUnleashEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPigZap(PigZapEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onSheepDyeWool(SheepDyeWoolEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onSheepRegrowWool(SheepRegrowWoolEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onSlimeSplit(SlimeSplitEvent event) {
        findEntity(event.getEntity());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPotionSplash(PotionSplashEvent event) {
        findEntity(event.getEntity());
        for (Entity entity: event.getAffectedEntities()) findEntity(entity);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        findEntity(event.getItem());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        findEntity(event.getItem());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity: event.getChunk().getEntities()) findEntity(entity);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        EntityManager entityManager = plugin.getEntityManager();
        if (entityManager == null) return;
        for (Entity entity: event.getChunk().getEntities()) {
            EntityWatcher entityWatcher = entityManager.getEntityWatcher(entity);
            if (entityWatcher == null) continue;
            entityWatcher.entityWillUnload();
            entityManager.removeEntity(entityWatcher);
            entityWatcher.entityDidUnload();
        }
    }
}

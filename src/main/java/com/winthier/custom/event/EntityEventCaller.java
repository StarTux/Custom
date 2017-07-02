package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityContext.Position;
import com.winthier.custom.entity.EntityContext;
import com.winthier.custom.entity.EntityWatcher;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

@RequiredArgsConstructor
abstract class EntityEventCaller {
    private final EventDispatcher dispatcher;

    abstract void call(Event event);

    protected void callWithEntity(Event event, Entity entity, Position position) {
        if (entity == null) return;
        EntityWatcher entityWatcher = CustomPlugin.getInstance().getEntityManager().getEntityWatcher(entity);
        if (entityWatcher == null) return;
        CustomEntity customEntity = entityWatcher.getCustomEntity();
        HandlerCaller<CustomEntity> handlerCaller = dispatcher.getEntities().get(customEntity.getCustomId());
        if (handlerCaller == null) return;
        EntityContext context = new EntityContext(entity, customEntity, entityWatcher, position);
        handlerCaller.call(event, context);
    }

    protected void callWithEntity(Event event, Entity entity) {
        callWithEntity(event, entity, Position.ENTITY);
    }

    static EntityEventCaller of(EventDispatcher dispatcher, Class<? extends Event> eventClass) {
        if (EntityDamageByEntityEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ev;
                    callWithEntity(event, event.getEntity());
                    callWithEntity(event, ((EntityDamageByEntityEvent)event).getDamager(), Position.DAMAGER);
                }
            };
        } else if (EntityMountEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityMountEvent event = (EntityMountEvent)ev;
                    callWithEntity(event, event.getEntity());
                    callWithEntity(event, ((EntityMountEvent)event).getMount(), Position.MOUNT);
                }
            };
        } else if (EntityDismountEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityDismountEvent event = (EntityDismountEvent)ev;
                    callWithEntity(event, event.getEntity());
                    callWithEntity(event, ((EntityDismountEvent)event).getDismounted(), Position.MOUNT);
                }
            };
        } else if (PotionSplashEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    PotionSplashEvent event = (PotionSplashEvent)ev;
                    callWithEntity(event, event.getEntity());
                    for (LivingEntity affected: event.getAffectedEntities()) {
                        callWithEntity(event, affected, Position.SPLASHED);
                    }
                }
            };
        } else if (AreaEffectCloudApplyEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    AreaEffectCloudApplyEvent event = (AreaEffectCloudApplyEvent)ev;
                    callWithEntity(event, event.getEntity());
                    for (LivingEntity affected: event.getAffectedEntities()) {
                        callWithEntity(event, affected, Position.SPLASHED);
                    }
                }
            };
        } else if (ProjectileHitEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    ProjectileHitEvent event = (ProjectileHitEvent)ev;
                    callWithEntity(event, event.getEntity());
                    Entity hitEntity = ((ProjectileHitEvent)event).getHitEntity();
                    if (hitEntity != null) callWithEntity(event, hitEntity, Position.PROJECTILE_TARGET);
                }
            };
        } else if (EntityEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityEvent event = (EntityEvent)ev;
                    callWithEntity(event, event.getEntity());
                }
            };
        } else if (PlayerInteractEntityEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    callWithEntity(ev, ((PlayerInteractEntityEvent)ev).getRightClicked());
                }
            };
        } else if (ChunkEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    for (Entity entity: ((ChunkEvent)ev).getChunk().getEntities()) {
                        callWithEntity(ev, entity);
                    }
                }
            };
        } else if (InventoryPickupItemEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    InventoryPickupItemEvent event = (InventoryPickupItemEvent)ev;
                    callWithEntity(event, event.getItem());
                }
            };
        } else if (PlayerPickupItemEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerPickupItemEvent event = (PlayerPickupItemEvent)ev;
                    callWithEntity(event, event.getItem());
                }
            };
        } else if (HangingBreakByEntityEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    HangingBreakByEntityEvent event = (HangingBreakByEntityEvent)ev;
                    callWithEntity(event, event.getEntity());
                    callWithEntity(event, event.getRemover(), Position.DAMAGER);
                }
            };
        } else if (HangingEvent.class.isAssignableFrom(eventClass)) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    HangingEvent event = (HangingEvent)ev;
                    callWithEntity(event, event.getEntity());
                }
            };
        } else {
            CustomPlugin.getInstance().getLogger().warning("No EntityEventCaller found for " + eventClass.getName());
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    // Do nothing
                }
            };
        }
    }
}

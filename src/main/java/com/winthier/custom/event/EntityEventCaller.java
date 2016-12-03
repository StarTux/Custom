package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

@RequiredArgsConstructor
abstract class EntityEventCaller {
    final EventDispatcher dispatcher;

    abstract void call(Event event);

    protected void callWithEntity(Event event, Entity entity, EntityEventContext.Position position) {
        if (entity == null) return;
        EntityWatcher entityWatcher = CustomPlugin.getInstance().getEntityManager().getEntityWatcher(entity);
        if (entityWatcher == null) return;
        EntityEventContext context = new EntityEventContext(position, entity, entityWatcher);
        context.save(event);
        CustomEntity customEntity = entityWatcher.getCustomEntity();
        for (HandlerCaller caller: dispatcher.getEntityCallers()) {
            if (caller.listener == customEntity) {
                caller.call(event);
            }
        }
        context.remove(event);
    }

    protected void callWithEntity(Event event, Entity entity) {
        callWithEntity(event, entity, EntityEventContext.Position.ENTITY);
    }
    
    static EntityEventCaller of(EventDispatcher dispatcher, Event event) {
        if (event instanceof EntityEvent) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityEvent event = (EntityEvent)ev;
                    callWithEntity(event, event.getEntity());
                    // We have to check subclasses in the final
                    // class because we could get unlucky and get
                    // the subclass on the first call when we were
                    // expecting a superclass.
                    // Perhaps replace instanceof with the Class
                    // check?
                    if (event instanceof EntityDamageByEntityEvent) {
                        callWithEntity(event, ((EntityDamageByEntityEvent)event).getDamager(), EntityEventContext.Position.DAMAGER);
                    } else if (event instanceof EntityMountEvent) {
                        callWithEntity(event, ((EntityMountEvent)event).getMount(), EntityEventContext.Position.MOUNT);
                    } else if (event instanceof EntityDismountEvent) {
                        callWithEntity(event, ((EntityDismountEvent)event).getDismounted(), EntityEventContext.Position.MOUNT);
                    } else if (event instanceof ProjectileHitEvent) {
                        Entity hitEntity = ((ProjectileHitEvent)event).getHitEntity();
                        if (hitEntity != null) callWithEntity(event, hitEntity, EntityEventContext.Position.PROJECTILE_TARGET);
                        if (event instanceof PotionSplashEvent) {
                            PotionSplashEvent splashEvent = (PotionSplashEvent)event;
                            for (LivingEntity affected: splashEvent.getAffectedEntities()) {
                                callWithEntity(event, affected, EntityEventContext.Position.SPLASHED);
                            }
                        }
                    }
                }
            };
        } else if (event instanceof PlayerInteractEntityEvent) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    callWithEntity(event, ((PlayerInteractEntityEvent)ev).getRightClicked());
                }
            };
        } else if (event instanceof ChunkEvent) {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    for (Entity entity: ((ChunkEvent)ev).getChunk().getEntities()) {
                        callWithEntity(event, entity);
                    }
                }
            };
        } else if (event instanceof InventoryPickupItemEvent) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    InventoryPickupItemEvent event = (InventoryPickupItemEvent)ev;
                    callWithEntity(event, event.getItem());
                }
            };
        } else if (event instanceof PlayerPickupItemEvent) {
            return new EntityEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerPickupItemEvent event = (PlayerPickupItemEvent)ev;
                    callWithEntity(event, event.getItem());
                }
            };
        } else {
            CustomPlugin.getInstance().getLogger().warning("No EntityEventCaller found for " + event.getEventName());
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    // Do nothing
                }
            };
        }
    }
}

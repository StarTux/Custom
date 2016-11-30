package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;

@RequiredArgsConstructor
abstract class EntityEventCaller {
    final EventDispatcher dispatcher;

    abstract void call(Event event);

    protected void callWithEntity(Event event, Entity entity, EntityEventContext.Position position) {
        if (entity == null) return;
        EntityWatcher entityWatcher = CustomPlugin.getInstance().getEntityManager().getEntityWatcher(entity);
        if (entityWatcher == null) return;
        EntityEventContext context = new EntityEventContext(position, entity, entityWatcher);
        dispatcher.getEventManager().getEntityContextMap().put(event, context);
        for (HandlerCaller caller: dispatcher.getEntityCallers()) {
            caller.call(event);
        }
        dispatcher.getEventManager().getEntityContextMap().remove(event);
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
                    }
                }
            };
        } else {
            return new EntityEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    // Do nothing
                }
            };
        }
    }
}

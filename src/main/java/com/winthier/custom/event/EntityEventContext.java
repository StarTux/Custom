package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.EntityWatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

@Getter @RequiredArgsConstructor
public class EntityEventContext {
    public static enum Position {
        ENTITY,
        DAMAGER, // EntityDamageByEntityEvent.getDamager()
        MOUNT, // Entity(Mount/Dismount)Event.get(Mount/Dismounted)()
        SPLASHED, // PotionSplashEvent.getAffectedEntities()
    }

    final Position position;
    final Entity entity;
    final EntityWatcher entityWatcher;

    void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().put(event, this);
    }

    void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().remove(event);
    }

    public static EntityEventContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getEntityContextMap().get(event);
    }
}

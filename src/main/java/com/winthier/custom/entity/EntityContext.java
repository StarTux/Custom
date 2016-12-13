package com.winthier.custom.entity;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.EntityWatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

@Getter @RequiredArgsConstructor
public class EntityContext {
    public static enum Position {
        ENTITY,
        DAMAGER, // EntityDamageByEntityEvent.getDamager()
        MOUNT, // Entity(Mount/Dismount)Event.get(Mount/Dismounted)()
        PROJECTILE_TARGET, // ProjectileHitEvent.getHitEntity()
        SPLASHED, // PotionSplashEvent.getAffectedEntities()
    }

    final Position position;

    public void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().put(event, this);
    }

    public void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().remove(event);
    }

    public static EntityContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getEntityContextMap().get(event);
    }
}

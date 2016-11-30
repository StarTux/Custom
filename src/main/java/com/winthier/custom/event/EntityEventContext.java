package com.winthier.custom.event;

import com.winthier.custom.entity.EntityWatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;

@Getter @RequiredArgsConstructor
public class EntityEventContext {
    static enum Position {
        ENTITY,
        DAMAGER, // EntityDamageByEntityEvent.getDamager()
    }

    final Position position;
    final Entity entity;
    final EntityWatcher entityWatcher;
}

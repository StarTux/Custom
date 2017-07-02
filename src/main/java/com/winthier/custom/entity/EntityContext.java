package com.winthier.custom.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;

/**
 * Store additional event information not contained in the event
 * itself.  The EntityEventCaller will create an instance of this
 * class for each event and give it to the EventHandler method as the
 * second parameter.
 */
@Getter @RequiredArgsConstructor
public class EntityContext {
    /**
     * The position of the customized entity.  Most EntityEvents have
     * nothing but the main entity found in getEntity(), in which case
     * this will return ENTITY.  However, some events pertain to more
     * than one entity, in which case it can be unclear which entity
     * the specific event call is referring to.
     */
    public enum Position {
        /** EntityEvent.getEntity() */
        ENTITY,
        /** EntityDamageByEntityEvent.getDamager()
         *  HangingBreakByEntityEvent.getRemover() */
        DAMAGER,
        /** Entity(Mount/Dismount)Event.get(Mount/Dismounted)() */
        MOUNT,
        /** ProjectileHitEvent.getHitEntity() */
        PROJECTILE_TARGET,
        /** PotionSplashEvent.getAffectedEntities()
            AreaEffectCloudApplyEvent.getAffectedEntities() */
        SPLASHED,
    }

    private final Entity entity;
    private final CustomEntity customEntity;
    private final EntityWatcher entityWatcher;
    private final Position position;
}

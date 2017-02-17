package com.winthier.custom.entity;

import com.winthier.custom.CustomPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;

/**
 * Store additional event information not contained in the event
 * itself.  The EventManager or its worker classes will create an
 * instance of this class for each event and store it.  The
 * CustomEntity can retrieve it within the EventHandler by calling
 * the static of() method.
 */
@Getter @RequiredArgsConstructor
public class EntityContext {
    /**
     * The position of the customized entity.  Most EntityEvents
     * have nothing but the main entity found in getEntity(), in
     * which case this will return ENTITY.  However, some events
     * pertain to more than one entity, in which case it can be
     * unclear what is happening.  Use EntityContext.getPosition()
     * to make it clear.
     */
    public enum Position {
        /**
         * EntityEvent.getEntity()
         */
        ENTITY,
        /**
         * EntityDamageByEntityEvent.getDamager()
         */
        DAMAGER,
        /**
         * Entity(Mount/Dismount)Event.get(Mount/Dismounted)()
         */
        MOUNT,
        /**
         * ProjectileHitEvent.getHitEntity()
         */
        PROJECTILE_TARGET,
        /**
         * PotionSplashEvent.getAffectedEntities()
         */
        SPLASHED,
    }

    final Position position;

    /**
     * Internal use only!
     */
    public void save(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().put(event, this);
    }

    /**
     * Internal use only!
     */
    public void remove(Event event) {
        CustomPlugin.getInstance().getEventManager().getEntityContextMap().remove(event);
    }

    /**
     * Retrieve the stored context for the given event.
     */
    public static EntityContext of(Event event) {
        return CustomPlugin.getInstance().getEventManager().getEntityContextMap().get(event);
    }
}

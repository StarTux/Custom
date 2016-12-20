package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * This is the default CustomEntity implementation which will be
 * used for all entities which are discovered in the world that
 * have a custom ID stored for which there is not corresponding
 * CustomEntity registered.  It will return the
 * DefaultEntityWatcher which attempts to cancel all event in an
 * attempt to keep the entity unchanged.
 */
@Getter @RequiredArgsConstructor
public class DefaultCustomEntity implements CustomEntity {
    final String customId;

    @Override
    public Entity spawnEntity(Location location, CustomConfig config) {
        return null;
    }

    @Override
    public EntityWatcher createEntityWatcher(Entity entity, CustomConfig config) {
        return new DefaultEntityWatcher(entity, this, config);
    }
}

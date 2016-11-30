package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter @RequiredArgsConstructor
public class DefaultCustomEntity implements CustomEntity {
    final String customId;

    @Override
    public Entity spawnEntity(Location location, CustomConfig config) {
        return null;
    }

    @Override
    public EntityWatcher watchEntity(Entity entity, CustomConfig config) {
        return null; // Use DefaultCustomEntity
    }
}

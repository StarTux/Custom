package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.entity.CustomEntity;
import org.bukkit.entity.Entity;

public interface EntityWatcher {
    Entity getEntity();
    CustomEntity getCustomEntity();
    CustomConfig getCustomConfig();
}

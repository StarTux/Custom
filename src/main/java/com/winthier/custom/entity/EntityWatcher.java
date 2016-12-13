package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.entity.CustomEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public interface EntityWatcher extends Listener {
    Entity getEntity();
    CustomEntity getCustomEntity();
    CustomConfig getCustomConfig();
}

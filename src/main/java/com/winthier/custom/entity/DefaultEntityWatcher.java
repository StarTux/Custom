package com.winthier.custom.entity;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;

@Getter @RequiredArgsConstructor
public class DefaultEntityWatcher implements EntityWatcher {
    final Entity entity;
    final CustomEntity customEntity;
    final CustomConfig customConfig;
}

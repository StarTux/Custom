package com.winthier.custom.entity;

public abstract class AbstractEntityWatcher implements EntityWatcher {
    @Override
    public void didDiscoverEntity() {}

    @Override
    public void willUnloadEntity() {}
}

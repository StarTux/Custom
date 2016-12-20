package com.winthier.custom.entity;

/**
 * An abstract version of EntityWatcher which overrides all
 * optional hooks with empty method bodies.  Extend this class
 * rather than implementing the interface to save yourself some
 * trouble.
 */
public abstract class AbstractEntityWatcher implements EntityWatcher {
    @Override
    public void didSpawnEntity() {}

    @Override
    public void didDiscoverEntity() {}

    @Override
    public void entityWillUnload() {}

    @Override
    public void entityDidUnload() {}
}

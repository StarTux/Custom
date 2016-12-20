package com.winthier.custom.block;

public abstract class AbstractBlockWatcher implements BlockWatcher {
    @Override
    public void didDiscoverBlock() {}

    @Override
    public void blockWillUnload() {}
}

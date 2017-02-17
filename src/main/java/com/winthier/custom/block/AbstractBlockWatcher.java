package com.winthier.custom.block;

public abstract class AbstractBlockWatcher implements BlockWatcher {
    @Override
    public void blockWasDiscovered() { }

    @Override
    public void blockWillUnload() { }

    @Override
    public void blockWillDisappear() { }

    @Override
    public void blockDidDisappear() { }
}

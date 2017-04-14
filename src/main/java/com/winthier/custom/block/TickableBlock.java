package com.winthier.custom.block;

/**
 * CustomBlocks implementing TickableBlock will have their onTick()
 * method called once per tick while the block is loaded and within
 * range of any online player.
 */
public interface TickableBlock {
    void onTick(BlockWatcher blockWatcher);
}

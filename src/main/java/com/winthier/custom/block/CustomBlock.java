package com.winthier.custom.block;

import org.bukkit.block.Block;
import org.bukkit.event.Listener;

public interface CustomBlock extends Listener {
    String getCustomId();

    default void setBlock(Block block) { }

    default BlockWatcher createBlockWatcher(Block block) {
        final CustomBlock customBlock = this;
        return new BlockWatcher() {
            @Override public Block getBlock() {
                return block;
            }
            @Override public CustomBlock getCustomBlock() {
                return customBlock;
            }
        };
    }

    default void blockWasCreated(BlockWatcher watcher) { }

    default void blockWasLoaded(BlockWatcher watcher) { }

    default void blockWillUnload(BlockWatcher watcher) { }

    default void blockWasRemoved(BlockWatcher watcher) { }
}

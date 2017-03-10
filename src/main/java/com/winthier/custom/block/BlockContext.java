package com.winthier.custom.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
public final class BlockContext {
    public enum Position {
        /**
         * BlockEvent.getBlock()
         */
        BLOCK,
        /**
         * BlockFromToEvent.getToBlock()
         */
        TO,
        /**
         * *ExplodeEvent.blockList()
         */
        EXPLODE_LIST,
    }

    private final Block block;
    private final CustomBlock customBlock;
    private final BlockWatcher blockWatcher;
    private final Position position;
}

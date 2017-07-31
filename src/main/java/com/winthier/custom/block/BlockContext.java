package com.winthier.custom.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
public final class BlockContext {
    public enum Position {
        /** BlockEvent.getBlock() */
        BLOCK,
        /** BlockFromToEvent.getToBlock() */
        TO,
        /** ExplodeEvent.blockList() */
        EXPLODE_LIST,
        /** PistonExtendEvent.getBlocks() */
        PISTON_EXTEND_LIST,
        /** PistonRetractEvent.getBlocks() */
        PISTON_RETRACT_LIST,
        /** BlockSpreadEvent.getSource() */
        SPREAD_SOURCE,
    }

    private final Block block;
    private final CustomBlock customBlock;
    private final BlockWatcher blockWatcher;
    private final Position position;

    @Override
    public String toString() {
        return "BlockContext(" + block + ", " + customBlock.getClass().getName() + ", " + blockWatcher.getClass().getName() + ", " + position + ")";
    }
}

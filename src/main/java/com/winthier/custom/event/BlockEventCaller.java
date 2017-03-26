package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.block.BlockContext.Position;
import com.winthier.custom.block.BlockContext;
import com.winthier.custom.block.BlockWatcher;
import com.winthier.custom.block.CustomBlock;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
abstract class BlockEventCaller {
    private final EventDispatcher dispatcher;
    abstract void call(Event event);

    protected void callWithBlock(Event event, Block block, Position position) {
        if (block == null) return;
        // Find the BlockWatcher via BlockManager, which will load
        // it if necessary.
        BlockWatcher blockWatcher = CustomPlugin.getInstance().getBlockManager().getBlockWatcher(block);
        if (blockWatcher == null) return;
        CustomBlock customBlock = blockWatcher.getCustomBlock();
        HandlerCaller<CustomBlock> handlerCaller = dispatcher.getBlocks().get(customBlock.getCustomId());
        if (handlerCaller == null) return;
        BlockContext blockContext = new BlockContext(block, customBlock, blockWatcher, position);
        handlerCaller.call(event, blockContext);
    }

    static BlockEventCaller of(EventDispatcher dispatcher, Class<? extends Event> eventClass) {
        if (BlockFromToEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockFromToEvent event = (BlockFromToEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                    callWithBlock(event, event.getToBlock(), Position.TO);
                }
            };
        } else if (BlockExplodeEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockExplodeEvent event = (BlockExplodeEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                    for (Block block: new ArrayList<>(event.blockList())) {
                        callWithBlock(event, block, Position.EXPLODE_LIST);
                    }
                }
            };
        } else if (EntityExplodeEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityExplodeEvent event = (EntityExplodeEvent)ev;
                    for (Block block: new ArrayList<>(event.blockList())) {
                        callWithBlock(event, block, Position.EXPLODE_LIST);
                    }
                }
            };
        } else if (BlockPistonExtendEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockPistonExtendEvent event = (BlockPistonExtendEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                    for (Block block: event.getBlocks()) {
                        callWithBlock(event, block, Position.PISTON_EXTEND_LIST);
                    }
                }
            };
        } else if (BlockPistonRetractEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockPistonRetractEvent event = (BlockPistonRetractEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                    for (Block block: event.getBlocks()) {
                        callWithBlock(event, block, Position.PISTON_RETRACT_LIST);
                    }
                }
            };
        } else if (BlockSpreadEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockSpreadEvent event = (BlockSpreadEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                    callWithBlock(event, event.getSource(), Position.SPREAD_SOURCE);
                }
            };
        } else if (BlockDispenseEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockDispenseEvent event = (BlockDispenseEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                }
            };
        } else if (BlockEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockEvent event = (BlockEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                }
            };
        } else if (PlayerInteractEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    PlayerInteractEvent event = (PlayerInteractEvent)ev;
                    if (!event.hasBlock()) return;
                    callWithBlock(event, event.getClickedBlock(), Position.BLOCK);
                }
            };
        } else if (EntityChangeBlockEvent.class.isAssignableFrom(eventClass)) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityChangeBlockEvent event = (EntityChangeBlockEvent)ev;
                    callWithBlock(event, event.getBlock(), Position.BLOCK);
                }
            };
        } else {
            CustomPlugin.getInstance().getLogger().warning("No BlockEventCaller found for " + eventClass.getName());
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    // Do nothing
                }
            };
        }
    }
}

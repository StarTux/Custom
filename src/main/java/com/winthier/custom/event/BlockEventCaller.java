package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.block.BlockWatcher;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
abstract class BlockEventCaller {
    final EventDispatcher dispatcher;
    abstract void call(Event event);

    void callWithBlock(Event event, Block block) {
        if (block == null) return;
        // Find the BlockWatcher via BlockManager, which will load
        // it if necessary.
        BlockWatcher blockWatcher = CustomPlugin.getInstance().getBlockManager().getBlockWatcher(block);
        if (blockWatcher == null) return;
        // Fetch the registered handler caller which is now
        // guaranteed to be registered if the BlockWatcher is
        // listening for this event.
        HandlerCaller<BlockWatcher> handlerCaller = dispatcher.blocks.get(block);
        if (handlerCaller == null) return;
        if (handlerCaller.listener != blockWatcher) return;
        handlerCaller.call(event);
    }

    static BlockEventCaller of(EventDispatcher dispatcher, Event event) {
        if (event instanceof BlockEvent) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    BlockEvent event = (BlockEvent)ev;
                    callWithBlock(event, event.getBlock());
                }
            };
        } else if (event instanceof PlayerInteractEvent) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    PlayerInteractEvent event = (PlayerInteractEvent)ev;
                    if (!event.hasBlock()) return;
                    callWithBlock(event, event.getClickedBlock());
                }
            };
        } else if (event instanceof EntityChangeBlockEvent) {
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    EntityChangeBlockEvent event = (EntityChangeBlockEvent)ev;
                    callWithBlock(event, event.getBlock());
                }
            };
        } else {
            CustomPlugin.getInstance().getLogger().warning("No BlockEventCaller found for " + event.getEventName());
            return new BlockEventCaller(dispatcher) {
                @Override void call(Event ev) {
                    // Do nothing
                }
            };
        }
    }
}
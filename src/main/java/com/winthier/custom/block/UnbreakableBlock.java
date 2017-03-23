package com.winthier.custom.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public interface UnbreakableBlock {
    @EventHandler
    default void onBlockBreak(BlockBreakEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockBurn(BlockBurnEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockDamage(BlockDamageEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockExplode(BlockExplodeEvent event, BlockContext context) {
        event.blockList().remove(context.getBlock());
    }

    @EventHandler
    default void onBlockFade(BlockFadeEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockForm(BlockFormEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockFromTo(BlockFromToEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockGrow(BlockGrowEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockIgnite(BlockIgniteEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockPhysics(BlockPhysicsEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockPistonExtend(BlockPistonExtendEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockPistonRectract(BlockPistonRetractEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onBlockSpread(BlockSpreadEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onCauldronLevelChange(CauldronLevelChangeEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onEntityBlockForm(EntityBlockFormEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onLeavesDecay(LeavesDecayEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onEntityChangeBlock(EntityChangeBlockEvent event, BlockContext context) {
        event.setCancelled(true);
    }

    @EventHandler
    default void onEntityExplode(EntityExplodeEvent event, BlockContext context) {
        event.blockList().remove(context.getBlock());
    }
}

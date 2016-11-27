package com.winthier.custom.event;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
abstract class ItemEventCaller {
    final EventDispatcher dispatcher;
    abstract void callEvent(Event event);

    protected void callWithItemInHand(Event event, Player player, EquipmentSlot slot) {
        ItemStack item;
        if (slot == EquipmentSlot.HAND) {
            item = player.getInventory().getItemInMainHand();
        } else if (slot == EquipmentSlot.OFF_HAND) {
            item = player.getInventory().getItemInOffHand();
        } else {
            return;
        }
        CustomConfig config = CustomConfig.of(item);
        if (config == null) return;
        CustomItem customItem = CustomPlugin.getInstance().getItemRegistry().findItem(config);
        if (customItem == null) return;
        for (HandlerCaller caller: dispatcher.itemCallers) {
            if (caller.listener == customItem) {
                caller.call(event);
            }
        }
    }

    static ItemEventCaller of(EventDispatcher dispatcher, Event event) {
        if (event instanceof PlayerInteractEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    PlayerInteractEvent event = (PlayerInteractEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof PlayerInteractEntityEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof EntityDamageByEntityEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ev;
                    if (!(event.getDamager() instanceof Player)) return;
                    callWithItemInHand(event, (Player)event.getDamager(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockDamageEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    BlockDamageEvent event = (BlockDamageEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockBreakEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    BlockBreakEvent event = (BlockBreakEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockPlaceEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    BlockPlaceEvent event = (BlockPlaceEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof EntityShootBowEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event ev) {
                    EntityShootBowEvent event = (EntityShootBowEvent)ev;
                    if (!(event.getEntity() instanceof Player)) return;
                    Player player = (Player)event.getEntity();
                    EquipmentSlot hand;
                    if (event.getBow().equals(player.getInventory().getItemInMainHand())) {
                        hand = EquipmentSlot.HAND;
                    } else if (event.getBow().equals(player.getInventory().getItemInOffHand())) {
                        hand = EquipmentSlot.OFF_HAND;
                    } else {
                        player.sendMessage("OOO");
                        return; // ???
                    }
                    callWithItemInHand(event, player, hand);
                }
            };
        } else {
            return new ItemEventCaller(dispatcher) {
                @Override public void callEvent(Event event) {
                    // Do nothing
                }
            };
        }
    }
}

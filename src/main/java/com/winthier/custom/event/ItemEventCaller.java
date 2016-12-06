package com.winthier.custom.event;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
abstract class ItemEventCaller {
    final EventDispatcher dispatcher;
    abstract void call(Event event);

    protected void callWithItemInHand(Event event, Player player, EquipmentSlot hand) {
        ItemStack item;
        ItemEventContext.Position position;
        if (hand == EquipmentSlot.HAND) {
            item = player.getInventory().getItemInMainHand();
            position = ItemEventContext.Position.HAND;
        } else if (hand == EquipmentSlot.OFF_HAND) {
            item = player.getInventory().getItemInOffHand();
            position = ItemEventContext.Position.OFF_HAND;
        } else {
            return;
        }
        if (item == null || item.getType() == Material.AIR) return;
        CustomConfig config = CustomConfig.of(item);
        if (config == null) return;
        CustomItem customItem = CustomPlugin.getInstance().getItemManager().getItem(config);
        if (customItem == null) return;
        ItemEventContext context = new ItemEventContext(player, item, position, config);
        context.save(event);
        for (HandlerCaller caller: dispatcher.itemCallers) {
            if (caller.listener == customItem) {
                caller.call(event);
            }
        }
        context.remove(event);
    }

    // Items not in anyone's hand
    protected void callWithItem(Event event, Player player, ItemStack item, ItemEventContext.Position position) {
        if (item == null || item.getType() == Material.AIR) return;
        CustomConfig config = CustomConfig.of(item);
        if (config == null) return;
        CustomItem customItem = CustomPlugin.getInstance().getItemManager().getItem(config);
        if (customItem == null) return;
        ItemEventContext context = new ItemEventContext(player, item, position, config);
        context.save(event);
        for (HandlerCaller caller: dispatcher.itemCallers) {
            if (caller.listener == customItem) {
                caller.call(event);
            }
        }
        context.remove(event);
    }    

    static ItemEventCaller of(EventDispatcher dispatcher, Event event) {
        if (event instanceof PlayerInteractEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerInteractEvent event = (PlayerInteractEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof PlayerInteractEntityEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof EntityDamageByEntityEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ev;
                    if (!(event.getDamager() instanceof Player)) return;
                    callWithItemInHand(event, (Player)event.getDamager(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockDamageEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockDamageEvent event = (BlockDamageEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockBreakEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockBreakEvent event = (BlockBreakEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (event instanceof BlockPlaceEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockPlaceEvent event = (BlockPlaceEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (event instanceof EntityShootBowEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
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
        } else if (event instanceof PlayerSwapHandItemsEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerSwapHandItemsEvent event = (PlayerSwapHandItemsEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.OFF_HAND);
                }
            };
        } else if (event instanceof InventoryPickupItemEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    InventoryPickupItemEvent event = (InventoryPickupItemEvent)ev;
                    callWithItem(event, null, event.getItem().getItemStack(), ItemEventContext.Position.ITEM);
                }
            };
        } else if (event instanceof PlayerPickupItemEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerPickupItemEvent event = (PlayerPickupItemEvent)ev;
                    callWithItem(event, event.getPlayer(), event.getItem().getItemStack(), ItemEventContext.Position.ITEM);
                }
            };
        } else if (event instanceof EnchantItemEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EnchantItemEvent event = (EnchantItemEvent)ev;
                    callWithItem(event, event.getEnchanter(), event.getItem(), ItemEventContext.Position.ITEM);
                }
            };
        } else if (event instanceof PrepareItemEnchantEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareItemEnchantEvent event = (PrepareItemEnchantEvent)ev;
                    callWithItem(event, event.getEnchanter(), event.getItem(), ItemEventContext.Position.ITEM);
                }
            };
        } else if (event instanceof PrepareAnvilEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareAnvilEvent event = (PrepareAnvilEvent)ev;
                    Player player = event.getViewers().isEmpty() ? null : (Player)event.getViewers().get(0);
                    callWithItem(event, player, event.getInventory().getItem(0), ItemEventContext.Position.ANVIL_LEFT);
                    callWithItem(event, player, event.getInventory().getItem(1), ItemEventContext.Position.ANVIL_RIGHT);
                }
            };
        } else if (event instanceof PrepareItemCraftEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareItemCraftEvent event = (PrepareItemCraftEvent)ev;
                    InventoryHolder holder = event.getInventory().getHolder();
                    Player player = holder instanceof Player ? (Player)holder : null;
                    for (ItemStack item: event.getInventory().getMatrix()) {
                        callWithItem(event, player, item, ItemEventContext.Position.CRAFTING_MATRIX);
                    }
                }
            };
        } else if (event instanceof CraftItemEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    CraftItemEvent event = (CraftItemEvent)ev;
                    InventoryHolder holder = event.getInventory().getHolder();
                    Player player = holder instanceof Player ? (Player)holder : null;
                    for (ItemStack item: event.getInventory().getMatrix()) {
                        callWithItem(event, player, item, ItemEventContext.Position.CRAFTING_MATRIX);
                    }
                }
            };
        } else if (event instanceof PlayerFishEvent) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerFishEvent event = (PlayerFishEvent)ev;
                    Player player = event.getPlayer();
                    if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                        callWithItemInHand(event, player, EquipmentSlot.HAND);
                    } else if (player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD) {
                        callWithItemInHand(event, player, EquipmentSlot.OFF_HAND);
                    }
                }
            };
        } else {
            CustomPlugin.getInstance().getLogger().warning("No ItemEventCaller found for " + event.getEventName());
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event event) {
                    // Do nothing
                }
            };
        }
    }
}

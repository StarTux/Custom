package com.winthier.custom.event;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemContext.Position;
import com.winthier.custom.item.ItemContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
abstract class ItemEventCaller {
    private final EventDispatcher dispatcher;
    abstract void call(Event event);

    protected final void callWithItemInHand(Event event, Player player, EquipmentSlot hand) {
        ItemStack item;
        Position position;
        int slot;
        if (hand == EquipmentSlot.HAND) {
            item = player.getInventory().getItemInMainHand();
            position = Position.HAND;
            slot = player.getInventory().getHeldItemSlot();
        } else if (hand == EquipmentSlot.OFF_HAND) {
            item = player.getInventory().getItemInOffHand();
            position = Position.OFF_HAND;
            slot = 40;
        } else {
            return;
        }
        if (item == null || item.getType() == Material.AIR) return;
        CustomItem customItem = CustomPlugin.getInstance().getItemManager().getCustomItem(item);
        if (customItem == null) return;
        HandlerCaller handlerCaller = dispatcher.getItems().get(customItem.getCustomId());
        if (handlerCaller == null) return;
        ItemContext context = new ItemContext(item, customItem, player, position, slot);
        handlerCaller.call(event, context);
    }

    // Items not in anyone's hand
    protected final void callWithItem(Event event, Player player, ItemStack item, Position position) {
        if (item == null || item.getType() == Material.AIR) return;
        CustomItem customItem = CustomPlugin.getInstance().getItemManager().getCustomItem(item);
        if (customItem == null) return;
        HandlerCaller handlerCaller = dispatcher.getItems().get(customItem.getCustomId());
        if (handlerCaller == null) return;
        int slot = position == Position.HAND ? player.getInventory().getHeldItemSlot() : position.slot;
        ItemContext context = new ItemContext(item, customItem, player, position, slot);
        handlerCaller.call(event, context);
    }

    static ItemEventCaller of(EventDispatcher dispatcher, Class<? extends Event> eventClass) {
        if (PlayerInteractEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerInteractEvent event = (PlayerInteractEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (PlayerInteractEntityEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (EntityDamageByEntityEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ev;
                    if (event.getDamager() instanceof Player) {
                        callWithItemInHand(event, (Player)event.getDamager(), EquipmentSlot.HAND);
                    }
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player)event.getEntity();
                        callWithItem(event, player, player.getInventory().getHelmet(), Position.HELMET);
                        callWithItem(event, player, player.getInventory().getChestplate(), Position.CHESTPLATE);
                        callWithItem(event, player, player.getInventory().getLeggings(), Position.LEGGINGS);
                        callWithItem(event, player, player.getInventory().getBoots(), Position.BOOTS);
                    }
                }
            };
        } else if (EntityDamageEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EntityDamageEvent event = (EntityDamageEvent)ev;
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player)event.getEntity();
                        callWithItem(event, player, player.getInventory().getHelmet(), Position.HELMET);
                        callWithItem(event, player, player.getInventory().getChestplate(), Position.CHESTPLATE);
                        callWithItem(event, player, player.getInventory().getLeggings(), Position.LEGGINGS);
                        callWithItem(event, player, player.getInventory().getBoots(), Position.BOOTS);
                    }
                }
            };
        } else if (BlockDamageEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockDamageEvent event = (BlockDamageEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (BlockBreakEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockBreakEvent event = (BlockBreakEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                }
            };
        } else if (BlockPlaceEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockPlaceEvent event = (BlockPlaceEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), event.getHand());
                }
            };
        } else if (EntityShootBowEvent.class.isAssignableFrom(eventClass)) {
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
                        return;
                    }
                    callWithItemInHand(event, player, hand);
                }
            };
        } else if (PlayerSwapHandItemsEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerSwapHandItemsEvent event = (PlayerSwapHandItemsEvent)ev;
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.HAND);
                    callWithItemInHand(event, event.getPlayer(), EquipmentSlot.OFF_HAND);
                }
            };
        } else if (InventoryPickupItemEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    InventoryPickupItemEvent event = (InventoryPickupItemEvent)ev;
                    callWithItem(event, null, event.getItem().getItemStack(), Position.ITEM);
                }
            };
        } else if (PlayerPickupItemEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerPickupItemEvent event = (PlayerPickupItemEvent)ev;
                    callWithItem(event, event.getPlayer(), event.getItem().getItemStack(), Position.ITEM);
                }
            };
        } else if (EnchantItemEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EnchantItemEvent event = (EnchantItemEvent)ev;
                    callWithItem(event, event.getEnchanter(), event.getItem(), Position.ITEM);
                }
            };
        } else if (PrepareItemEnchantEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareItemEnchantEvent event = (PrepareItemEnchantEvent)ev;
                    callWithItem(event, event.getEnchanter(), event.getItem(), Position.ITEM);
                }
            };
        } else if (PrepareAnvilEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareAnvilEvent event = (PrepareAnvilEvent)ev;
                    Player player = event.getViewers().isEmpty() ? null : (Player)event.getViewers().get(0);
                    callWithItem(event, player, event.getInventory().getItem(0), Position.ANVIL_LEFT);
                    callWithItem(event, player, event.getInventory().getItem(1), Position.ANVIL_RIGHT);
                }
            };
        } else if (PrepareItemCraftEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PrepareItemCraftEvent event = (PrepareItemCraftEvent)ev;
                    InventoryHolder holder = event.getInventory().getHolder();
                    Player player = holder instanceof Player ? (Player)holder : null;
                    for (ItemStack item: event.getInventory().getMatrix()) {
                        callWithItem(event, player, item, Position.CRAFTING_MATRIX);
                    }
                }
            };
        } else if (CraftItemEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    CraftItemEvent event = (CraftItemEvent)ev;
                    InventoryHolder holder = event.getInventory().getHolder();
                    Player player = holder instanceof Player ? (Player)holder : null;
                    for (ItemStack item: event.getInventory().getMatrix()) {
                        callWithItem(event, player, item, Position.CRAFTING_MATRIX);
                    }
                }
            };
        } else if (PlayerFishEvent.class.isAssignableFrom(eventClass)) {
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
        } else if (PlayerItemConsumeEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerItemConsumeEvent event = (PlayerItemConsumeEvent)ev;
                    final Player player = event.getPlayer();
                    EquipmentSlot hand;
                    if (event.getItem().equals(player.getInventory().getItemInMainHand())) {
                        hand = EquipmentSlot.HAND;
                    } else if (event.getItem().equals(player.getInventory().getItemInOffHand())) {
                        hand = EquipmentSlot.OFF_HAND;
                    } else {
                        return;
                    }
                    callWithItemInHand(event, player, hand);
                }
            };
        } else if (BlockDispenseEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    BlockDispenseEvent event = (BlockDispenseEvent)ev;
                    callWithItem(event, null, event.getItem(), Position.ITEM);
                }
            };
        } else if (PlayerItemDamageEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    PlayerItemDamageEvent event = (PlayerItemDamageEvent)ev;
                    callWithItem(event, event.getPlayer(), event.getItem(), Position.ITEM);
                }
            };
        } else if (EntityToggleGlideEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    EntityToggleGlideEvent event = (EntityToggleGlideEvent)ev;
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player)event.getEntity();
                        callWithItem(event, player, player.getEquipment().getChestplate(), Position.CHESTPLATE);
                    }
                }
            };
        } else if (InventoryClickEvent.class.isAssignableFrom(eventClass)) {
            return new ItemEventCaller(dispatcher) {
                @Override public void call(Event ev) {
                    InventoryClickEvent event = (InventoryClickEvent)ev;
                    if (event.getWhoClicked() instanceof Player) {
                        Player player = (Player)event.getWhoClicked();
                        callWithItem(event, player, event.getCurrentItem(), Position.ITEM);
                    }
                }
            };
        } else {
            Method method = null;
            try {
                method = eventClass.getMethod("getItem");
            } catch (NoSuchMethodException nsme) { }
            try {
                if (method == null) method = eventClass.getMethod("getItemStack");
            } catch (NoSuchMethodException nsme) { }
            if (method != null && ItemStack.class.isAssignableFrom(method.getReturnType())) {
                final Method getterMethod = method;
                return new ItemEventCaller(dispatcher) {
                    @Override public void call(Event ev) {
                        try {
                            ItemStack item = (ItemStack)getterMethod.invoke(ev);
                            callWithItem(ev, null, item, Position.ITEM);
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        } catch (InvocationTargetException ite) {
                            ite.printStackTrace();
                        }
                    }
                };
            } else {
                CustomPlugin.getInstance().getLogger().warning("No ItemEventCaller found for " + eventClass.getName());
                return new ItemEventCaller(dispatcher) {
                    @Override public void call(Event event) {
                        // Do nothing
                    }
                };
            }
        }
    }
}

package com.winthier.custom;

import com.winthier.custom.block.BlockWatcher;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.inventory.CustomInventory;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.util.Dirty;
import com.winthier.custom.util.Items;
import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
final class CustomCommand implements TabExecutor {
    private final CustomPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = sender instanceof Player ? (Player)sender : null;
        if (args.length == 0) return false;
        String firstArg = args[0].toLowerCase();
        if (firstArg.equals("give") && args.length >= 3) {
            String targetName = args[1];
            Player target = Bukkit.getServer().getPlayer(targetName);
            if (target == null) {
                Msg.warn(sender, "Player not found: %s.", targetName);
                return true;
            }
            String customId = args[2];
            int amount = 1;
            if (args.length >= 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException nfe) {
                    Msg.warn(sender, "Number expected: %s.", args[3]);
                    return true;
                }
                if (amount < 1) amount = 1;
            }
            Map<String, Object> data = null;
            if (args.length >= 5) {
                StringBuilder sb = new StringBuilder(args[4]);
                for (int i = 5; i < args.length; i += 1) {
                    sb.append(" ").append(args[i]);
                }
                String json = sb.toString();
                Object o = Msg.parseJson(json);
                if (o == null || !(o instanceof Map)) {
                    Msg.warn(sender, "Invalid JSON: %s", json);
                    return true;
                }
                @SuppressWarnings("unchecked")
                final Map<String, Object> tmp = (Map<String, Object>)o;
                data = tmp;
            }
            ItemStack item = CustomPlugin.getInstance().getItemManager().spawnItemStack(customId, amount, data);
            Items.give(item, target);
            if (data != null) {
                Msg.info(sender, "Item %s spawned for %s with %s.", customId, target.getName(), Msg.toJsonString(data));
            } else {
                Msg.info(sender, "Item %s spawned for %s.", customId, target.getName());
            }
        } else if (firstArg.equals("summon") && args.length >= 2) {
            if (player == null) {
                Msg.warn(sender, "Player expected");
                return true;
            }
            String customId = args[1];
            EntityWatcher entityWatcher;
            if (args.length < 3) {
                entityWatcher = plugin.getEntityManager().spawnEntity(player.getLocation(), customId);
            } else {
                StringBuilder sb = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i += 1) {
                    sb.append(" ").append(args[i]);
                }
                Object conf = Msg.parseJson(sb.toString());
                if (conf == null) {
                    Msg.warn(sender, "Invalid JSON: " + sb.toString());
                    return true;
                }
                entityWatcher = plugin.getEntityManager().spawnEntity(player.getLocation(), customId, conf);
            }
            if (entityWatcher == null) {
                Msg.warn(sender, "Failed to spawn custom entity: %s.", customId);
                return true;
            }
            Msg.info(sender, "Custom entity spawned: %s.", customId);
        } else if (firstArg.equals("setblock") && args.length == 2) {
            String customId = args[1];
            Block block = player.getLocation().getBlock();
            BlockWatcher blockWatcher = plugin.getBlockManager().setBlock(block, customId);
            Msg.info(player, "Custom block '%s' created at: %d %d %d", blockWatcher.getCustomBlock().getCustomId(), block.getX(), block.getY(), block.getZ());
        } else if (firstArg.equals("removeblock") && args.length == 1) {
            Block block = player.getLocation().getBlock();
            BlockWatcher blockWatcher = plugin.getBlockManager().getBlockWatcher(block);
            if (blockWatcher == null) {
                Msg.info(player, "No custom block found at: %d %d %d!", block.getX(), block.getY(), block.getZ());
            } else {
                plugin.getBlockManager().removeBlockWatcher(blockWatcher);
                Msg.info(player, "Custom block '%s' removed at: %d %d %d", blockWatcher.getCustomBlock().getCustomId(), block.getX(), block.getY(), block.getZ());
            }
        } else if (firstArg.equals("getblock") && args.length == 1) {
            Block block = player.getLocation().getBlock();
            BlockWatcher blockWatcher = plugin.getBlockManager().getBlockWatcher(block);
            if (blockWatcher == null) {
                Msg.warn(player, "No custom block found: %d %d %d", block.getX(), block.getY(), block.getZ());
                return true;
            }
            Msg.info(player, "Custom block found: %s %s", blockWatcher.getCustomBlock().getCustomId(), plugin.getBlockManager().loadBlockData(blockWatcher));
        } else if (firstArg.equals("reload") && args.length == 1) {
            plugin.reload();
            Msg.info(sender, "Custom Plugin Reloaded");
        } else if (firstArg.equals("items")) {
            final List<String> items = new ArrayList<>();
            for (CustomItem item: plugin.getItemManager().getRegisteredItems()) items.add(item.getCustomId());
            Collections.sort(items);
            if (items.isEmpty()) {
                sender.sendMessage("Nothing found.");
                return true;
            }
            StringBuilder sb = new StringBuilder().append(items.size()).append(" CustomItems:");
            for (String item: items) sb.append(" ").append(item);
            sender.sendMessage(sb.toString());
            if (player != null) {
                int size = ((items.size() - 1) / 9 + 1) * 9;
                final Inventory inventory = plugin.getServer().createInventory(player, size, "Custom Items");
                for (String item: items) inventory.addItem(plugin.getItemManager().spawnItemStack(item, 1));
                plugin.getInventoryManager().openInventory(player, new CustomInventory() {
                        @Override public Inventory getInventory() {
                            return inventory;
                        }
                        @Override public void onInventoryClick(InventoryClickEvent event) {
                            if (!event.getInventory().equals(inventory)) return;
                            int index = event.getSlot();
                            if (index < 0 || index >= items.size()) return;
                            String customId = items.get(index);
                            int stackSize = event.isShiftClick() ? inventory.getItem(index).getType().getMaxStackSize() : 1;
                            ItemStack item = plugin.getItemManager().spawnItemStack(customId, stackSize);
                            Items.give(item, player);
                            player.sendMessage("Spawned " + customId + ".");
                        }
                    });
            }
        } else if (firstArg.equals("entitymessage") && args.length >= 2) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            List<String> entityList = new ArrayList<>();
            String[] msgs = Arrays.copyOfRange(args, 1, args.length);
            for (Entity e: player.getNearbyEntities(4.0, 4.0, 4.0)) {
                EntityWatcher watcher = plugin.getEntityManager().getEntityWatcher(e);
                if (watcher != null) {
                    entityList.add(watcher.getCustomEntity().getCustomId());
                    watcher.handleMessage(player, msgs);
                }
            }
            StringBuilder sb = new StringBuilder("Sent to ").append(entityList.size()).append(" entities:");
            for (String entity: entityList) sb.append(" ").append(entity);
            player.sendMessage(sb.toString());
        } else if (firstArg.equals("itemmessage") && args.length >= 2) {
            if (player == null) {
                sender.sendMessage("Player expected");
                return true;
            }
            ItemStack item = player.getInventory().getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                sender.sendMessage("No item in hand");
                return true;
            }
            CustomItem customItem = plugin.getItemManager().getCustomItem(item);
            if (customItem == null) {
                sender.sendMessage("Item is not custom");
                return true;
            }
            customItem.handleMessage(item, sender, Arrays.copyOfRange(args, 1, args.length));
            player.sendMessage("Message sent to item " + customItem.getCustomId() + " in hand.");
        } else if (firstArg.equals("debug")) {
            Object o = Dirty.getItemTag(player.getInventory().getItemInMainHand());
            player.sendMessage("Item tag: (" + o + ")");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "summon", "getblock", "setblock", "reload", "items", "entitymessage", "itemmessage", "debug").stream().filter(i -> i.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equals("give")) {
            return plugin.getItemManager().getRegisteredItems().stream().map(i -> i.getCustomId()).filter(i -> i.startsWith(args[2])).collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equals("summon")) {
            return plugin.getEntityManager().getRegisteredEntities().stream().map(i -> i.getCustomId()).filter(i -> i.startsWith(args[1])).collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equals("setblock")) {
            return plugin.getBlockManager().getRegisteredBlocks().stream().map(i -> i.getCustomId()).filter(i -> i.startsWith(args[1])).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}

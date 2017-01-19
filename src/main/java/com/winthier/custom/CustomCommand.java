package com.winthier.custom;

import com.winthier.custom.block.BlockWatcher;
import com.winthier.custom.block.CustomBlock;
import com.winthier.custom.block.DefaultBlockWatcher;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.DefaultEntityWatcher;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.util.Msg;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class CustomCommand implements CommandExecutor {
    final CustomPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player)sender : null;
        if (args.length == 0) return false;
        String firstArg = args[0].toLowerCase();
        if (firstArg.equals("give") && args.length >= 3) {
            String targetName = args[1];
            Player target = Bukkit.getServer().getPlayer(targetName);
            if (target == null) {
                Msg.warn(sender, "Player not found: %s.", targetName);
                return true;
            }
            String itemId = args[2];
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
            CustomItem customItem = CustomPlugin.getInstance().getItemManager().findItem(itemId);
            if (customItem == null) {
                Msg.warn(player, "Item not found: %s.", itemId);
                return true;
            }
            CustomConfig config = new CustomConfig(itemId, (String)null);
            ItemStack item = customItem.spawnItemStack(amount, config);
            item = config.save(item);
            target.getWorld().dropItemNaturally(target.getEyeLocation(), item).setPickupDelay(0);
            Msg.info(player, "Item spawned for %s.", target.getName());
        } else if (firstArg.equals("summon") && args.length == 2) {
            String name = args[1];
            CustomEntity customEntity = plugin.getEntityManager().getCustomEntity(name);
            if (customEntity == null) {
                Msg.warn(sender, "Custom entity not found: %s.", name);
                return true;
            }
            CustomConfig config = new CustomConfig(customEntity.getCustomId(), (String)null);
            Entity entity = customEntity.spawnEntity(player.getLocation(), config);
            if (entity == null) {
                Msg.warn(sender, "Failed to spawn custom entity: %s.", name);
                return true;
            }
            config.save(entity);
            EntityWatcher watcher = customEntity.createEntityWatcher(entity, config);
            if (watcher == null) watcher = new DefaultEntityWatcher(entity, customEntity, config);
            plugin.getEntityManager().watchEntity(watcher);
            Msg.info(sender, "Custom entity spawned: %s.", customEntity.getCustomId());
        } else if (firstArg.equals("setblock") && args.length == 2) {
            String customId = args[1];
            CustomBlock customBlock = plugin.getBlockManager().getCustomBlock(customId);
            if (customBlock == null) {
                Msg.warn(player, "Custom block not found: %s", customId);
                return true;
            }
            Block block = player.getLocation().getBlock();
            CustomConfig config = new CustomConfig(customId, (String)null);
            customBlock.setBlock(block, config);
            BlockWatcher blockWatcher = customBlock.createBlockWatcher(block, config);
            if (blockWatcher == null) blockWatcher = new DefaultBlockWatcher(block, customBlock, config);
            plugin.getBlockManager().setBlockWatcher(block, blockWatcher);
            Msg.info(player, "No custom block '%s' created at: %d %d %d", customBlock.getCustomId(), block.getX(), block.getY(), block.getZ());
        } else if (firstArg.equals("getblock") && args.length == 1) {
            Block block = player.getLocation().getBlock();
            BlockWatcher blockWatcher = plugin.getBlockManager().getBlockWatcher(block);
            if (blockWatcher == null) {
                Msg.warn(player, "No custom block found: %d %d %d", block.getX(), block.getY(), block.getZ());
                return true;
            }
            Msg.info(player, "Custom block found: %s", blockWatcher.getCustomBlock().getCustomId());
        } else if (firstArg.equals("reload") && args.length == 1) {
            plugin.reload();
            Msg.info(sender, "Custom Plugin Reloaded");
        }
        return true;
    }
}

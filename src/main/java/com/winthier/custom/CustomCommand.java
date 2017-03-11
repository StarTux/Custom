package com.winthier.custom;

import com.winthier.custom.block.BlockWatcher;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.util.Dirty;
import com.winthier.custom.util.Msg;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
final class CustomCommand implements CommandExecutor {
    private final CustomPlugin plugin;

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
            CustomPlugin.getInstance().getItemManager().dropItemStack(target.getEyeLocation(), customId, amount).setPickupDelay(0);
            Msg.info(player, "Item spawned for %s.", target.getName());
        } else if (firstArg.equals("summon") && args.length == 2) {
            String customId = args[1];
            EntityWatcher entityWatcher = plugin.getEntityManager().spawnEntity(player.getLocation(), customId);
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
        } else if (firstArg.equals("debug")) {
            Object o = Dirty.getItemTag(player.getInventory().getItemInMainHand());
            player.sendMessage("Item tag: (" + o + ")");
        }
        return true;
    }
}

package com.winthier.custom;

import com.winthier.custom.item.CustomItem;
import com.winthier.custom.util.Msg;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
            CustomItem customItem = CustomPlugin.getInstance().getItemRegistry().findItem(itemId);
            if (customItem == null) {
                Msg.warn(player, "Item not found: %s.", itemId);
                return true;
            }
            CustomConfig config = new CustomConfig(itemId, (String)null);
            ItemStack item = customItem.spawnItemStack(amount, config);
            item = config.save(item);
            player.getWorld().dropItemNaturally(player.getEyeLocation(), item).setPickupDelay(0);
            Msg.info(player, "Item spawned for %s.", target.getName());
        } else if (firstArg.equals("reload") && args.length >= 1) {
            plugin.reload();
            sender.sendMessage("Custom Plugin Reloaded");
        }
        return true;
    }
}

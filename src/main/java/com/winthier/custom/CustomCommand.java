package com.winthier.custom;

import com.winthier.custom.item.Item;
import com.winthier.custom.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class CustomCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player)sender : null;
        if (player == null) return false;
        if (args.length == 0) return false;
        String firstArg = args[0].toLowerCase();
        if (firstArg.equals("spawnitem") && args.length > 1) {
            StringBuilder sb = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; ++i) sb.append(" ").append(args[i]);
            String itemId = sb.toString();
            Item item = CustomPlugin.getInstance().findItem(itemId);
            if (item == null) {
                Msg.warn(player, "Item not found: %s.", itemId);
            } else {
                player.getWorld().dropItem(player.getEyeLocation(), item.spawnItemStack()).setPickupDelay(0);
                Msg.info(player, "Spawned item %s.", item.getDisplayName());
            }
        }
        return true;
    }
}

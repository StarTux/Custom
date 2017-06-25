package com.winthier.custom.item;

import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class to format an item description in a unified way.  Use
 * this class to store item descriptions in a custom item.  One
 * version may be stored and reused.
 */
@Data
public final class ItemDescription {
    private String displayName;
    private String category;
    private String description;
    private String usage;
    private final Map<String, String> stats = new LinkedHashMap<>();
    private List<String> lore;
    private static final int LINE_LENGTH = 28;

    public ItemDescription() { }

    public ItemDescription(ItemDescription orig) {
        this.displayName = orig.displayName;
        this.category = orig.category;
        this.description = orig.description;
        this.usage = orig.usage;
        this.stats.putAll(orig.stats);
    }

    public List<String> getLore() {
        if (lore == null) {
            lore = new ArrayList<>();
            if (category != null) {
                lore.add(Msg.format("&9%s", category));
            }
            if (description != null) {
                List<String> lines = Msg.wrap(description, LINE_LENGTH);
                for (int i = 0; i < lines.size(); ++i) lines.set(i, ChatColor.RESET + lines.get(i));
                lore.addAll(lines);
            }
            if (usage != null) {
                lore.add("");
                List<String> lines = Msg.wrap(ChatColor.GREEN + "USAGE" + ChatColor.RESET + " " + usage, LINE_LENGTH);
                for (int i = 1; i < lines.size(); ++i) lines.set(i, ChatColor.RESET + lines.get(i));
                lore.addAll(lines);
            }
            if (!stats.isEmpty()) {
                lore.add("");
                for (Map.Entry<String, String> entry: stats.entrySet()) {
                    lore.add(ChatColor.GREEN + entry.getKey() + ": " + ChatColor.RESET + entry.getValue());
                }
            }
        }
        return lore;
    }

    public void flushLore() {
        lore = null;
    }

    public void load(ConfigurationSection config) {
        displayName = config.getString("DisplayName");
        category = config.getString("Category");
        description = config.getString("Description");
        usage = config.getString("Usage");
        if (displayName != null) displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        if (category != null) category = ChatColor.translateAlternateColorCodes('&', category);
        if (description != null) description = ChatColor.translateAlternateColorCodes('&', description);
        if (usage != null) usage = ChatColor.translateAlternateColorCodes('&', usage);
        lore = null;
    }

    public static ItemDescription of(ConfigurationSection config) {
        ItemDescription instance = new ItemDescription();
        instance.load(config);
        return instance;
    }

    public void apply(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(getLore());
        item.setItemMeta(meta);
    }

    public ItemDescription clone() {
        return new ItemDescription(this);
    }
}

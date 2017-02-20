package com.winthier.custom.item;

import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
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
    private String category;
    private String description;
    private String usage;
    private List<String> lore;
    private static final int LINE_LENGTH = 32;

    public List<String> getLore() {
        if (lore == null) {
            lore = new ArrayList<>();
            if (category != null) {
                lore.add(Msg.format("&o%s", category));
            }
            if (description != null) {
                List<String> lines = Msg.wrap(description, LINE_LENGTH);
                for (int i = 0; i < lines.size(); ++i) lines.set(i, Msg.format("&r%s", lines.get(i)));
                lore.addAll(lines);
            }
            if (usage != null) {
                lore.add("");
                List<String> lines = Msg.wrap(Msg.format("&a&lUSAGE&r %s", usage), LINE_LENGTH);
                for (int i = 1; i < lines.size(); ++i) lines.set(i, Msg.format("&r%s", lines.get(i)));
                lore.addAll(lines);
            }
        }
        return lore;
    }

    public void flushLore() {
        lore = null;
    }

    public void load(ConfigurationSection config) {
        category = config.getString("Category", category);
        description = config.getString("Description", description);
        usage = config.getString("Usage", usage);
        lore = null;
    }

    public static ItemDescription of(ConfigurationSection config) {
        ItemDescription instance = new ItemDescription();
        instance.load(config);
        return instance;
    }

    public void apply(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getLore());
        item.setItemMeta(meta);
    }
}

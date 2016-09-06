package com.winthier.custom.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Subclass this class for easy custom item creation.  You need to
 * override createRawItemStack() and getDisplayName().
 * Additionally, override handleEvent() for interactivity, and you
 * should be good to go.
 */
public abstract class AbstractItem implements Item {
    /**
     * Create a raw item stack, meaning an item without any json
     * information hidden in the lore.  The spawnItemStack()
     * functions will call this and do the heavy lifting for you.
     * Make sure to return a unique copy, so use clone() if you
     * choose to store a template instance.
     */
    public abstract ItemStack createRawItemStack(int amount, ItemJson json);

    public abstract String getDisplayName(ItemJson json);

    @Override
    public ItemStack spawnItemStack(int amount, ItemJson json) {
        ItemStack result = createRawItemStack(amount, json);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + getDisplayName(json));
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = Arrays.asList("");
        } else if (lore.isEmpty()) {
            lore.add("");
        }
        json.getRaw().put("id", getId());
        lore.set(0, lore.get(0) + ItemUtil.MAGIC + ItemUtil.hideJson(json.getRaw()));
        meta.setLore(lore);
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public ItemStack spawnItemStack(int amount) {
        return spawnItemStack(amount, new ItemJson());
    }

    @Override
    public ItemStack spawnItemStack() {
        return spawnItemStack(1, new ItemJson());
    }

    @Override
    public boolean handleEvent(Event event, ItemContext itemContext) {
        return false;
    }
}

package com.winthier.custom.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * See Item class.
 */
public abstract class AbstractItem implements Item {
    @Override
    public ItemStack spawnItemStack(int amount) {
        ItemStack result = new ItemStack(getMaterial(), 1, getDurability());
        if (getEnchantments() != null) {
            result.addUnsafeEnchantments(getEnchantments());
        }
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + getDisplayName());
        List<String> lore = getLore();
        if (lore.isEmpty()) lore.add("");
        lore.set(0, lore.get(0) + ItemUtil.MAGIC + ItemUtil.hideJson(getJson()));
        meta.setLore(lore);
        result.setItemMeta(meta);
        result.setAmount(amount);
        return result;
    }

    @Override
    public short getDurability() {
        return (short)0;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        if (getDescription() != null) {
            for (String line: WordUtils.wrap(getDescription(), 32).split("\n")) {
                lore.add(line);
            }
        }
        return lore;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return null;
    }

    @Override
    public Map<String, Object> getJson() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId());
        return result;
    }

    @Override
    public boolean handleEvent(Event event, ItemContext itemContext) {
        return false;
    }
}

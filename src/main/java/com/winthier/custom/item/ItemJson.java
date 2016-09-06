package com.winthier.custom.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemJson {
    private Map<String, Object> raw;

    ItemJson(Map<String, Object> raw) {
        this.raw = raw;
    }

    ItemJson() {
        this(null);
    }

    public Map<String, Object> getRaw() {
        if (raw == null) {
            raw = new HashMap<>();
        }
        return raw;
    }

    private Object findItem(String... path) {
        Map<String, Object> current = getRaw();
        for (String pathName: path) {
            Object finding = current.get(pathName);
            if (finding == null) return null;
            if (!(finding instanceof Map)) return null;
            @SuppressWarnings("unchecked")
            final Map<String, Object> tmp = (Map<String, Object>)finding;
            current = tmp;
        }
        return current;
    }

    public void save(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        if (lore.isEmpty()) lore.add("");
        String firstLine = lore.get(0);
        String[] arr = firstLine.split(ItemUtil.MAGIC, 2);
        lore.set(0, arr[0] + ItemUtil.MAGIC + ItemUtil.hideJson(raw));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
}

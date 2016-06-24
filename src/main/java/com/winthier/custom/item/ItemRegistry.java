package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.util.Msg;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRegistry {
    final Map<String, Item> registeredItems = new HashMap<>();

    public void reload() {
        registeredItems.clear();
        ItemRegisterEvent.call();
    }

    public boolean registerItem(Item item) {
        if (registeredItems.containsKey(item.getId())) return false;
        registeredItems.put(item.getId(), item);
        CustomPlugin.getInstance().getLogger().info("Registered item " + item.getId() + ": " + item.getClass().getName());
        return true;
    }

    public ItemContext findItemContext(ItemStack itemStack) {
        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        if (!meta.hasLore()) return null;
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return null;
        String firstLine = lore.get(0);
        String[] arr = firstLine.split(Msg.MAGIC, 2);
        if (arr.length != 2) return null;
        Map<String, Object> json = Msg.unhideJson(arr[1]);
        if (json == null) return null;
        if (!json.containsKey("id")) return null;
        String id = json.get("id").toString();
        Item item = registeredItems.get(id);
        return new ItemContext(item, itemStack, json);
    }

    public Item findItem(String id) {
        return registeredItems.get(id);
    }
}

package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRegistry {
    final Map<String, Item> registeredItems = new HashMap<>();
    @Getter final List<CraftingRecipe> registeredRecipes = new ArrayList<>();

    public void reload() {
        registeredItems.clear();
        ItemRegisterEvent event = ItemRegisterEvent.call();
        for (Item item: event.getItems()) {
            try {
                if (registeredItems.containsKey(item.getId())) {
                    CustomPlugin.getInstance().getLogger().warning("Item Registry: Duplicate Item ID: " + item.getId());
                } else {
                    registeredItems.put(item.getId(), item);
                    CustomPlugin.getInstance().getLogger().info("Registered Item: " + item.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (CraftingRecipe craftingRecipe: event.getRecipes()) {
            try {
                Recipe recipe = craftingRecipe.getRecipe();
                boolean ret = Bukkit.getServer().addRecipe(recipe);
                registeredRecipes.add(craftingRecipe);
                CustomPlugin.getInstance().getLogger().info("Registered Recipe: " + craftingRecipe.getName());
                if (!ret) {
                    CustomPlugin.getInstance().getLogger().warning("Item Registry: Could not register recipe: " + craftingRecipe.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        String[] arr = firstLine.split(ItemUtil.MAGIC, 2);
        if (arr.length != 2) return null;
        Map<String, Object> json = ItemUtil.unhideJson(arr[1]);
        if (json == null) return null;
        if (!json.containsKey("id")) return null;
        String id = json.get("id").toString();
        Item item = registeredItems.get(id);
        if (item == null) {
            CustomPlugin.getInstance().getLogger().warning("Found custom item with unknown ID: " + id);
            item = new DummyItem(id, id, itemStack.getType(), null, null);
            registeredItems.put(id, item);
        }
        return new ItemContext(item, itemStack, json);
    }

    public Item findItem(String id) {
        return registeredItems.get(id);
    }
}

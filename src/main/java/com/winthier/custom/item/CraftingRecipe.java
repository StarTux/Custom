package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

@Getter
@RequiredArgsConstructor
public class CraftingRecipe {
    final String name;
    final String result;
    final List<String> ingredients;

    public CraftingRecipe(String name, String result, String... ingredients) {
        this(name, result, Arrays.asList(ingredients));
    }

    public boolean matches(ItemStack[] matrix) {
        if (ingredients.size() != matrix.length) return false;
        Iterator<ItemStack> matrixIter = Arrays.asList(matrix).iterator();
        Iterator<String> ingredientIter = ingredients.iterator();
        while (matrixIter.hasNext() && ingredientIter.hasNext()) {
            ItemStack itemStack = matrixIter.next();
            String ingredient = ingredientIter.next();
            if (ingredient == null) {
                if (itemStack != null && itemStack.getType() != Material.AIR) return false;
            } else {
                ItemContext itemContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(itemStack);
                if (itemContext == null) return false;
                if (!itemContext.item.getId().equals(ingredient)) return false;
            }
        }
        return true;
    }

    Item getResultItem() {
        Item item = CustomPlugin.getInstance().getItemRegistry().findItem(result);
        if (item == null) CustomPlugin.getInstance().getLogger().warning("Recipe " + name + ": Result item not found: " + result);
        return item;
    }

    List<Item> getIngredientItems() {
        List<Item> list = new ArrayList<>();
        for (String name: ingredients) {
            if (name == null) {
                list.add(null);
            } else {
                Item item = CustomPlugin.getInstance().getItemRegistry().findItem(name);        
                if (item == null) CustomPlugin.getInstance().getLogger().warning("Recipe " + this.name + ": Ingredient item not found: " + name);
                list.add(item);
            }
        }
        return list;
    }

    Recipe getRecipe() {
        StringBuilder sb = new StringBuilder();
        final String chars = "abcdefghijklmnopqrstuvwxyz";
        final Map<Character, MaterialData> ingredientMap = new HashMap<>();
        int pos = 0;
        for (Item ingredient: getIngredientItems()) {
            if (ingredient == null) {
                sb.append(" ");
            } else {
                char chr = chars.charAt(pos);
                sb.append(chr);
                @SuppressWarnings("deprecation")
                MaterialData mat = new MaterialData(ingredient.getMaterial(), (byte)ingredient.getDurability());
                ingredientMap.put(chr, mat);
            }
            pos += 1;
        }
        String shape = sb.toString();
        ShapedRecipe recipe = new ShapedRecipe(getResultItem().spawnItemStack(1));
        recipe.shape(shape.substring(0, 3), shape.substring(3, 6), shape.substring(6, 9));
        for (Map.Entry<Character, MaterialData> entry: ingredientMap.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }
        return recipe;
    }
}

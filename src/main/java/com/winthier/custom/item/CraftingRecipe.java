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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

/**
 * This class supports shaped recipes.  We may in the future have
 * to add options for shapeless recipes.  Not to mention furnace
 * and brewing.
 */
@Getter
@RequiredArgsConstructor
public class CraftingRecipe {
    final ItemStack result;
    final List<ItemStack> ingredients;

    public CraftingRecipe(ItemStack result, ItemStack... ingredients) {
        this(result, Arrays.asList(ingredients));
    }

    public boolean matches(ItemStack[] matrix) {
        // Matrix sizes must match.
        if (ingredients.size() != matrix.length) return false;
        Iterator<ItemStack> matrixIter = Arrays.asList(matrix).iterator();
        Iterator<ItemStack> recipeIter = ingredients.iterator();
        while (matrixIter.hasNext() && recipeIter.hasNext()) {
            ItemStack matrixItem = matrixIter.next();
            ItemStack recipeItem = recipeIter.next();
            if (recipeItem == null) {
                // Test if the recipe item is null
                if (matrixItem != null && matrixItem.getType() != Material.AIR) return false;
            } else if (matrixItem == null || matrixItem.getType() == Material.AIR) {
                // Test if the matrix item is null
                if (recipeItem != null) return false;
            } else {
                // Nothing is null. Check if a custom item is required.
                ItemContext recipeContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(recipeItem);
                if (recipeContext == null) {
                    // The recipe item is not custom, so we require both items to be similar.
                    if (!matrixItem.isSimilar(recipeItem)) return false;
                } else {
                    // The recipe item is custom, so the matrix item needs to be custom as well.
                    ItemContext matrixContext = CustomPlugin.getInstance().getItemRegistry().findItemContext(matrixItem);
                    if (matrixContext == null) return false;
                    if (!recipeContext.item.getId().equals(matrixContext.item.getId())) return false;
                    // So far we are happy if both custom items
                    // have the same id.  A nasty plugin developer
                    // may one day require both items to have
                    // compatible json information in addition.
                    // Let us hope that day will never come, but
                    // if it does, this code will require some
                    // patching.
                }
            }
        }
        return true;
    }

    Recipe getBukkitRecipe() {
        StringBuilder sb = new StringBuilder();
        final String chars = "abcdefghijklmnopqrstuvwxyz";
        final Map<Character, MaterialData> ingredientMap = new HashMap<>();
        int pos = 0;
        for (ItemStack ingredient: ingredients) {
            if (ingredient == null) {
                sb.append(" ");
            } else {
                char chr = chars.charAt(pos);
                sb.append(chr);
                MaterialData mat = ingredient.getData();
                ingredientMap.put(chr, mat);
            }
            pos += 1;
        }
        String shape = sb.toString();
        ShapedRecipe recipe = new ShapedRecipe(result);
        if (ingredients.size() == 9) {
            recipe.shape(shape.substring(0, 3), shape.substring(3, 6), shape.substring(6, 9));
        } else if (ingredients.size() == 4) {
            recipe.shape(shape.substring(0, 2), shape.substring(2, 4));
        }
        for (Map.Entry<Character, MaterialData> entry: ingredientMap.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }
        return recipe;
    }
}

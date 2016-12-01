package com.winthier.custom.item;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemRegistry {
    final CustomPlugin plugin;
    final Map<String, CustomItem> registeredItems = new HashMap<>();
    // @Getter final List<CraftingRecipe> registeredRecipes = new ArrayList<>();

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomItem item: event.getItems()) {
            if (registeredItems.containsKey(item.getCustomId())) {
                CustomPlugin.getInstance().getLogger().warning("Item Registry: Duplicate Item ID: " + item.getCustomId());
            } else {
                registeredItems.put(item.getCustomId(), item);
                plugin.getEventManager().registerEvents(item);
                CustomPlugin.getInstance().getLogger().info("Registered Item: " + item.getCustomId());
            }
        }
        // int recipeSuccessCount = 0;
        // int recipeFailCount = 0;
        // for (CraftingRecipe craftingRecipe: event.getRecipes()) {
        //     try {
        //         Recipe recipe = craftingRecipe.getBukkitRecipe();
        //         boolean ret = plugin.getServer().addRecipe(recipe);
        //         registeredRecipes.add(craftingRecipe);
        //         if (!ret) {
        //             recipeFailCount += 1;
        //         } else {
        //             recipeSuccessCount += 1;
        //         }
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         recipeFailCount += 1;
        //     }
        // }
        // CustomPlugin.getInstance().getLogger().info("Registered " + recipeSuccessCount + " recipes. Failed to register " + recipeFailCount);
    }

    public CustomItem findItem(String id) {
        return registeredItems.get(id);
    }

    public CustomItem findItem(CustomConfig config) {
        return findItem(config.getCustomId());
    }
}

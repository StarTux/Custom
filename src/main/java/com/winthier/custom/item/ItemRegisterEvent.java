package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called so all plugins interested in registering
 * custom items can listen to it and use it to do so.
 */
public class ItemRegisterEvent extends Event {
    // Event Stuff 
    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    // My Stuff

    @Getter
    final List<Item> items = new ArrayList<>();
    @Getter
    private final List<CraftingRecipe> recipes = new ArrayList<>();

    public void registerItem(Item item) {
        items.add(item);
    }

    public void registerRecipe(CraftingRecipe recipe) {
        recipes.add(recipe);
    }

    static ItemRegisterEvent call() {
        ItemRegisterEvent event = new ItemRegisterEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}


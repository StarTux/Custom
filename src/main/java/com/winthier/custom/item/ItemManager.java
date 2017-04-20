package com.winthier.custom.item;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import com.winthier.custom.util.Dirty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class ItemManager {
    private final CustomPlugin plugin;
    private final Map<String, CustomItem> registeredItems = new HashMap<>();
    private final ItemCrawler itemCrawler = new ItemCrawler(this);

    // Public use methods

    /**
     * For public use.
     *
     * Returns an ItemContext instance with the following fields
     * filled: customItem, itemStack. It will set the
     * following fields to null: player, position.  Returns null
     * if the item stack is not a custom item, or the custom item
     * cannot be found.  Use this if you must find out if a random
     * item is custom.  Do not use this in lieu of EventHandlers
     * in your CustomItem subclass!
     */
    public ItemContext getItemContext(ItemStack item) {
        if (item == null) return null;
        CustomItem customItem = getCustomItem(item);
        if (customItem == null) return null;
        return new ItemContext(item, customItem, null, null, 0);
    }

    public CustomItem getCustomItem(ItemStack item) {
        String customId = getCustomId(item);
        if (customId == null) return null;
        CustomItem result = getCustomItem(customId);
        if (result == null) {
            plugin.getLogger().warning("Encountered unknown custom item '" + customId + "'. Using default implementation.");
            result = new DefaultCustomItem(customId);
            registeredItems.put(customId, result);
        }
        return result;
    }

    public CustomItem getCustomItem(String customId) {
        return registeredItems.get(customId);
    }

    public ItemStack spawnItemStack(String customId, int amount) {
        CustomItem customItem = getCustomItem(customId);
        if (customItem == null) throw new IllegalArgumentException("Unknown item id: " + customId);
        ItemStack itemStack = customItem.spawnItemStack(amount);
        itemStack = Dirty.setCustomId(itemStack, customId);
        customItem.itemStackWasSpawned(itemStack);
        return itemStack;
    }

    public Item dropItemStack(Location location, String customId, int amount) {
        ItemStack itemStack = spawnItemStack(customId, amount);
        return location.getWorld().dropItem(location, itemStack);
    }

    public List<CustomItem> getRegisteredItems() {
        return new ArrayList<>(registeredItems.values());
    }

    public ItemStack wrapItemStack(ItemStack itemStack, String customId) {
        return Dirty.setCustomId(itemStack, customId);
    }

    // Internal use methods

    public void onEnable() {
        itemCrawler.start();
    }

    public void onDisable() {
        itemCrawler.stop();
    }

    public String getCustomId(ItemStack item) {
        return Dirty.getCustomId(item);
    }

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomItem item: event.getItems()) {
            if (registeredItems.containsKey(item.getCustomId())) {
                CustomPlugin.getInstance().getLogger().warning("Item Manager: Duplicate Item id: " + item.getCustomId());
            } else {
                registeredItems.put(item.getCustomId(), item);
                plugin.getEventManager().registerEvents(item);
            }
        }
    }
}

package com.winthier.custom.item;

import com.winthier.custom.CustomConfig;
import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomRegisterEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class ItemManager {
    private final CustomPlugin plugin;
    private final Map<String, CustomItem> registeredItems = new HashMap<>();

    // Public use methods

    /**
     * For public use.
     *
     * Returns an ItemContext instance with the following fields
     * filled: customItem, itemStack, config.  It will set the
     * following fields to null: player, position.  Returns null
     * if the item stack is not a custom item, or the custom item
     * cannot be found.  Use this if you must find out if a random
     * item is custom.  Do not use this in lieu of EventHandlers
     * in your CustomItem subclass!
     */
    public ItemContext getItemContext(ItemStack item) {
        if (item == null) return null;
        CustomConfig config = CustomConfig.of(item);
        if (config == null) return null;
        CustomItem customItem = findItem(config);
        if (customItem == null) return null;
        return new ItemContext(null, customItem, item, null, config);
    }

    public ItemStack spawnItemStack(CustomConfig config, int amount) {
        CustomItem customItem = findItem(config);
        if (customItem == null) throw new IllegalArgumentException("Unknown custom item id: " + config.getCustomId());
        ItemStack itemStack = customItem.spawnItemStack(amount, config);
        config.save(itemStack);
        return itemStack;
    }

    public ItemStack spawnItemStack(String customId, int amount) {
        return spawnItemStack(new CustomConfig(customId), amount);
    }

    public Item dropItemStack(Location location, CustomConfig config, int amount) {
        ItemStack itemStack = spawnItemStack(config, amount);
        return location.getWorld().dropItem(location, itemStack);
    }

    public Item dropItemStack(Location location, String customId, int amount) {
        return dropItemStack(location, new CustomConfig(customId), amount);
    }

    // Internal use methods

    public void onCustomRegister(CustomRegisterEvent event) {
        for (CustomItem item: event.getItems()) {
            if (registeredItems.containsKey(item.getCustomId())) {
                CustomPlugin.getInstance().getLogger().warning("Item Manager: Duplicate Item ID: " + item.getCustomId());
            } else {
                registeredItems.put(item.getCustomId(), item);
                plugin.getEventManager().registerEvents(item);
                CustomPlugin.getInstance().getLogger().info("Registered Item: " + item.getCustomId());
            }
        }
    }

    public CustomItem findItem(String id) {
        return registeredItems.get(id);
    }

    public CustomItem findItem(CustomConfig config) {
        return findItem(config.getCustomId());
    }

    public CustomItem getItem(CustomConfig config) {
        String id = config.getCustomId();
        CustomItem result = findItem(id);
        if (result == null) {
            plugin.getLogger().warning("Encountered unknown custom item '" + config.getCustomId() + "'");
            result = new DefaultCustomItem(id);
            registeredItems.put(id, result);
        }
        return result;
    }
}

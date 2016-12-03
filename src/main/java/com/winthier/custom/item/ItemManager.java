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
public class ItemManager {
    final CustomPlugin plugin;
    final Map<String, CustomItem> registeredItems = new HashMap<>();

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

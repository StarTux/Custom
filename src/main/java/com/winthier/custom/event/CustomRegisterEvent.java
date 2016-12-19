package com.winthier.custom.event;

import com.winthier.custom.block.CustomBlock;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.item.CustomItem;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called so all plugins interested in registering
 * custom things can listen to it and call the appropriate method
 * once it gets called.
 */
public class CustomRegisterEvent extends Event {
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
    final List<CustomItem> items = new ArrayList<>();
    @Getter
    final List<CustomEntity> entities = new ArrayList<>();
    @Getter
    final List<CustomBlock> blocks = new ArrayList<>();

    public void addItem(CustomItem item) {
        items.add(item);
    }

    public void addEntity(CustomEntity entity) {
        entities.add(entity);
    }

    public void addBlock(CustomBlock block) {
        blocks.add(block);
    }
}

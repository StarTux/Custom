package com.winthier.custom.event;

import com.winthier.custom.block.CustomBlock;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.item.CustomItem;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Listen for one specific event with one priority.
 */
@Getter @RequiredArgsConstructor
class EventDispatcher implements Listener, EventExecutor {
    private final EventManager eventManager;
    private final Class<? extends Event> eventClass;
    private final EventPriority priority;
    private final Map<String, HandlerCaller<CustomItem>> items = new HashMap<>();
    private final Map<String, HandlerCaller<CustomEntity>> entities = new HashMap<>();
    private final Map<String, HandlerCaller<CustomBlock>> blocks = new HashMap<>();
    private ItemEventCaller itemEventCaller = null;
    private EntityEventCaller entityEventCaller = null;
    private BlockEventCaller blockEventCaller = null;

    void enable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(eventClass, this, priority, this, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (!eventClass.isInstance(event)) return; // Happens sometimes
        if (!items.isEmpty()) {
            if (itemEventCaller == null) itemEventCaller = ItemEventCaller.of(this, event);
            itemEventCaller.call(event);
        }
        if (!entities.isEmpty()) {
            if (entityEventCaller == null) entityEventCaller = EntityEventCaller.of(this, event);
            entityEventCaller.call(event);
        }
        if (!blocks.isEmpty()) {
            if (blockEventCaller == null) blockEventCaller = BlockEventCaller.of(this, eventClass);
            blockEventCaller.call(event);
        }
    }

    void registerEvent(Listener listener, Method method, boolean ignoreCancelled) {
        if (listener instanceof CustomItem) {
            CustomItem customItem = (CustomItem)listener;
            HandlerCaller<CustomItem> caller = new HandlerCaller<>(eventClass, customItem, method, ignoreCancelled);
            items.put(customItem.getCustomId(), caller);
        }
        if (listener instanceof CustomEntity) {
            CustomEntity customEntity = (CustomEntity)listener;
            HandlerCaller<CustomEntity> caller = new HandlerCaller<>(eventClass, customEntity, method, ignoreCancelled);
            entities.put(customEntity.getCustomId(), caller);
        }
        if (listener instanceof CustomBlock) {
            CustomBlock customBlock = (CustomBlock)listener;
            HandlerCaller<CustomBlock> caller = new HandlerCaller<>(eventClass, customBlock, method, ignoreCancelled);
            blocks.put(customBlock.getCustomId(), caller);
        }
    }

    void clear() {
        items.clear();
        entities.clear();
        blocks.clear();
        itemEventCaller = null;
        entityEventCaller = null;
    }
}

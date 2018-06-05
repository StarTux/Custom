package com.winthier.custom.entity;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

public final class SimpleScriptEntity implements CustomEntity {
    public static final String CUSTOM_ID = "custom:SimpleScript";

    @Override
    public String getCustomId() {
        return CUSTOM_ID;
    }

    @Override
    public Entity spawnEntity(Location location) {
        return location.getWorld().spawn(location, ArmorStand.class);
    }

    @Override
    public EntityWatcher createEntityWatcher(Entity entity) {
        return new Watcher(this, entity);
    }

    @Override
    public void entityWasDiscovered(EntityWatcher watcher) {
        watcher.getEntity().remove();
    }

    @Override
    public void entityWillUnload(EntityWatcher watcher) {
        watcher.getEntity().remove();
    }

    @Getter @RequiredArgsConstructor
    public static final class Watcher implements EntityWatcher {
        private final SimpleScriptEntity customEntity;
        private final Entity entity;
        @Setter private Consumer<Event> eventHandler = null;

        @Override
        public void handleEvent(Event event, EntityContext context) {
            if (eventHandler != null) eventHandler.accept(event);
        }
    }
}

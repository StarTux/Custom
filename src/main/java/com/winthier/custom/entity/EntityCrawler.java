package com.winthier.custom.entity;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.event.CustomTickEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

@Getter @RequiredArgsConstructor
class EntityCrawler {
    private final EntityManager entityManager;
    private Iterator<Entity> serverEntities;
    private Iterator<UUID> customEntities;
    private BukkitRunnable task;
    private int ticks = 0;

    public void checkAll() {
        for (World world: Bukkit.getServer().getWorlds()) {
            for (Entity entity: world.getEntities()) {
                entityManager.getEntityWatcher(entity);
            }
        }
    }

    public void start() {
        stop();
        task = new BukkitRunnable() {
                @Override public void run() {
                    tick();
                }
            };
        task.runTaskTimer(CustomPlugin.getInstance(), 1, 1);
    }

    public void stop() {
        if (task == null) return;
        try {
            task.cancel();
        } catch (IllegalStateException ise) { }
        task = null;
    }

    void tick() {
        if (serverEntities == null || !serverEntities.hasNext()) {
            List<Entity> list = new ArrayList<>();
            for (World world: Bukkit.getServer().getWorlds()) {
                for (Entity entity: world.getEntities()) {
                    list.add(entity);
                }
            }
            serverEntities = list.iterator();
        }
        for (int i = 0; i < 100; ++i) {
            if (!serverEntities.hasNext()) break;
            Entity entity = serverEntities.next();
            if (!entity.isValid()) continue;
            entityManager.getEntityWatcher(entity);
        }
        int ticks = this.ticks++;
        CustomTickEvent.Type.WILL_TICK_ENTITIES.call(ticks);
        for (UUID uuid: entityManager.getWatchedEntities()) {
            EntityWatcher entityWatcher = entityManager.getEntityWatcher(uuid);
            if (entityWatcher == null) continue;
            CustomEntity customEntity = entityWatcher.getCustomEntity();
            if (!entityWatcher.getEntity().isValid()) {
                customEntity.entityDidUnload(entityWatcher);
                entityManager.unwatchEntity(entityWatcher);
            } else if (customEntity instanceof TickableEntity) {
                ((TickableEntity)customEntity).onTick(entityWatcher);
            }
        }
        CustomTickEvent.Type.DID_TICK_ENTITIES.call(ticks);
    }
}

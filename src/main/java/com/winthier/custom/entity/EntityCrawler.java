package com.winthier.custom.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

@Getter @RequiredArgsConstructor
class EntityCrawler {
    final EntityManager entityManager;
    Iterator<Entity> serverEntities;
    Iterator<UUID> customEntities;
    static enum State {
        SERVER, CUSTOM;
    }
    State state = State.SERVER;
    BukkitRunnable task;

    public void checkAll() {
        for (World world: entityManager.plugin.getServer().getWorlds()) {
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
        task.runTaskTimer(entityManager.plugin, 1, 1);
    }

    public void stop() {
        if (task == null) return;
        try {
            task.cancel();
        } catch (IllegalStateException ise) {}
        task = null;
    }

    void tick() {
        if (state == State.SERVER) {
            if (serverEntities == null) {
                List<Entity> list = new ArrayList<>();
                for (World world: entityManager.plugin.getServer().getWorlds()) {
                    for (Entity entity: world.getEntities()) {
                        list.add(entity);
                    }
                }
                serverEntities = list.iterator();
            } else {
                for (int i = 0; i < 100; ++i) {
                    if (serverEntities.hasNext()) {
                        Entity entity = serverEntities.next();
                        if (entity.isValid()) {
                            entityManager.getEntityWatcher(entity);
                        }
                    } else {
                        serverEntities = null;
                        state = State.CUSTOM;
                        return;
                    }
                }
            }
        } else if (state == State.CUSTOM) {
            if (customEntities == null) {
                List<UUID> list = new ArrayList<>(entityManager.entityWatcherMap.keySet());
                customEntities = list.iterator();
            } else {
                for (int i = 0; i < 100; ++i) {
                    if (customEntities.hasNext()) {
                        UUID uuid = customEntities.next();
                        EntityWatcher entityWatcher = entityManager.entityWatcherMap.get(uuid);
                        if (entityWatcher != null && !entityWatcher.getEntity().isValid()) {
                            entityManager.removeEntity(entityWatcher);
                            entityWatcher.entityDidUnload();
                            Location loc = entityWatcher.getEntity().getLocation();
                            entityManager.plugin.getLogger().warning(String.format("Custom entity %s did disappear at %s %d %d %d", entityWatcher.getCustomEntity().getCustomId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                        }
                    } else {
                        customEntities = null;
                        state = State.SERVER;
                        return;
                    }
                }
            }
        }
    }
}

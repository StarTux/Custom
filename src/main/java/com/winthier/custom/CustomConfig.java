package com.winthier.custom;

import com.winthier.custom.util.Dirty;
import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomConfig {
    final static String KEY_ENTITY_CUSTOM = "Winthier.Custom";
    final static String KEY_ENTITY_SEPARATOR = ";";

    @Getter final String customId;
    Map<String, Object> raw = null; // lazy
    String json;

    public CustomConfig(String customId, String json) {
        this.customId = customId;
        this.raw = null;
        this.json = json;
    }

    public CustomConfig(String customId, Map<String, Object> raw) {
        this.customId = customId;
        this.raw = raw;
        this.json = null;
    }

    // Getters

    Map<String, Object> getRaw() {
        if (raw == null && json != null) {
            Object o = Msg.parseJson(json);
            json = null;
            if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>)o;
                raw = m;
            }
        }
        return raw;
    }

    public String getString(String path, String dfl) {
        Object o = findItem(path);
        if (o == null) {
            return dfl;
        } else if (o instanceof String) {
            return (String)o;
        } else {
            return o.toString();
        }
    }

    public Integer getInt(String path, Integer dfl) {
        Object o = findItem(path);
        if (o == null) {
            return dfl;
        } else if (o instanceof Integer) {
            return (Integer)o;
        } else if (o instanceof Number) {
            return ((Number)o).intValue();
        } else {
            try {
                return Integer.parseInt(o.toString());
            } catch (NumberFormatException nfe) {
                return dfl;
            }
        }
    }

    // Getter Utility

    private Object findItem(String path) {
        return findItem(path.split("\\."));
    }

    private Object findItem(String[] path) {
        if (raw == null) return null;
        Map<String, Object> current = raw;
        for (String pathName: path) {
            Object finding = current.get(pathName);
            if (finding == null) return null;
            if (!(finding instanceof Map)) return null;
            @SuppressWarnings("unchecked")
            final Map<String, Object> tmp = (Map<String, Object>)finding;
            current = tmp;
        }
        return current;
    }

    // Serialization

    public String serialize() {
        if (getRaw() == null) return null;
        return Msg.toJsonString(getRaw());
    }

    /**
     * May return a copy in case itemStack is not an instance of
     * CraftItemStack.
     */
    public ItemStack save(ItemStack itemStack) {
        return Dirty.saveConfig(itemStack, this);
    }

    public void save(Entity entity) {
        for (String tag: new ArrayList<>(entity.getScoreboardTags())) {
            if (tag.startsWith(KEY_ENTITY_CUSTOM)) {
                entity.removeScoreboardTag(tag);
            }
        }
        String json = serialize();
        String tag;
        if (json != null) {
            tag = KEY_ENTITY_CUSTOM + KEY_ENTITY_SEPARATOR + getCustomId() + KEY_ENTITY_SEPARATOR + json;
        } else {
            tag = KEY_ENTITY_CUSTOM + KEY_ENTITY_SEPARATOR + getCustomId();
        }
        entity.addScoreboardTag(tag);
    }

    public void save(Block block) {
        // TODO
    }

    // Deserialization

    public static CustomConfig of(ItemStack item) {
        return Dirty.loadConfig(item);
    }

    public static CustomConfig of(Entity entity) {
        String tag = null;
        for (String aTag: entity.getScoreboardTags()) {
            if (tag.startsWith(KEY_ENTITY_CUSTOM)) {
                tag = aTag;
                break;
            }
        }
        if (tag == null) return null;
        String[] tokens = tag.split(KEY_ENTITY_SEPARATOR, 3);
        String id = tokens[1];
        String json = tokens.length == 3 ? tokens[2] : null;
        return new CustomConfig(id, json);
    }

    public static CustomConfig of(Block block) {
        // TODO
        return null;
    }
}

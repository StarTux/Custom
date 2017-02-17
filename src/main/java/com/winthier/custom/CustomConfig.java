package com.winthier.custom;

import com.winthier.custom.util.Dirty;
import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public final class CustomConfig {
    static final String KEY_ENTITY_CUSTOM = "Winthier.Custom";
    static final String KEY_ENTITY_SEPARATOR = ";";

    @Getter
    private final String customId;
    private Map<String, Object> raw = null; // lazy
    private String json;

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

    public Map<String, Object> getRaw() {
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

    Map<String, Object> getRawP() {
        if (getRaw() == null) raw = new HashMap<>();
        return raw;
    }

    public String getString(String path, String dfl) {
        if (getRaw() == null) return null;
        Object o = raw.get(path);
        if (o == null) {
            return dfl;
        } else if (o instanceof String) {
            return (String)o;
        } else {
            return o.toString();
        }
    }

    public Integer getInt(String path, Integer dfl) {
        if (getRaw() == null) return null;
        Object o = raw.get(path);
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

    public void set(String path, Object o) {
        getRawP().put(path, o);
    }

    public void remove(String path) {
        Map<String, Object> thisRaw = getRaw();
        if (thisRaw != null) thisRaw.remove(path);
    }

    // Serialization

    public String getJsonString() {
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
        String jsonString = getJsonString();
        String tag;
        if (jsonString != null) {
            tag = KEY_ENTITY_CUSTOM + KEY_ENTITY_SEPARATOR + getCustomId() + KEY_ENTITY_SEPARATOR + jsonString;
        } else {
            tag = KEY_ENTITY_CUSTOM + KEY_ENTITY_SEPARATOR + getCustomId();
        }
        entity.addScoreboardTag(tag);
    }

    // Deserialization

    public static CustomConfig of(ItemStack item) {
        return Dirty.loadConfig(item);
    }

    public static CustomConfig of(Entity entity) {
        String tag = null;
        for (String aTag: entity.getScoreboardTags()) {
            if (aTag.startsWith(KEY_ENTITY_CUSTOM)) {
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
}

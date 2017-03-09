package com.winthier.custom;

import com.winthier.custom.util.Dirty;
import com.winthier.custom.util.Msg;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
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

    public CustomConfig(String customId) {
        this.customId = customId;
        this.raw = null;
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
        if (getRaw() == null) return dfl;
        Object o = raw.get(path);
        if (o == null) {
            return dfl;
        } else if (o instanceof String) {
            return (String)o;
        } else {
            return o.toString();
        }
    }

    public int getInt(String path, int dfl) {
        if (getRaw() == null) return dfl;
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

    // Deserialization

    public static CustomConfig of(ItemStack item) {
        return Dirty.loadConfig(item);
    }
}

package com.winthier.custom.item;

import com.winthier.custom.util.Msg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONValue;

public class ItemUtil {
    public static final String MAGIC = "" + ChatColor.RESET + ChatColor.BLACK + ChatColor.MAGIC;

    public static String jsonToString(Object json) {
        if (json == null) {
            return "";
        } else if (json instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Object o: (List)json) {
                sb.append(jsonToString(o));
            }
            return sb.toString();
        } else if (json instanceof Map) {
            Map map = (Map)json;
            StringBuilder sb = new StringBuilder();
            sb.append(map.get("text"));
            sb.append(map.get("extra"));
            return sb.toString();
        } else if (json instanceof String) {
            return (String)json;
        } else {
            return json.toString();
        }
    }

    public static String hideString(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            sb.append(ChatColor.COLOR_CHAR).append(string.charAt(i));
        }
        return sb.toString();
    }

    public static String unhideString(String string) {
        if (string.length() % 2 != 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i += 2) {
            if (string.charAt(i) != ChatColor.COLOR_CHAR) return null;
            sb.append(string.charAt(i + 1));
        }
        return sb.toString();
    }

    public static String hideJson(Map<String, Object> json) {
        return hideString(JSONValue.toJSONString(json));
    }

    public static Map<String, Object> unhideJson(String string) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>)JSONValue.parse(unhideString(string));
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<Enchantment, Integer> getDefaultEnchantments() {
        Map<Enchantment, Integer> result = new HashMap<>();
        result.put(Enchantment.DURABILITY, 1);
        return result;
    }

    public static List<String> makeBukkitLore(String lore) {
        return Arrays.asList(lore.split("\n"));
    }
}

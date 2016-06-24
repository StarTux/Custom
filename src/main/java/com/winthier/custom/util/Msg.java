package com.winthier.custom.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONValue;

public class Msg {
    public static final String MAGIC = "" + ChatColor.RESET + ChatColor.BLACK + ChatColor.MAGIC;

    public static String format(String msg, Object... args) {
        if (msg == null) return "";
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        return msg;
    }

    public static void send(CommandSender to, String msg, Object... args) {
        to.sendMessage(format(msg, args));
    }

    public static void info(CommandSender to, String msg, Object... args) {
        to.sendMessage(format("&r[&3Custom&r] ") + format(msg, args));
    }

    public static void warn(CommandSender to, String msg, Object... args) {
        to.sendMessage(format("&r[&cCustom&r] &c") + format(msg, args));
    }

    static void consoleCommand(String cmd, Object... args)
    {
        if (args.length > 0) cmd = String.format(cmd, args);
        // if (CustomPlugin.getInstance().debugMode) {
        //     ChstomPlugin.getInstance().getLogger().info("Running console command: " + cmd);
        // }
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }

    public static void raw(Player player, Object... obj)
    {
        if (obj.length == 0) return;
        if (obj.length == 1) {
            consoleCommand("minecraft:tellraw %s %s", player.getName(), JSONValue.toJSONString(obj[0]));
        } else {
            consoleCommand("minecraft:tellraw %s %s", player.getName(), JSONValue.toJSONString(Arrays.asList(obj)));
        }
    }

    public static Object button(ChatColor color, String chat, String insertion, String tooltip, String command) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", format(chat));
        map.put("color", color.name().toLowerCase());
        if (insertion != null) {
            map.put("insertion", insertion);
        }
        if (command != null) {
            Map<String, Object> clickEvent = new HashMap<>();
            map.put("clickEvent", clickEvent);
            clickEvent.put("action", command.endsWith(" ") ? "suggest_command" : "run_command");
            clickEvent.put("value", command);
        }
        if (tooltip != null) {
            Map<String, Object> hoverEvent = new HashMap<>();
            map.put("hoverEvent", hoverEvent);
            hoverEvent.put("action", "show_text");
            hoverEvent.put("value", format(tooltip));
        }
        return map;
    }

    public static Object button(ChatColor color, String chat, String tooltip, String command) {
        return button(color, chat, null, tooltip, command);
    }

    public static Object button(String chat, String tooltip, String command) {
        return button(ChatColor.WHITE, chat, null, tooltip, command);
    }

    public static String camelCase(String msg) {
        StringBuilder sb = new StringBuilder();
        for (String tok: msg.split("_")) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(tok.substring(0, 1).toUpperCase());
            sb.append(tok.substring(1).toLowerCase());
        }
        return sb.toString();
    }

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
}

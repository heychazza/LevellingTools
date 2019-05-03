package net.chazza.levellingtools.util;

import org.bukkit.ChatColor;

public class StringUtil {
    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

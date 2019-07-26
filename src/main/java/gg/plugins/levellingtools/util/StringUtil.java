package gg.plugins.levellingtools.util;

import gg.plugins.levellingtools.LevellingTools;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class StringUtil
{
    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendActionbar(Player player, String message) {
        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Object packet = constructor.newInstance(icbc, (byte) 2);
            Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
    
    public static String getToolName(final String type, final Integer level, final LevellingTools levellingTools) {
        final FileConfiguration config = levellingTools.getConfig();
        final String configName = config.getString("level." + level + ".settings.format." + type + ".name");
        if ((configName == null || configName.isEmpty()) && level == 1) {
            return config.getString("format." + type + ".name");
        }
        return configName;
    }
    
    public static List<String> getToolLore(final String type, final Integer level, final LevellingTools levellingTools) {
        final FileConfiguration config = levellingTools.getConfig();
        final List<String> configLore = config.getStringList("level." + level + ".settings.format." + type + ".lore");
        if ((configLore == null || configLore.isEmpty()) && level == 1) {
            return config.getStringList("format." + type + ".lore");
        }
        return configLore;
    }
    
    public static ConfigurationSection getToolXp(final Integer level, final LevellingTools levellingTools) {
        final FileConfiguration config = levellingTools.getConfig();
        final ConfigurationSection experience = config.getConfigurationSection("level." + level + ".settings.experience");
        if (experience == null && level == 1) {
            return config.getConfigurationSection("settings.global.xp");
        }
        return experience;
    }
}

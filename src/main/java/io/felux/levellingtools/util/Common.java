package io.felux.levellingtools.util;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.api.Tool;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.storage.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.List;

public class Common {

    private static LevellingTools levellingTools = JavaPlugin.getPlugin(LevellingTools.class);

    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String prefix = "[LT]";

    public static void loading(String object) {
        sendConsoleMessage(prefix + " Loading " + object + "..");
    }

    public static void sendMessage(String object) {
        sendConsoleMessage(prefix + " " + object);
    }

    public static void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(translate(msg));
    }

    private static DecimalFormat decimalFormat = new DecimalFormat("#.#");

    public static String format(double doubleStr) {
        levellingTools.log("Formatting " + doubleStr + " to " + decimalFormat.format(doubleStr));
        return decimalFormat.format(doubleStr);
    }

    public static void sendActionbar(Player player, String message) {
        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Object packet = constructor.newInstance(icbc, (byte) 2);
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
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


    public static double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    public static int getProgress(PlayerData playerData) {
        Tool currentLvl = Tool.getCurrentLevel(playerData);
        Tool nextLevel = Tool.getNextLevel(playerData);

        double minXp = playerData.getXp() - currentLvl.getXpRequired();
        double maxXp = nextLevel.getXpRequired() - currentLvl.getXpRequired();

        double percent = calculatePercentage(minXp, maxXp);
        return (int) (percent > 100 ? 100 : percent);
    }

    public static String getProgressBar(final PlayerData playerData) {
        return getProgressBar(getProgress(playerData));
    }

    public static String getProgressBar(final double percentage) {
        int bars = levellingTools.getConfig().getInt("settings.progress-bar", 10);
        int filledBars = (int) (bars * (percentage / 100));
        int leftOver = bars - filledBars;

        levellingTools.log("Creating Progress Bar..");
        levellingTools.log("Filled Bars: " + filledBars);
        levellingTools.log("Left Over Bars: " + leftOver);

        StringBuilder sb = new StringBuilder();
        sb.append(Lang.PROGRESS_START.asString());
        sb.append(Lang.PROGRESS_COMPLETE.asString());

        for (int i = 0; i < filledBars; i++) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }

        sb.append(Lang.PROGRESS_INCOMPLETE.asString());
        for (int i = 0; i < leftOver; i++) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }

        sb.append(Lang.PROGRESS_END.asString());
        return sb.toString();
    }
}
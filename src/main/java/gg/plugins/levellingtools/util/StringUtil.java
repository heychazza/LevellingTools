package gg.plugins.levellingtools.util;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import gg.plugins.levellingtools.LevellingTools;
import java.lang.reflect.Field;
import java.util.Objects;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class StringUtil
{
    public static String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void sendActionbar(final Player player, final String message) {
        if (player == null || message == null) {
            return;
        }
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
        try {
            final Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            final Object craftPlayer = craftPlayerClass.cast(player);
            final Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            final Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            final Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            final Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) {
                method = chat.getDeclaredMethod("a", String.class);
            }
            final Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(Objects.requireNonNull(method).invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(String.class).newInstance(message);
            final Object packetPlayOutChat = ppoc.getConstructor(chatBaseComponent, Byte.TYPE).newInstance(object, 2);
            final Method handle = craftPlayerClass.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
            final Object iCraftPlayer = handle.invoke(craftPlayer);
            final Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            final Object playerConnection = playerConnectionField.get(iCraftPlayer);
            final Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
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

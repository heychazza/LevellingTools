package net.chazza.levellingtools.util;

import net.chazza.levellingtools.LevellingTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class StringUtil {
    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);


        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(Objects.requireNonNull(method).invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getToolName(String type, Integer level, LevellingTools levellingTools) {
        FileConfiguration config = levellingTools.getConfig();

        String configName = config.getString("level." + level + ".settings.format." + type + ".name");
        boolean isBlank = configName == null || configName.isEmpty();

        if (isBlank) {
            if (level == 1) return config.getString("settings.format." + type + ".name");
            else return config.getString("level." + (level - 1) + ".settings.format." + type + ".name");
        }

        return configName;
    }

    public static List<String> getToolLore(String type, Integer level, LevellingTools levellingTools) {
        FileConfiguration config = levellingTools.getConfig();

        List<String> configLore = config.getStringList("level." + level + ".settings.format." + type + ".lore");
        boolean isBlank = configLore == null || configLore.isEmpty();

        if (isBlank) {
            if (level == 1) return config.getStringList("settings.format." + type + ".lore");
            else return config.getStringList("level." + (level - 1) + ".settings.format." + type + ".lore");
        }

        return configLore;

    }
}

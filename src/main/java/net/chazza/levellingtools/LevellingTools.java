package net.chazza.levellingtools;

import com.google.common.collect.Maps;
import net.chazza.levellingtools.tool.LevellingTool;
import net.chazza.levellingtools.util.EnchantmentEnum;
import net.chazza.levellingtools.util.LevellingUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class LevellingTools extends JavaPlugin {

    public void onEnable() {
        //BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(this);
        saveDefaultConfig();

        new LevellingUtil().setBaseMulti(getConfig().getDouble("settings.level-multiplier"));

        for(String levelStr : getConfig().getConfigurationSection("level").getKeys(false)) {
            int toolLevel = Integer.valueOf(levelStr);

            Map<Enchantment, Integer> enchantments = Maps.newHashMap();
            for(String enchantmentStr : getConfig().getConfigurationSection("level." + levelStr + ".settings.enchantments").getKeys(false)) {
                int enchantLevel = getConfig().getInt("level." + toolLevel + ".settings.enchantments." + enchantmentStr);

                if(!EnchantmentEnum.exists(enchantmentStr)) {
                    getLogger().warning("Enchantment '" + enchantmentStr + "' for level " + toolLevel + " is invalid. Skipping!");
                    continue;
                }
                enchantments.put(EnchantmentEnum.valueOf(enchantmentStr).getEnchantment(), enchantLevel);
            }

            String configType = getConfig().getString("level." + levelStr + ".settings.type").toUpperCase();
            Material matType = Material.getMaterial(configType);
            if(matType == null) {
                getLogger().warning("Material '" + configType  + "' for level " + toolLevel + " is invalid. Skipping!");
                continue;
            }

            getLogger().info("[DEBUG] Level: " + toolLevel + " - Type: " + matType.name() + " - Enchantments: " + enchantments.size());
            new LevellingTool(toolLevel, enchantments, matType);
        }
    }

    public void onDisable() {
    }
}

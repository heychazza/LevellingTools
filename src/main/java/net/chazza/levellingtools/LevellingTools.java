package net.chazza.levellingtools;

import com.google.common.collect.Maps;
import net.chazza.levellingtools.events.ToolJoinEvent;
import net.chazza.levellingtools.events.ToolMineEvent;
import net.chazza.levellingtools.tool.LevellingTool;
import net.chazza.levellingtools.util.EnchantmentEnum;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevellingTools extends JavaPlugin {

    public void onEnable() {
        //BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(this);
        saveDefaultConfig();

        new ToolJoinEvent(this);
        new ToolMineEvent(this);

        for(String levelStr : getConfig().getConfigurationSection("level").getKeys(false)) {
            int toolLevel = Integer.valueOf(levelStr);
            int xpRequired = getConfig().getInt("level." + levelStr + ".settings.xp");

            String name = getConfig().getString("level." + levelStr + ".settings.name");
            List<String> lore = getConfig().getStringList("level." + levelStr + ".settings.lore");
            List<String> commands = getConfig().getStringList("level." + levelStr + ".commands");

            if(name == null || name.isEmpty()) name = getConfig().getString("format.pickaxe.name");
            if(lore == null || lore.size() == 0) lore = getConfig().getStringList("format.pickaxe.lore");
            if(commands == null || commands.size() == 0) commands = new ArrayList<>();

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
            LevellingTool.getTools().put(toolLevel, new LevellingTool(toolLevel, xpRequired, enchantments, name, lore, matType, commands));
        }
    }

    public void onDisable() {
    }

}

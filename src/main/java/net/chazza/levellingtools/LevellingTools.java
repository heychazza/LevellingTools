package net.chazza.levellingtools;

import com.google.common.collect.Maps;
import net.chazza.levellingtools.events.ToolJoinEvent;
import net.chazza.levellingtools.events.ToolMineEvent;
import net.chazza.levellingtools.events.ToolPreMineEvent;
import net.chazza.levellingtools.tool.BlockXP;
import net.chazza.levellingtools.tool.LevellingTool;
import net.chazza.levellingtools.util.EnchantmentEnum;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LevellingTools extends JavaPlugin {

    public void onEnable() {
        //BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(this);
        saveDefaultConfig();

        new ToolJoinEvent(this);
        new ToolPreMineEvent(this);
        new ToolMineEvent(this);

        getLogger().info("");
        getLogger().info("");
        getLogger().info("  _                        _  _  _                 _                 _      ");
        getLogger().info(" | |                      | || |(_)               | |               | |     ");
        getLogger().info(" | |      ___ __   __ ___ | || | _  _ __    __ _  | |_  ___    ___  | | ___ ");
        getLogger().info(" | |     / _ \\ \\ / // _ \\| || || || '_ \\  / _` | | __|/ _ \\  / _ \\ | |/ __|");
        getLogger().info(" | |____|  __/ \\ V /|  __/| || || || | | || (_| | | |_| (_) || (_) || |\\__ \\");
        getLogger().info(" |______|\\___|  \\_/  \\___||_||_||_||_| |_| \\__, |  \\__|\\___/  \\___/ |_||___/");
        getLogger().info("                                            __/ |                           ");
        getLogger().info("                                           |___/                            ");
        getLogger().info("");
        getLogger().info("");

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

            List<BlockXP> blockXp = new ArrayList<>();
            for(String blockStr : getConfig().getConfigurationSection("level." + levelStr + ".settings.experience").getKeys(false)) {
                if(blockStr.contains(";")) {
                    // Has Data
                    String[] blockData = blockStr.split(";", 2);
                    Material matType = Material.getMaterial(blockData[0]);
                    if(matType == null) {
                        getLogger().warning("Material '" + blockStr  + "' for level " + toolLevel + " is invalid. Skipping!");
                        continue;
                    }

                    Integer xp = getConfig().getInt("level." + toolLevel + ".settings.experience." + blockStr);
                    //getLogger().info("[DEBUG] Added material '" + matType.name() + "' with data (" + blockData[1] + "), giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, Integer.valueOf(blockData[1]), xp));
                } else {
                    // No Data
                    Material matType = Material.getMaterial(blockStr);
                    if(matType == null) {
                        getLogger().warning("Material '" + blockStr  + "' for level " + toolLevel + " is invalid. Skipping!");
                        continue;
                    }

                    Integer xp = getConfig().getInt("level." + toolLevel + ".settings.experience." + blockStr);
                    //getLogger().info("[DEBUG] Added material '" + matType.name() + "' with no data, giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, 0, xp));
                }
            }

            String configType = getConfig().getString("level." + levelStr + ".settings.type").toUpperCase();
            if(!Arrays.asList("WOOD", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                getLogger().warning("Material '" + configType  + "' for level " + toolLevel + " is invalid. Skipping!");
                continue;
            }

            //getLogger().info("[DEBUG] Level: " + toolLevel + " - Type: " + matType.name() + " - Enchantments: " + enchantments.size());
            LevellingTool.getTools().put(toolLevel, new LevellingTool(toolLevel, xpRequired, enchantments, blockXp, name, lore, configType, commands));
        }
    }

    public void onDisable() {
    }

}

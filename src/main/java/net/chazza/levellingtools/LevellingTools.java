package net.chazza.levellingtools;

import com.google.common.collect.Maps;
import net.chazza.levellingtools.config.ConfigWrapper;
import net.chazza.levellingtools.config.Lang;
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

    /*
    check if format (override = true),

    if(true) tool.setPickaxe(name, lore);
    else tool.setPickaxe(config.name, config.lore);
     */

    public void onEnable() {
        saveDefaultConfig();
        Lang.init(new ConfigWrapper(this, "lang.yml"));

        new MongoDB(this);

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

        getConfig().getConfigurationSection("level").getKeys(false).forEach(levelStr -> {
            int level = Integer.valueOf(levelStr);
            int xpRequired = getConfig().getInt("level." + levelStr + ".settings.xp");

            boolean pickaxeOverride = getConfig().getBoolean("level." + levelStr + ".settings.format.pickaxe.override");
            boolean axeOverride = getConfig().getBoolean("level." + levelStr + ".settings.format.axe.override");
            boolean shovelOverride = getConfig().getBoolean("level." + levelStr + ".settings.format.shovel.override");

            String pickaxeName;
            List<String> pickaxeLore;
            if(pickaxeOverride) {
                pickaxeName = getConfig().getString("level." + levelStr + ".settings.format.pickaxe.name");
                pickaxeLore = getConfig().getStringList("level." + levelStr + ".settings.format.pickaxe.lore");
            } else {
                pickaxeName = getConfig().getString("settings.format.pickaxe.name");
                pickaxeLore = getConfig().getStringList("settings.format.pickaxe.lore");
            }

            String axeName;
            List<String> axeLore;
            if(pickaxeOverride) {
                axeName = getConfig().getString("level." + levelStr + ".settings.format.axe.name");
                axeLore = getConfig().getStringList("level." + levelStr + ".settings.format.axe.lore");
            } else {
                axeName = getConfig().getString("settings.format.axe.name");
                axeLore = getConfig().getStringList("settings.format.axe.lore");
            }

            String shovelName;
            List<String> shovelLore;
            if(pickaxeOverride) {
                shovelName = getConfig().getString("level." + levelStr + ".settings.format.shovel.name");
                shovelLore = getConfig().getStringList("level." + levelStr + ".settings.format.shovel.lore");
            } else {
                shovelName = getConfig().getString("settings.format.shovel.name");
                shovelLore = getConfig().getStringList("settings.format.shovel.lore");
            }

            Map<Enchantment, Integer> enchantments = Maps.newHashMap();
            getConfig().getConfigurationSection("level." + levelStr + ".settings.enchantments").getKeys(false).forEach(enchantmentStr -> {
                int enchantLevel = getConfig().getInt("level." + levelStr + ".settings.enchantments." + enchantmentStr);

                if(!EnchantmentEnum.exists(enchantmentStr)) {
                    getLogger().warning("Enchantment '" + enchantmentStr + "' for level " + levelStr + " is invalid. Skipping!");
                    return;
                }
                enchantments.put(EnchantmentEnum.valueOf(enchantmentStr).getEnchantment(), enchantLevel);
            });


            List<BlockXP> blockXp = new ArrayList<>();
            getConfig().getConfigurationSection("level." + levelStr + ".settings.experience").getKeys(false).forEach(blockStr -> {
                if(blockStr.contains(";")) {
                    // Has Data
                    String[] blockData = blockStr.split(";", 2);
                    Material matType = Material.getMaterial(blockData[0]);
                    if(matType == null) {
                        getLogger().warning("Material '" + blockStr  + "' for level " + levelStr + " is invalid. Skipping!");
                        return;
                    }

                    Integer xp = getConfig().getInt("level." + levelStr + ".settings.experience." + blockStr);
                    //getLogger().info("[DEBUG] Added material '" + matType.name() + "' with data (" + blockData[1] + "), giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, Integer.valueOf(blockData[1]), xp));
                } else {
                    // No Data
                    Material matType = Material.getMaterial(blockStr);
                    if(matType == null) {
                        getLogger().warning("Material '" + blockStr  + "' for level " + levelStr + " is invalid. Skipping!");
                        return;
                    }

                    Integer xp = getConfig().getInt("level." + levelStr + ".settings.experience." + blockStr);
                    //getLogger().info("[DEBUG] Added material '" + matType.name() + "' with no data, giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, 0, xp));
                }
            });


            String configType = getConfig().getString("level." + levelStr + ".settings.type").toUpperCase();
            if(!Arrays.asList("WOOD", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                getLogger().warning("Material '" + configType  + "' for level " + levelStr + " is invalid. Skipping!");
                return;
            }

            List<String> commands = getConfig().getStringList("level." + levelStr + ".commands");

            LevellingTool levellingTool = new LevellingTool(level, xpRequired);
            levellingTool.setEnchantments(enchantments);
            levellingTool.setBlockXp(blockXp);
            levellingTool.setPickaxeName(pickaxeName);
            levellingTool.setPickaxeLore(pickaxeLore);
            levellingTool.setAxeName(axeName);
            levellingTool.setAxeLore(axeLore);
            levellingTool.setShovelName(shovelName);
            levellingTool.setShovelLore(shovelLore);
            levellingTool.setType(configType);
            levellingTool.setCommands(commands);

            LevellingTool.getTools().put(level, levellingTool);

        });


    }

    public void onDisable() {
    }

}

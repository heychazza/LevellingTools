package net.chazza.levellingtools.config;

import com.google.common.collect.Maps;
import dev.morphia.Datastore;
import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.tool.BlockXP;
import net.chazza.levellingtools.tool.LevellingTool;
import net.chazza.levellingtools.util.EnchantmentEnum;
import net.chazza.levellingtools.util.MongoDB;
import net.chazza.levellingtools.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class ConfigCache {

    private static LevellingTools plugin;
    private static HashMap<Integer, LevellingTool> tools;
    private static MongoDB mongoDB;

    public ConfigCache(LevellingTools plugin) {
        ConfigCache.plugin = plugin;
    }

    public static void setup() {
        ConfigCache.tools = Maps.newHashMap();
        ConfigCache.mongoDB = new MongoDB(
                plugin.getConfig().getString("settings.database.name"),
                plugin.getConfig().getString("settings.database.host"),
                plugin.getConfig().getInt("settings.database.port")
        );

        plugin.getConfig().getConfigurationSection("level").getKeys(false).forEach(levelStr -> {
            int level = Integer.valueOf(levelStr);
            int xpRequired = plugin.getConfig().getInt("level." + levelStr + ".settings.xp");

            String pickaxeName = StringUtil.getToolName("pickaxe", level, plugin);
            List<String> pickaxeLore = StringUtil.getToolLore("pickaxe", level, plugin);

            String axeName = StringUtil.getToolName("axe", level, plugin);
            List<String> axeLore = StringUtil.getToolLore("axe", level, plugin);

            String shovelName = StringUtil.getToolName("shovel", level, plugin);
            List<String> shovelLore = StringUtil.getToolLore("shovel", level, plugin);

            Map<Enchantment, Integer> enchantments = Maps.newHashMap();
            plugin.getConfig().getConfigurationSection("level." + levelStr + ".settings.enchantments").getKeys(false).forEach(enchantmentStr -> {
                int enchantLevel = plugin.getConfig().getInt("level." + levelStr + ".settings.enchantments." + enchantmentStr);

                if (!EnchantmentEnum.exists(enchantmentStr)) {
                    plugin.getLogger().warning(String.format("Skipping invalid enchantment (%s) for level %s.", enchantmentStr, levelStr));
                    return;
                }
                enchantments.put(EnchantmentEnum.valueOf(enchantmentStr).getEnchantment(), enchantLevel);
            });


            List<BlockXP> blockXp = new ArrayList<>();
            plugin.getConfig().getConfigurationSection("level." + levelStr + ".settings.experience").getKeys(false).forEach(blockStr -> {
                if (blockStr.contains(";")) {
                    // Has Data
                    String[] blockData = blockStr.split(";", 2);
                    Material matType = Material.getMaterial(blockData[0]);
                    if (matType == null) {
                        plugin.getLogger().warning(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    Integer xp = plugin.getConfig().getInt("level." + levelStr + ".settings.experience." + blockStr);
                    //plugin.getLogger().info("[DEBUG] Added material '" + matType.name() + "' with data (" + blockData[1] + "), giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, Integer.valueOf(blockData[1]), xp));
                } else {
                    // No Data
                    Material matType = Material.getMaterial(blockStr);
                    if (matType == null) {
                        plugin.getLogger().warning(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    Integer xp = plugin.getConfig().getInt("level." + levelStr + ".settings.experience." + blockStr);
                    //plugin.getLogger().info("[DEBUG] Added material '" + matType.name() + "' with no data, giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new BlockXP(matType, 0, xp));
                }
            });

            String configType = plugin.getConfig().getString("level." + levelStr + ".settings.type").toUpperCase();
            if (!Arrays.asList("WOOD", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                plugin.getLogger().warning(String.format("Skipping invalid tool type (%s) for level %s.", configType, levelStr));
                return;
            }

            List<String> commands = plugin.getConfig().getStringList("level." + levelStr + ".commands");
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

            getTools().put(level, levellingTool);
        });
    }

    public static HashMap<Integer, LevellingTool> getTools() {
        return tools;
    }

    public static Datastore getDB() {
        return mongoDB.getDatabase();
    }

    public static LevellingTools getPlugin() {
        return plugin;
    }
}

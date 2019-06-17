package gg.plugins.levellingtools.config;

import com.google.common.collect.Maps;
import dev.morphia.Datastore;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.tool.BlockXP;
import gg.plugins.levellingtools.tool.LevellingTool;
import gg.plugins.levellingtools.util.Enchantments;
import gg.plugins.levellingtools.util.MongoDB;
import gg.plugins.levellingtools.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class ConfigCache {

    private static LevellingTools plugin;
    private static HashMap<Integer, LevellingTool> tools;
    private static MongoDB mongoDB;

    private static List<String> blacklistedWorlds;
    private static List<String> blacklistedRegions;
    private static boolean cancelBlacklistedWorld;
    private static boolean cancelBlacklistedRegion;

    private static List<String> globalActions;

    private static String lastPickName;
    private static String lastAxeName;
    private static String lastShovelName;
    private static List<String> lastPickLore;
    private static List<String> lastAxeLore;
    private static List<String> lastShovelLore;
    private static Set<String> lastBlockXp;

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
            int xpRequired = plugin.getConfig().getInt("level." + levelStr + ".settings.xp", -1);

            String pickaxeName = StringUtil.getToolName("pickaxe", level, plugin);
            List<String> pickaxeLore = StringUtil.getToolLore("pickaxe", level, plugin);

            if (pickaxeName == null || pickaxeName.isEmpty()) pickaxeName = lastPickName;
            if (pickaxeLore == null || pickaxeLore.size() == 0) pickaxeLore = lastPickLore;

            lastPickName = pickaxeName;
            lastPickLore = pickaxeLore;

            String axeName = StringUtil.getToolName("axe", level, plugin);
            List<String> axeLore = StringUtil.getToolLore("axe", level, plugin);

            if (axeName == null || axeName.isEmpty()) axeName = lastAxeName;
            if (axeLore == null || axeLore.size() == 0) axeLore = lastAxeLore;

            lastAxeName = axeName;
            lastAxeLore = axeLore;

            String shovelName = StringUtil.getToolName("shovel", level, plugin);
            List<String> shovelLore = StringUtil.getToolLore("shovel", level, plugin);

            if (shovelName == null || shovelName.isEmpty()) shovelName = lastShovelName;
            if (shovelLore == null || shovelLore.size() == 0) shovelLore = lastShovelLore;

            lastShovelName = shovelName;
            lastShovelLore = shovelLore;

            Map<Enchantment, Integer> enchantments = Maps.newHashMap();
            plugin.getConfig().getConfigurationSection("level." + levelStr + ".settings.enchantments").getKeys(false).forEach(enchantmentStr -> {
                int enchantLevel = plugin.getConfig().getInt("level." + levelStr + ".settings.enchantments." + enchantmentStr, 0);

                if (!Enchantments.exists(enchantmentStr)) {
                    plugin.getLogger().warning(String.format("Skipping invalid enchantment (%s) for level %s.", enchantmentStr, levelStr));
                    return;
                }
                enchantments.put(Enchantments.valueOf(enchantmentStr).getEnchantment(), enchantLevel);
            });

            List<BlockXP> blockXp = new ArrayList<>();
            Set<String> configBlockXp = StringUtil.getToolXp(level, plugin).getKeys(false);

            if (configBlockXp == null || configBlockXp.size() == 0) configBlockXp = lastBlockXp;
            lastBlockXp = configBlockXp;

            configBlockXp.forEach(blockStr -> {
                if (blockStr.contains(";")) {
                    // Has Data
                    String[] blockData = blockStr.split(";", 2);
                    Material matType = Material.getMaterial(blockData[0]);
                    if (matType == null) {
                        plugin.getLogger().warning(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    Integer xp = plugin.getConfig().getInt("level." + levelStr + ".settings.experience." + blockStr, 0);
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

            String configType = plugin.getConfig().getString("level." + levelStr + ".settings.type", "WOOD").toUpperCase();
            if (!Arrays.asList("WOOD", "STONE", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                plugin.getLogger().warning(String.format("Skipping invalid tool type (%s) for level %s.", configType, levelStr));
                return;
            }

            List<String> actions = plugin.getConfig().getStringList("level." + levelStr + ".actions");
            LevellingTool levellingTool = new LevellingTool(level, xpRequired);
            levellingTool.setRestriction(plugin.getConfig().getBoolean("level." + levelStr + ".settings.restrict", false));
            levellingTool.setEnchantments(enchantments);
            levellingTool.setBlockXp(blockXp);
            levellingTool.setPickaxeName(pickaxeName);
            levellingTool.setPickaxeLore(pickaxeLore);
            levellingTool.setAxeName(axeName);
            levellingTool.setAxeLore(axeLore);
            levellingTool.setShovelName(shovelName);
            levellingTool.setShovelLore(shovelLore);
            levellingTool.setType(configType);
            levellingTool.setActions(actions);

            getTools().put(level, levellingTool);
        });

        blacklistedWorlds = new ArrayList<>();
        blacklistedRegions = new ArrayList<>();

        blacklistedWorlds.addAll(plugin.getConfig().getStringList("settings.blacklist.world.list"));
        blacklistedRegions.addAll(plugin.getConfig().getStringList("settings.blacklist.region.list"));

        cancelBlacklistedWorld = plugin.getConfig().getBoolean("settings.blacklist.world.cancel");
        cancelBlacklistedRegion = plugin.getConfig().getBoolean("settings.blacklist.region.cancel");

        globalActions = plugin.getConfig().getStringList("settings.global.actions");
    }

    public static HashMap<Integer, LevellingTool> getTools() {
        return tools;
    }

    public static Datastore getDB() {
        return mongoDB.getDatabase();
    }

    public static List<String> getBlacklistedWorlds() {
        return blacklistedWorlds;
    }

    public static List<String> getBlacklistedRegions() {
        return blacklistedRegions;
    }

    public static boolean cancelBlacklistedWorld() {
        return cancelBlacklistedWorld;
    }

    public static boolean cancelBlacklistedRegion() {
        return cancelBlacklistedRegion;
    }

    public static List<String> getGlobalActions() {
        return globalActions;
    }

    public static LevellingTools getPlugin() {
        return plugin;
    }
}

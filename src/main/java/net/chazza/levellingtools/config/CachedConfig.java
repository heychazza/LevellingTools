package net.chazza.levellingtools.config;

import com.google.common.collect.Maps;
import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.api.Booster;
import net.chazza.levellingtools.api.Tool;
import net.chazza.levellingtools.util.Common;
import net.chazza.levellingtools.util.Enchant;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.stream.Stream;

public class CachedConfig {

    private static LevellingTools plugin;
    private static Map<Integer, Tool> tools;
    private static List<Booster> boosters;

    private static List<String> blacklistedWorlds;
    private static List<String> blacklistedRegions;

    private static List<String> globalActions;

    private static String lastPickName;
    private static String lastAxeName;
    private static String lastShovelName;
    private static List<String> lastPickLore;
    private static List<String> lastAxeLore;
    private static List<String> lastShovelLore;
    private static Set<String> lastBlockXp;

    private static String enchantPrefix;
    private static boolean giveOnJoin;
    private static boolean debug;

    public static List<Material> pickaxeBlocks;
    public static List<Material> axeBlocks;
    public static List<Material> shovelBlocks;


    public CachedConfig(LevellingTools plugin) {
        CachedConfig.plugin = plugin;
    }

    public static boolean debugMode() {
        return debug;
    }

    public static List<Booster> getMultipliers() {
        return boosters;
    }

    public static Map<Integer, Tool> getTools() {
        return tools;
    }

    public static Booster getMultiplier(Player player) {
        for (Booster booster : getMultipliers()) {
            if (player.hasPermission(booster.getPermission())) return booster;
        }

        return null;
    }

    public static String getEnchantPrefix() {
        return enchantPrefix;
    }

    public static List<String> getBlacklistedWorlds() {
        return blacklistedWorlds;
    }

    public static List<String> getBlacklistedRegions() {
        return blacklistedRegions;
    }

    public static List<String> getGlobalActions() {
        return globalActions;
    }

    public static boolean canGiveOnJoin() {
        return giveOnJoin;
    }

    public static void setup() {
        CachedConfig.tools = Maps.newHashMap();
        CachedConfig.boosters = new ArrayList<>();

        FileConfiguration data = plugin.getConfig();

        if (!plugin.isEnabled()) return;

        enchantPrefix = Common.translate(data.getString("settings.enchants.prefix", "&7"));
        giveOnJoin = data.getBoolean("settings.give_on_join", true);
        debug = data.getBoolean("settings.debug", false);
        pickaxeBlocks = new ArrayList<>();
        axeBlocks = new ArrayList<>();
        shovelBlocks = new ArrayList<>();

        plugin.getConfig().getStringList("settings.blocks.pickaxe").forEach(pickaxeBlock -> {
            if (Material.getMaterial(pickaxeBlock) == null) {
                plugin.getLogger().warning("Material '" + pickaxeBlock + "' for pickaxe blocks is invalid. Skipping!");
                return;
            }
            pickaxeBlocks.add(Material.valueOf(pickaxeBlock));
        });

        plugin.getConfig().getStringList("settings.blocks.shovel").forEach(shovelBlock -> {
            if (Material.getMaterial(shovelBlock) == null) {
                plugin.getLogger().warning("Material '" + shovelBlock + "' for shovel blocks is invalid. Skipping!");
                return;
            }
            shovelBlocks.add(Material.valueOf(shovelBlock));
        });

        plugin.getConfig().getStringList("settings.blocks.axe").forEach(axeBlock -> {
            if (Material.getMaterial(axeBlock) == null) {
                plugin.getLogger().warning("Material '" + axeBlock + "' for axe blocks is invalid. Skipping!");
                return;
            }
            axeBlocks.add(Material.valueOf(axeBlock));
        });

        Objects.requireNonNull(data.getConfigurationSection("settings.boosters")).getKeys(false).forEach(multiId -> {
            double multiplier = data.getDouble("settings.boosters." + multiId, 1.0);

            if (multiplier < 1.0) {
                plugin.getLogger().warning("The booster '" + multiId + "' has a value lower than expected and has been set to 1.0.");
                multiplier = 1.0;
            }

            boolean exists = false;
            for (Booster multiObj : boosters) {
                if (multiObj.getId().equalsIgnoreCase(multiId)) {
                    plugin.getLogger().warning("The booster '" + multiId + "' already exists.");
                    exists = true;
                }
            }

            if (!exists) boosters.add(new Booster(multiId, multiplier));
        });

        data.getConfigurationSection("level").getKeys(false).forEach(levelStr -> {
            int level = Integer.valueOf(levelStr);
            double xpRequired = data.getDouble("level." + levelStr + ".settings.xp", -1);

            boolean useOneFormat = data.getBoolean("settings.use_one_format", false);

            String pickaxeName = Common.getToolName("pickaxe", level, plugin);
            List<String> pickaxeLore = Common.getToolLore("pickaxe", level, plugin);

            if (pickaxeName == null || pickaxeName.isEmpty()) pickaxeName = lastPickName;
            if (pickaxeLore == null || pickaxeLore.size() == 0) pickaxeLore = lastPickLore;

            lastPickName = pickaxeName;
            lastPickLore = pickaxeLore;

            String axeName = useOneFormat ? pickaxeName : Common.getToolName("axe", level, plugin);
            List<String> axeLore = useOneFormat ? pickaxeLore : Common.getToolLore("axe", level, plugin);

            if (axeName == null || axeName.isEmpty()) axeName = lastAxeName;
            if (axeLore == null || axeLore.size() == 0) axeLore = lastAxeLore;

            lastAxeName = axeName;
            lastAxeLore = axeLore;

            String shovelName = useOneFormat ? pickaxeName : Common.getToolName("shovel", level, plugin);
            List<String> shovelLore = useOneFormat ? pickaxeLore : Common.getToolLore("shovel", level, plugin);

            if (shovelName == null || shovelName.isEmpty()) shovelName = lastShovelName;
            if (shovelLore == null || shovelLore.size() == 0) shovelLore = lastShovelLore;

            lastShovelName = shovelName;
            lastShovelLore = shovelLore;

            Map<Enchantment, Integer> vanillaEnchants = Maps.newHashMap();
            Map<String, String> customEnchants = Maps.newHashMap();

            if (data.getConfigurationSection("level." + levelStr + ".settings.enchants") != null) {
                data.getKeys(false).forEach(enchantmentStr -> {

                    if (!Enchant.exists(enchantmentStr)) {
                        customEnchants.put(enchantmentStr, data.getString("level." + levelStr + ".settings.enchants." + enchantmentStr, "I"));
                        return;
                    }

                    int enchantLevel = data.getInt("level." + levelStr + ".settings.enchants." + enchantmentStr, 0);
                    vanillaEnchants.put(Enchant.valueOf(enchantmentStr).getEnchantment(), enchantLevel);
                });

            }

            List<Tool.BlockXP> blockXp = new ArrayList<>();
            Set<String> configBlockXp = Common.getToolXp(level, plugin).getKeys(false);

            if (configBlockXp.size() == 0) configBlockXp = lastBlockXp;
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

                    int xp = data.getInt("level." + levelStr + ".settings.experience." + blockStr, 0);
                    //plugin.getLogger().info("[DEBUG] Added material '" + matType.name() + "' with data (" + blockData[1] + "), giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new Tool.BlockXP(matType, Integer.valueOf(blockData[1]), xp));
                } else {
                    // No Data
                    Material matType = Material.getMaterial(blockStr);
                    if (matType == null) {
                        plugin.getLogger().warning(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    int xp = data.getInt("level." + levelStr + ".settings.experience." + blockStr, 0);
                    //plugin.getLogger().info("[DEBUG] Added material '" + matType.name() + "' with no data, giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new Tool.BlockXP(matType, 0, xp));
                }
            });

            String configType = data.getString("level." + levelStr + ".settings.type", "WOOD").toUpperCase();
            if (!Arrays.asList("WOOD", "STONE", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                plugin.getLogger().warning(String.format("Skipping invalid tool type (%s) for level %s.", configType, levelStr));
                return;
            }

            List<ItemFlag> itemFlags = new ArrayList<>();
            data.getStringList("level." + levelStr + ".settings.flags").forEach(itemFlag -> {
                boolean flagExists = Stream.of(ItemFlag.values()).anyMatch(e -> e.name().equalsIgnoreCase(itemFlag));
                if (flagExists) {
                    itemFlags.add(ItemFlag.valueOf(itemFlag));
                } else {
                    plugin.getLogger().warning("Item flag '" + itemFlag + "' is invalid.");
                }
            });

            List<String> actions = data.getStringList("level." + levelStr + ".actions");
            Tool tool = new Tool(level, xpRequired);
            tool.setRestriction(data.getBoolean("level." + levelStr + ".settings.restrict", false));
            tool.setVanillaEnchants(vanillaEnchants);
            tool.setCustomEnchants(customEnchants);
            tool.setBlockXp(blockXp);
            tool.setPickaxeName(pickaxeName);
            tool.setPickaxeLore(pickaxeLore);
            tool.setAxeName(axeName);
            tool.setAxeLore(axeLore);
            tool.setShovelName(shovelName);
            tool.setShovelLore(shovelLore);
            tool.setType(configType);
            tool.setActions(actions);
            tool.setItemFlags(itemFlags);
            tool.createItem();

            getTools().put(level, tool);
        });

        blacklistedWorlds = new ArrayList<>();
        blacklistedRegions = new ArrayList<>();

        blacklistedWorlds.addAll(data.getStringList("settings.blacklist.world.list"));
        blacklistedRegions.addAll(data.getStringList("settings.blacklist.region.list"));

        globalActions = data.getStringList("settings.global.actions");
    }

    public static LevellingTools getPlugin() {
        return plugin;
    }

}
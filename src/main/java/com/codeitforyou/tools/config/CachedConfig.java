package com.codeitforyou.tools.config;

import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.lib.api.xseries.XEnchantment;
import com.codeitforyou.lib.api.xseries.XMaterial;
import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.api.Booster;
import com.codeitforyou.tools.api.Tool;
import com.codeitforyou.tools.util.Common;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Stream;

public class CachedConfig {

    private static final Tools plugin = (Tools) JavaPlugin.getProvidingPlugin(Tools.class);
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
    private static boolean firstJoinOnly;
    private static boolean debug;

    public static List<Material> pickaxeBlocks;
    public static List<Material> axeBlocks;
    public static List<Material> shovelBlocks;

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

    public static boolean isFirstJoinOnly() {
        return firstJoinOnly;
    }

    public static void setup() {
        CachedConfig.tools = Maps.newHashMap();
        CachedConfig.boosters = new ArrayList<>();

        FileConfiguration data = plugin.getConfig();

        if (!plugin.isEnabled()) return;

        enchantPrefix = StringUtil.translate(data.getString("settings.enchants.prefix", "&7"));
        giveOnJoin = data.getBoolean("settings.give_on_join", true);
        firstJoinOnly = data.getBoolean("settings.first_join_only", false);
        debug = data.getBoolean("settings.debug", false);
        pickaxeBlocks = new ArrayList<>();
        axeBlocks = new ArrayList<>();
        shovelBlocks = new ArrayList<>();

        plugin.log("Enchantment prefix set to '" + enchantPrefix + "'.");
        plugin.log("Give on join set to '" + giveOnJoin + "'.");

        for (String pickaxeBlock : plugin.getConfig().getStringList("settings.blocks.pickaxe")) {
            if (Material.getMaterial(pickaxeBlock) == null) {
                plugin.log("Material '" + pickaxeBlock + "' for pickaxe blocks is invalid. Skipping!");
                continue;
            }
            pickaxeBlocks.add(XMaterial.matchXMaterial(pickaxeBlock).get().parseMaterial());
        }

        for (String shovelBlock : plugin.getConfig().getStringList("settings.blocks.shovel")) {
            if (Material.getMaterial(shovelBlock) == null) {
                plugin.log("Material '" + shovelBlock + "' for shovel blocks is invalid. Skipping!");
                continue;
            }
            shovelBlocks.add(XMaterial.matchXMaterial(shovelBlock).get().parseMaterial());
        }

        for (String axeBlock : plugin.getConfig().getStringList("settings.blocks.axe")) {
            if (Material.getMaterial(axeBlock) == null) {
                plugin.log("Material '" + axeBlock + "' for axe blocks is invalid. Skipping!");
                continue;
            }
            axeBlocks.add(XMaterial.matchXMaterial(axeBlock).get().parseMaterial());
        }

        ConfigurationSection boosterSection = data.getConfigurationSection("settings.boosters");

        if (boosterSection != null) {
            for (String multiId : boosterSection.getKeys(false)) {
                double multiplier = data.getDouble("settings.boosters." + multiId, 1.0);

                if (multiplier < 1.0) {
                    plugin.log("The booster '" + multiId + "' has a value lower than expected and has been set to 1.0.");
                    multiplier = 1.0;
                }

                boolean exists = false;
                for (Booster multiObj : boosters) {
                    if (multiObj.getId().equalsIgnoreCase(multiId)) {
                        plugin.log("The booster '" + multiId + "' already exists.");
                        exists = true;
                    }
                }

                if (!exists) boosters.add(new Booster(multiId, multiplier));
            }
        }

        ConfigurationSection levelSections = data.getConfigurationSection("level");
        if (levelSections == null) return;
        for (String levelStr : levelSections.getKeys(false)) {
            ConfigurationSection levelSection = levelSections.getConfigurationSection(levelStr);

            if (levelSection == null) {
                plugin.getLogger().warning("Level " + levelStr + " has invalid properties! Skipping..");
                continue;
            }

            int level = Integer.parseInt(levelStr);

            plugin.log(" ");
            plugin.log("Configuring level " + level + "..");
            double xpRequired = data.getDouble("level." + levelStr + ".settings.xp", -1);

            plugin.log("XP required is set to '" + xpRequired + "'.");

            boolean useOneFormat = data.getBoolean("settings.use_one_format", false);

            String pickaxeName = Common.getToolName("pickaxe", level, plugin);
            List<String> pickaxeLore = Common.getToolLore("pickaxe", level, plugin);

            if (pickaxeName == null || pickaxeName.isEmpty()) pickaxeName = lastPickName;
            if (pickaxeLore.size() == 0) pickaxeLore = lastPickLore;

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

            ConfigurationSection enchantSection = levelSection.getConfigurationSection("settings.enchants");
            if (enchantSection != null) {
                enchantSection.getKeys(false).forEach(enchantmentStr -> {

                    Optional<XEnchantment> enchantment = XEnchantment.matchXEnchantment(enchantmentStr);

                    if (!enchantment.isPresent()) {
                        customEnchants.put(enchantmentStr, levelSection.getString("settings.enchants." + enchantmentStr, "I"));
                        plugin.log("Registering '" + enchantmentStr + "' as a custom enchant.");
                        return;
                    }

                    int enchantLevel = levelSection.getInt("settings.enchants." + enchantmentStr, 0);
                    plugin.log("Registering '" + enchantmentStr + "' as a vanilla enchant.");
                    vanillaEnchants.put(enchantment.get().parseEnchantment(), enchantLevel);
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
                        plugin.log(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    int xp = levelSection.getInt("settings.experience." + blockStr, 0);
//                    plugin.log("Added material '" + matType.name() + "' with data (" + blockData[1] + "), giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new Tool.BlockXP(matType, Integer.valueOf(blockData[1]), xp));
                } else {
                    // No Data
                    Material matType = Material.getMaterial(blockStr);
                    if (matType == null) {
                        plugin.log(String.format("Skipping invalid material (%s) for level %s.", blockStr, levelStr));
                        return;
                    }

                    int xp = levelSection.getInt("settings.experience." + blockStr, 0);
//                    plugin.log("Added material '" + matType.name() + "' with no data, giving " + xp + " xp to level " + toolLevel + ".");
                    blockXp.add(new Tool.BlockXP(matType, 0, xp));
                }
            });

            String configType = levelSection.getString("settings.type", "WOOD").toUpperCase();
            if (!Arrays.asList("WOOD", "STONE", "IRON", "GOLD", "DIAMOND").contains(configType)) {
                plugin.log(String.format("Skipping invalid tool type (%s) for level %s.", configType, levelStr));
                continue;
            }

            List<ItemFlag> itemFlags = new ArrayList<>();
            levelSection.getStringList("settings.flags").forEach(itemFlag -> {
                boolean flagExists = Stream.of(ItemFlag.values()).anyMatch(e -> e.name().equalsIgnoreCase(itemFlag));
                if (flagExists) {
                    itemFlags.add(ItemFlag.valueOf(itemFlag));
                } else {
                    plugin.log("Item flag '" + itemFlag + "' is invalid.");
                }
            });

            List<String> actions = levelSection.getStringList("actions");
            Tool tool = new Tool(level, xpRequired);
            tool.setRestriction(levelSection.getBoolean("settings.restrict", false));
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
        }

        blacklistedWorlds = new ArrayList<>();
        blacklistedRegions = new ArrayList<>();

        blacklistedWorlds.addAll(data.getStringList("settings.blacklist.world.list"));
        blacklistedRegions.addAll(data.getStringList("settings.blacklist.region.list"));

        globalActions = data.getStringList("settings.global.actions");
    }

    public static Tools getPlugin() {
        return plugin;
    }
}
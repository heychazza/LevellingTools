package gg.plugins.levellingtools.tool;

import de.tr7zw.itemnbtapi.NBTItem;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.entity.PlayerEntity;
import gg.plugins.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class LevellingTool {
    private int level;
    private int xpRequired;
    private boolean restrict;
    private Map<Enchantment, Integer> enchantments;
    private List<BlockXP> blockXp;
    private String type;
    private String pickaxeName;
    private List<String> pickaxeLore;
    private String axeName;
    private List<String> axeLore;
    private String shovelName;
    private List<String> shovelLore;
    private List<String> actions;
    private static List<Material> axeBlocks;
    private static List<Material> pickaxeBlocks;
    private static List<Material> shovelBlocks;
    private static LevellingTools tools;

    public static LevellingTool getPlayerTool(final PlayerEntity playerEntity) {
        return ConfigCache.getTools().values().stream().filter(tool -> playerEntity.getExperience() >= tool.getXpRequired()).reduce((first, second) -> second).orElse(null);
    }

    private static String replaceVariables(final String str, final PlayerEntity player) {
        final LevellingTool nextLevel = (ConfigCache.getTools().size() > player.getLevel()) ? ConfigCache.getTools().get(player.getLevel() + 1) : ConfigCache.getTools().get(player.getLevel());
        return str.replace("{player}", player.getUsername()).replace("{blocks}", String.valueOf(player.getBlocksBroken())).replace("{currentxp}", String.valueOf(player.getExperience())).replace("{requiredxp}", String.valueOf(nextLevel.getXpRequired())).replace("{level}", String.valueOf(player.getLevel())).replace("{progress}", String.valueOf(player.getExperience() * 100 / nextLevel.getXpRequired())).replace("{progressbar}", getProgressBar(player));
    }

    public static int getProgress(final PlayerEntity player, final LevellingTool nextLevel) {
        return (int) (player.getExperience() / (float) nextLevel.getXpRequired());
    }

    public static String getProgressBar(final PlayerEntity player) {
        final LevellingTool nextLevel = (ConfigCache.getTools().size() > player.getLevel()) ? ConfigCache.getTools().get(player.getLevel() + 1) : ConfigCache.getTools().get(player.getLevel());
        final int progressBars = 10 * getProgress(player, nextLevel);
        final int leftOver = 10 - progressBars;
        final StringBuilder sb = new StringBuilder();
        sb.append(Lang.PROGRESS_START.asString());
        sb.append(Lang.PROGRESS_COMPLETE.asString());
        for (int i = 0; i < progressBars; ++i) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }
        sb.append(Lang.PROGRESS_INCOMPLETE.asString());
        for (int i = 0; i < leftOver; ++i) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }
        sb.append(Lang.PROGRESS_END.asString());
        return sb.toString();
    }

    public static ItemStack getItemStack(final Player player, final Block block) {
        final PlayerEntity user = PlayerEntity.getUser(player.getUniqueId());
        final LevellingTool tool = getPlayerTool(user);
        final Material toolType = getType(block, tool.getType());
        final ItemStack toolItem = new ItemStack(toolType);
        final ItemMeta toolMeta = toolItem.getItemMeta();
        String toolName;
        if (toolType.name().contains("PICKAXE")) {
            toolName = tool.getPickaxeName();
        } else if (toolType.name().contains("AXE")) {
            toolName = tool.getAxeName();
        } else {
            toolName = tool.getShovelName();
        }
        toolName = replaceVariables(toolName, user);
        toolMeta.setDisplayName(StringUtil.translate(toolName));
        List<String> toolLore;
        if (toolType.name().contains("PICKAXE")) {
            toolLore = tool.getPickaxeLore();
        } else if (toolType.name().contains("AXE")) {
            toolLore = tool.getAxeLore();
        } else {
            toolLore = tool.getShovelLore();
        }
        final List<String> lore = new ArrayList<String>();
        toolLore.forEach(localLore -> lore.add(StringUtil.translate(replaceVariables(localLore, user))));
        toolMeta.setLore(lore);
        toolItem.setItemMeta(toolMeta);
        tool.getEnchantments().forEach(toolItem::addUnsafeEnchantment);
        final NBTItem nbtItem = new NBTItem(toolItem);
        nbtItem.setString("omnitool", player.getUniqueId().toString());
        return nbtItem.getItem();
    }

    public LevellingTool(final int level, final int xpRequired) {
        this.level = level;
        this.xpRequired = xpRequired;
    }

    public int getLevel() {
        return this.level;
    }

    public int getXpRequired() {
        return this.xpRequired;
    }

    public boolean isRestricted() {
        return this.restrict;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    public List<BlockXP> getBlockXp() {
        return this.blockXp;
    }

    public String getType() {
        return this.type;
    }

    public String getPickaxeName() {
        return this.pickaxeName;
    }

    public String getAxeName() {
        return this.axeName;
    }

    public String getShovelName() {
        return this.shovelName;
    }

    public List<String> getPickaxeLore() {
        return this.pickaxeLore;
    }

    public List<String> getAxeLore() {
        return this.axeLore;
    }

    public List<String> getShovelLore() {
        return this.shovelLore;
    }

    public List<String> getActions() {
        return this.actions;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setXpRequired(final int xpRequired) {
        this.xpRequired = xpRequired;
    }

    public void setRestriction(final boolean restrict) {
        this.restrict = restrict;
    }

    public void setEnchantments(final Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setBlockXp(final List<BlockXP> blockXp) {
        this.blockXp = blockXp;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setPickaxeName(final String pickaxeName) {
        this.pickaxeName = pickaxeName;
    }

    public void setPickaxeLore(final List<String> pickaxeLore) {
        this.pickaxeLore = pickaxeLore;
    }

    public void setAxeName(final String axeName) {
        this.axeName = axeName;
    }

    public void setAxeLore(final List<String> axeLore) {
        this.axeLore = axeLore;
    }

    public void setShovelName(final String shovelName) {
        this.shovelName = shovelName;
    }

    public void setShovelLore(final List<String> shovelLore) {
        this.shovelLore = shovelLore;
    }

    public void setActions(final List<String> actions) {
        this.actions = actions;
    }

    public void executeActions(final Player player, final boolean fireGlobalActions) {
        if (fireGlobalActions) {
            ConfigCache.getGlobalActions().forEach(globalAction -> {
                globalAction = globalAction.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), globalAction);
                return;
            });
        }
        this.getActions().forEach(action -> {
            action = action.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), action);
        });
    }

    public Integer getXpFromBlock(final Block block) {
        final Optional<BlockXP> optionalBlockXP = this.getBlockXp().stream().filter(blockXp -> blockXp.getBlock() == block.getType() && blockXp.getData() == block.getData()).findFirst();
        return optionalBlockXP.isPresent() ? optionalBlockXP.get().getExperience() : 0;
    }

    public static int getOmnitoolSlot(final Player player) {
        int i = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item != null && new NBTItem(item).hasKey("omnitool")) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static Material getType(final Block block, final String type) {
        final Material pickaxeType = Material.valueOf((type + "_PICKAXE").toUpperCase());
        final Material axeType = Material.valueOf((type + "_AXE").toUpperCase());
        final Material shovelType = Material.valueOf((type + "_SPADE").toUpperCase());
        if (block != null && block.getType() != Material.AIR) {
            if (LevellingTool.pickaxeBlocks.contains(block.getType())) {
                return pickaxeType;
            }
            if (LevellingTool.axeBlocks.contains(block.getType()) && LevellingTool.tools.getConfig().getBoolean("global.omnitool.axe")) {
                return axeType;
            }
            if (LevellingTool.shovelBlocks.contains(block.getType()) && LevellingTool.tools.getConfig().getBoolean("global.omnitool.shovel")) {
                return shovelType;
            }
        }
        return pickaxeType;
    }

    static {
        LevellingTool.axeBlocks = Arrays.asList(Material.LOG, Material.LOG_2, Material.WOOD, Material.WOOD_STEP, Material.WOOD_DOUBLE_STEP, Material.WOOD_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS, Material.WORKBENCH, Material.LADDER, Material.RAILS, Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL, Material.SIGN, Material.SIGN_POST, Material.PUMPKIN, Material.JACK_O_LANTERN, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.FENCE, Material.FENCE_GATE);
        LevellingTool.pickaxeBlocks = Arrays.asList(Material.STONE, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.SANDSTONE, Material.OBSIDIAN, Material.COAL_ORE, Material.IRON_ORE, Material.IRON_BLOCK, Material.LAPIS_ORE, Material.LAPIS_BLOCK, Material.GOLD_ORE, Material.GOLD_BLOCK, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE, Material.REDSTONE_BLOCK, Material.EMERALD_ORE, Material.EMERALD_BLOCK, Material.DIAMOND_ORE, Material.DIAMOND_BLOCK, Material.STEP, Material.DOUBLE_STEP, Material.COBBLESTONE_STAIRS, Material.COBBLE_WALL, Material.BRICK, Material.BRICK_STAIRS, Material.ICE, Material.PACKED_ICE, Material.NETHERRACK, Material.NETHER_BRICK, Material.NETHER_BRICK_STAIRS, Material.NETHER_FENCE, Material.ANVIL, Material.QUARTZ_ORE, Material.QUARTZ_BLOCK, Material.QUARTZ_STAIRS, Material.CLAY_BRICK, Material.STAINED_CLAY, Material.HARD_CLAY, Material.FURNACE, Material.DISPENSER, Material.BREWING_STAND, Material.CAULDRON, Material.HOPPER);
        LevellingTool.shovelBlocks = Arrays.asList(Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SNOW, Material.SNOW_BLOCK, Material.CLAY, Material.SOUL_SAND, Material.MYCEL);
        LevellingTool.tools = (LevellingTools) JavaPlugin.getPlugin((Class) LevellingTools.class);
    }
}
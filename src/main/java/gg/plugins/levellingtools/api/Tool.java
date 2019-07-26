package gg.plugins.levellingtools.api;

import de.tr7zw.itemnbtapi.NBTItem;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.config.CachedConfig;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.storage.PlayerData;
import gg.plugins.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Tool {
    private int level;
    private double xpRequired;
    private boolean restrict = false;
    private Map<Enchantment, Integer> vanillaEnchants;
    private Map<String, String> customEnchants;
    private List<BlockXP> blockXp;
    private String type = "WOOD";
    private String pickaxeName = null;
    private List<String> pickaxeLore = null;
    private String axeName = null;
    private List<String> axeLore = null;
    private String shovelName = null;
    private List<String> shovelLore = null;
    private List<String> actions;
    private List<ItemFlag> itemFlags;
    private int bars = 10;
    private static LevellingTools tools = JavaPlugin.getPlugin(LevellingTools.class);

    public Tool(final int level, final double xpRequired) {
        this.level = level;
        this.xpRequired = xpRequired == -1 ? 0 : xpRequired;

        this.vanillaEnchants = new HashMap<>();
        this.customEnchants = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public static Tool getPlayerTool(final PlayerData playerData) {
        return CachedConfig.getTools().values().stream().filter(tool -> playerData.getXp() >= tool.getXpRequired()).reduce((first, second) -> second).orElse(null);
    }

    private static String replaceVariables(final String message, final String player, final String blocks, final String currentXp, final String requiredXp, final String level, final String progress, final String progressBar) {
        return message.replace("{player}", player)
                .replace("{blocks}", blocks)
                .replace("{currentxp}", currentXp)
                .replace("{requiredxp}", requiredXp)
                .replace("{level}", level)
                .replace("{progress}", progress)
                .replace("{progressbar}", progressBar);
    }

    public static Tool getPreviousLevel(PlayerData playerData) {
        return CachedConfig.getTools().size() > 1 && playerData.getLevel() > 1 ? CachedConfig.getTools().get(playerData.getLevel() - 1) : CachedConfig.getTools().get(playerData.getLevel());
    }

    public static Tool getCurrentLevel(PlayerData playerData) {
        return playerData.getLevel() < CachedConfig.getTools().size() ? CachedConfig.getTools().get(playerData.getLevel()) : CachedConfig.getTools().get(CachedConfig.getTools().size());
    }

    public static Tool getNextLevel(PlayerData playerData) {
        return CachedConfig.getTools().size() > playerData.getLevel() ? CachedConfig.getTools().get(playerData.getLevel() + 1) : CachedConfig.getTools().get(playerData.getLevel());
    }

    public static double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }

    public static int getProgress(PlayerData playerData) {
        Tool currentLvl = getCurrentLevel(playerData);
        Tool nextLevel = getNextLevel(playerData);

        double minXp = playerData.getXp() - currentLvl.getXpRequired();
        double maxXp = nextLevel.getXpRequired() - currentLvl.getXpRequired();

        double percent = calculatePercentage(minXp, maxXp);
        return (int) (percent > 100 ? 100 : percent);
    }

    public static String getProgressBar(final PlayerData playerData) {
        return getProgressBar(playerData, getProgress(playerData));
    }

    public static String getProgressBar(final PlayerData playerData, final double percentage) {
        int filledBars = (int) (getNextLevel(playerData).getBars() * percentage);
        int leftOver = getNextLevel(playerData).getBars() - filledBars;

        StringBuilder sb = new StringBuilder();
        sb.append(Lang.PROGRESS_START.asString());
        sb.append(Lang.PROGRESS_COMPLETE.asString());

        for (int i = 0; i < filledBars; i++) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }

        sb.append(Lang.PROGRESS_INCOMPLETE.asString());
        for (int i = 0; i < leftOver; i++) {
            sb.append(Lang.PROGRESS_CHARACTER.asString());
        }

        sb.append(Lang.PROGRESS_END.asString());
        return sb.toString();
    }

    public static ItemStack getItemStack(final Player player, final Block block) {
        final PlayerData user = PlayerData.get().get(player.getUniqueId());
        final Tool tool = getPlayerTool(user);
        final Material toolType = getType(block, tool.getType());
        final ItemStack toolItem = new ItemStack(toolType);
        final ItemMeta toolMeta = toolItem.getItemMeta();

        String username = user.getUsername();
        String blocks = String.valueOf(user.getBlocksBroken());
        String currentXp = String.valueOf(user.getXp());
        String requiredXp = String.valueOf(getNextLevel(user) != null ? getNextLevel(user).getXpRequired() : user.getXp());
        String level = String.valueOf(user.getLevel());
        double progress = getProgress(user);
        String progressStr = String.valueOf(progress);
        String progressBar = getProgressBar(user, progress);

        String toolName;
        if (toolType.name().contains("PICKAXE")) toolName = tool.getPickaxeName();
        else if (toolType.name().contains("AXE")) toolName = tool.getAxeName();
        else toolName = tool.getShovelName();
        toolName = replaceVariables(toolName, username, blocks, currentXp, requiredXp, level, progressStr, progressBar);
        toolMeta.setDisplayName(StringUtil.translate(toolName));
        List<String> toolLore;
        if (toolType.name().contains("PICKAXE")) toolLore = tool.getPickaxeLore();
        else if (toolType.name().contains("AXE")) toolLore = tool.getAxeLore();
        else toolLore = tool.getShovelLore();
        final List<String> lore = new ArrayList<>();

        tool.getCustomEnchants().forEach((enchant, enchantLvl) -> {
            lore.add(CachedConfig.getEnchantPrefix() + enchant + " " + enchantLvl);
        });

        if (toolLore != null && toolLore.size() > 0) {
            toolLore.forEach(localLore -> lore.add(StringUtil.translate(replaceVariables(localLore, username, blocks, currentXp, requiredXp, level, progressStr, progressBar))));
            toolMeta.setLore(lore);
        }

        toolMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        toolMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        tool.getItemFlags().forEach(toolMeta::addItemFlags);

        toolItem.setItemMeta(toolMeta);
        tool.getVanillaEnchants().forEach(toolItem::addUnsafeEnchantment);
        final NBTItem nbtItem = new NBTItem(toolItem);
        nbtItem.setString("omnitool", player.getUniqueId().toString());
        return nbtItem.getItem();
    }

    public int getLevel() {
        return this.level;
    }

    public double getXpRequired() {
        return this.xpRequired;
    }

    public boolean isRestricted() {
        return this.restrict;
    }

    public static int getOmnitoolSlot(final Player player) {
        int i = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && new NBTItem(item).hasKey("omnitool")) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static Material getType(final Block block, final String type) {
        String pickaxeTypeStr = type + "_PICKAXE";
        String axeTypeStr = type + "_AXE";
        String shovelTypeStr = type + "_SPADE";

        Material pickaxeType;
        Material axeType;
        Material shovelType;

        if (CachedConfig.useNewMaterials()) {
            pickaxeTypeStr = "LEGACY_" + pickaxeTypeStr;
            axeTypeStr = "LEGACY_" + axeTypeStr;
            shovelTypeStr = "LEGACY_" + type + "_SPADE";
        }

        pickaxeType = Material.valueOf(pickaxeTypeStr.toUpperCase());
        axeType = Material.valueOf(axeTypeStr.toUpperCase());
        shovelType = Material.valueOf(shovelTypeStr.toUpperCase());

        if (block != null && block.getType() != Material.AIR) {
            if (CachedConfig.axeBlocks.contains(block.getType()) && Tool.tools.getConfig().getBoolean("settings.type.axe", true)) {
                return axeType;
            } else if (CachedConfig.shovelBlocks.contains(block.getType()) && Tool.tools.getConfig().getBoolean("settings.type.shovel", true)) {
                return shovelType;
            }
        }
        return pickaxeType;
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

    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public int getBars() {
        return bars;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setXpRequired(final int xpRequired) {
        this.xpRequired = xpRequired == -1 ? 0 : xpRequired;
    }

    public void setRestriction(final boolean restrict) {
        this.restrict = restrict;
    }

    public Map<Enchantment, Integer> getVanillaEnchants() {
        return this.vanillaEnchants;
    }

    public void setVanillaEnchants(final Map<Enchantment, Integer> vanillaEnchants) {
        this.vanillaEnchants = vanillaEnchants;
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

    public void setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public void setBars(int bars) {
        this.bars = bars;
    }

    public void executeActions(final Player player, final boolean fireGlobalActions) {
        if (fireGlobalActions) {
            CachedConfig.getGlobalActions().forEach(globalAction -> {
                globalAction = globalAction.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), globalAction);
            });
        }
        this.getActions().forEach(action -> {
            action = action.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), action);
        });
    }

    public Map<String, String> getCustomEnchants() {
        return this.customEnchants;
    }

    public void setCustomEnchants(Map<String, String> customEnchants) {
        this.customEnchants = customEnchants;
    }

    public double getXpFromBlock(final Block block) {
        final Optional<BlockXP> optionalBlockXP = this.getBlockXp().stream().filter(blockXp -> blockXp.getBlock() == block.getType() && blockXp.getData() == block.getData()).findFirst();
        return optionalBlockXP.isPresent() ? optionalBlockXP.get().getXp() : 0.0;
    }

    public static class BlockXP {
        private Material block;
        private byte data;
        private double xp;

        public BlockXP(final Material block, final Integer data, final double xp) {
            this.block = block;
            this.data = data.byteValue();
            this.xp = xp;
        }

        public Material getBlock() {
            return this.block;
        }

        public byte getData() {
            return this.data;
        }

        public double getXp() {
            return this.xp;
        }
    }

}

package com.codeitforyou.tools.api;

import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.config.CachedConfig;
import com.codeitforyou.tools.nbt.NBT;
import com.codeitforyou.tools.storage.PlayerData;
import com.codeitforyou.tools.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    private static Tools Tools = JavaPlugin.getPlugin(Tools.class);
    private ItemStack item;

    public Tool(final int level, final double xpRequired) {
        this.level = level;
        this.xpRequired = xpRequired == -1 ? 0 : xpRequired;

        this.vanillaEnchants = new HashMap<>();
        this.customEnchants = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public static Tool getPlayerTool(PlayerData playerData) {
        return CachedConfig.getTools().values().stream().filter(tool -> playerData.getXp() >= tool.getXpRequired()).reduce((first, second) -> second).orElse(null);
    }

    private static String replaceVariables(final String message, final String player, final String blocks, final String currentXp, final String requiredXp, final String level, final String progress, final String progressBar) {
        return message
                .replace("{player}", player)
                .replace("{blocks}", blocks)
                .replace("{currentxp}", currentXp)
                .replace("{requiredxp}", requiredXp)
                .replace("{level}", level)
                .replace("{progress}", progress)
                .replace("{progressbar}", progressBar);
    }

    public static Tool getCurrentLevel(PlayerData playerData) {
        return playerData.getLevel() < CachedConfig.getTools().size() ? CachedConfig.getTools().get(playerData.getLevel()) : CachedConfig.getTools().get(CachedConfig.getTools().size());
    }

    public static Tool getNextLevel(PlayerData playerData) {
        return CachedConfig.getTools().size() > playerData.getLevel() ? CachedConfig.getTools().get(playerData.getLevel() + 1) : CachedConfig.getTools().get(playerData.getLevel());
    }

    public static ItemStack getItemStack(Player player, Block block) {
        PlayerData playerData = PlayerData.get().get(player.getUniqueId());

        if (playerData == null) {
            Tools.getStorageHandler().pullData(player.getName(), player.getUniqueId());
            playerData = PlayerData.get().get(player.getUniqueId());
        }
        Tool tool = getPlayerTool(playerData);

        ItemStack toolItem = tool.getItem().clone();
        ItemMeta toolMeta = toolItem.getItemMeta();
        Material toolType = getType(block != null ? block.getType() : Material.AIR, tool.getType());
        toolItem.setType(toolType);

        Tools.log(player.getName() + " wants a " + toolType + " tool.");

        String toolName;
        if (toolType.name().contains("PICKAXE")) toolName = tool.getPickaxeName();
        else if (toolType.name().contains("AXE")) toolName = tool.getAxeName();
        else toolName = tool.getShovelName();

        List<String> toolLore;
        if (toolType.name().contains("PICKAXE")) toolLore = tool.getPickaxeLore();
        else if (toolType.name().contains("AXE")) toolLore = tool.getAxeLore();
        else toolLore = tool.getShovelLore();

        String username = playerData.getUsername();
        String blocks = String.valueOf(playerData.getBlocksBroken());
        String currentXp = String.valueOf(Common.format(playerData.getXp()));
        String requiredXp = String.valueOf(getNextLevel(playerData) != null ? getNextLevel(playerData).getXpRequired() : playerData.getXp());
        String level = String.valueOf(playerData.getLevel());
        double progress = Common.getProgress(playerData);
        String progressStr = String.valueOf(Common.format(progress));
        String progressBar = Common.getProgressBar(progress);

        Tools.log(" ");
        Tools.log("Updating " + player.getName() + "..");
        Tools.log(" ");
        Tools.log("Blocks: " + blocks);
        Tools.log("Current XP: " + currentXp);
        Tools.log("Required XP: " + requiredXp);
        Tools.log("Level: " + level);
        Tools.log("Progress: " + progress + "% (" + progressStr + "%)");
        Tools.log("Progress Bar: " + progressBar);
        Tools.log(" ");

        if (toolMeta != null) {
            toolMeta.setDisplayName(Common.translate(replaceVariables(
                    toolName,
                    username,
                    blocks,
                    currentXp,
                    requiredXp,
                    level,
                    progressStr,
                    progressBar)
            ));
            List<String> updatedLore = new ArrayList<>();
            Objects.requireNonNull(toolLore).forEach(lore -> updatedLore.add(Common.translate(replaceVariables(
                    lore,
                    username,
                    blocks,
                    currentXp,
                    requiredXp,
                    level,
                    progressStr,
                    progressBar))
            ));
            toolMeta.setLore(updatedLore);
            toolItem.setItemMeta(toolMeta);
        }

        NBT item = NBT.get(toolItem);
        item.setString("omnitool", player.getUniqueId().toString());
        return item.apply(toolItem);
    }

    @SuppressWarnings("deprecation")
    public static void updateTool(final Player player, final Block block) {
        final PlayerData user = PlayerData.get().get(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                Tool tool = CachedConfig.getTools().get(user.getLevel());

                for (final Tool toolObj : CachedConfig.getTools().values()) {
                    if (toolObj.getLevel() == 1) {
                        continue;
                    }
                    if (toolObj.getLevel() <= user.getLevel()) {
                        continue;
                    }

                    if (toolObj.getXpRequired() > user.getXp()) {
                        break;
                    }
                    user.setLevel(user.getLevel() + 1);
                    tool = toolObj;
                    Bukkit.getPluginManager().callEvent(new ToolLevelUpEvent(player, player.getItemInHand(), null, user, toolObj));
                }

                if (tool.isRestricted() && user.getXp() > tool.getXpRequired()) {
                    user.setXp(tool.getXpRequired());
                }

                player.setItemInHand(getItemStack(player, block));
            }
        }.runTaskAsynchronously(Tools);
    }

    public static int getSlot(final Player player) {
        int i = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item != null && NBT.get(item).hasKey("omnitool")) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static Material getType(final Material block, final String type) {
        String pickaxeTypeStr = type + "_PICKAXE";
        String axeTypeStr = type + "_AXE";
        String shovelTypeStr = type + "_SPADE";

        Material pickaxeType;
        Material axeType;
        Material shovelType;

        pickaxeType = Material.valueOf(pickaxeTypeStr.toUpperCase());
        axeType = Material.valueOf(axeTypeStr.toUpperCase());
        shovelType = Material.valueOf(shovelTypeStr.toUpperCase());

        if (block != null && block != Material.AIR) {
            if (CachedConfig.axeBlocks.contains(block) && Tool.Tools.getConfig().getBoolean("settings.type.axe", true)) {
                return axeType;
            } else if (CachedConfig.shovelBlocks.contains(block) && Tool.Tools.getConfig().getBoolean("settings.type.shovel", true)) {
                return shovelType;
            }
        }
        return pickaxeType;
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

    public void createItem() {
        final Material toolType = getType(Material.STONE, getType());
        final ItemStack toolItem = new ItemStack(toolType);
        final ItemMeta toolMeta = toolItem.getItemMeta();

        String toolName = getPickaxeName();

        toolName = Common.translate(toolName);

        Objects.requireNonNull(toolMeta).setDisplayName(Common.translate(toolName));
        List<String> toolLore = getPickaxeLore();

        final List<String> lore = new ArrayList<>();

        getCustomEnchants().forEach((enchant, enchantLvl) -> {
            lore.add(CachedConfig.getEnchantPrefix() + enchant + " " + enchantLvl);
        });

        if (toolLore != null && toolLore.size() > 0) {
            toolLore.forEach(localLore -> lore.add(Common.translate(localLore)));
            toolMeta.setLore(lore);
        }

        toolMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        toolMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        getItemFlags().forEach(toolMeta::addItemFlags);

        toolItem.setItemMeta(toolMeta);
        getVanillaEnchants().forEach(toolItem::addUnsafeEnchantment);

        this.item = toolItem;
    }

    public ItemStack getItem() {
        return item;
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

    public void executeActions(final Player player, final boolean fireGlobalActions) {
        if (fireGlobalActions) {
            CachedConfig.getGlobalActions().forEach(globalAction -> {
                globalAction = globalAction.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
                Tools.getActionManager().runActions(player, globalAction);
            });
        }
        this.getActions().forEach(action -> {
            action = action.replace("%player%", player.getName()).replace("{player}", player.getName()).replace("%level%", String.valueOf(this.getLevel())).replace("{level}", String.valueOf(this.getLevel()));
            Tools.getActionManager().runActions(player, action);
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

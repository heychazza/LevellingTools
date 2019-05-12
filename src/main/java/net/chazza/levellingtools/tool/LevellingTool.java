package net.chazza.levellingtools.tool;

import de.tr7zw.itemnbtapi.NBTItem;
import net.chazza.levellingtools.config.ConfigCache;
import net.chazza.levellingtools.entity.UserEntity;
import net.chazza.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LevellingTool {

    public static LevellingTool getPlayerTool(UserEntity userEntity) {
        return ConfigCache.getTools().values().stream().filter(tool -> userEntity.getExperience() >= tool.getXpRequired()).reduce((first, second) -> second).orElse(null);
    }

    public static ItemStack getItemStack(Player player, Block block) {
        UserEntity user = UserEntity.getUser(player.getUniqueId());
        LevellingTool tool = getPlayerTool(user);

        Material toolType = LevellingTool.getType(block, tool.getType());
        ItemStack toolItem = new ItemStack(toolType);
        ItemMeta toolMeta = toolItem.getItemMeta();

        String toolName;

        if(toolType.name().contains("PICKAXE")) toolName = tool.getPickaxeName();
        else if(toolType.name().contains("AXE")) toolName = tool.getAxeName();
        else toolName = tool.getShovelName();

        toolName = toolName.replace("%level%", tool.getLevel() + "").replace("%broken%", user.getBlocksBroken() + "");
        toolMeta.setDisplayName(StringUtil.translate(toolName));

        List<String> toolLore;

        if(toolType.name().contains("PICKAXE")) toolLore = tool.getPickaxeLore();
        else if(toolType.name().contains("AXE")) toolLore = tool.getAxeLore();
        else toolLore = tool.getShovelLore();

        List<String> lore = new ArrayList<>();
        toolLore.forEach(localLore -> lore.add(StringUtil.translate(localLore.replace("%level%", tool.getLevel() + "").replace("%broken%", user.getBlocksBroken() + ""))));
        toolMeta.setLore(lore);

        toolItem.setItemMeta(toolMeta);
        tool.getEnchantments().forEach(toolItem::addUnsafeEnchantment);

        NBTItem nbtItem = new NBTItem(toolItem);
        nbtItem.setString("omnitool", player.getUniqueId().toString());

        return nbtItem.getItem();
    }

    private int level;
    private int xpRequired;
    private Map<Enchantment, Integer> enchantments;
    private List<BlockXP> blockXp;
    private String type;
    private String pickaxeName;
    private List<String> pickaxeLore;
    private String axeName;
    private List<String> axeLore;
    private String shovelName;
    private List<String> shovelLore;
    private List<String> commands;

    public LevellingTool(int level, int xpRequired) {
        this.level = level;
        this.xpRequired = xpRequired;
    }

    public int getLevel() {
        return level;
    }

    public int getXpRequired() {
        return xpRequired;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public List<BlockXP> getBlockXp() {
        return blockXp;
    }

    public String getType() {
        return type;
    }

    public String getPickaxeName() {
        return pickaxeName;
    }

    public String getAxeName() {
        return axeName;
    }

    public String getShovelName() {
        return shovelName;
    }

    public List<String> getPickaxeLore() {
        return pickaxeLore;
    }

    public List<String> getAxeLore() {
        return axeLore;
    }

    public List<String> getShovelLore() {
        return shovelLore;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXpRequired(int xpRequired) {
        this.xpRequired = xpRequired;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setBlockXp(List<BlockXP> blockXp) {
        this.blockXp = blockXp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPickaxeName(String pickaxeName) {
        this.pickaxeName = pickaxeName;
    }

    public void setPickaxeLore(List<String> pickaxeLore) {
        this.pickaxeLore = pickaxeLore;
    }

    public void setAxeName(String axeName) {
        this.axeName = axeName;
    }

    public void setAxeLore(List<String> axeLore) {
        this.axeLore = axeLore;
    }

    public void setShovelName(String shovelName) {
        this.shovelName = shovelName;
    }

    public void setShovelLore(List<String> shovelLore) {
        this.shovelLore = shovelLore;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void executeCommands(Player player) {
        getCommands().forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", player.getName())
                        .replace("%level%", getLevel() + "")
        ));
    }

    public Integer getXpFromBlock(Block block) {
        Optional<BlockXP> optionalBlockXP = getBlockXp().stream().filter(blockXp -> blockXp.getBlock() == block.getType() && blockXp.getData() == block.getData()).findFirst();
        return optionalBlockXP.isPresent() ? optionalBlockXP.get().getExperience() : 0;
    }

    public static int getOmnitoolSlot(Player player) {
        int i = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && new NBTItem(item).hasKey("omnitool")) return i;
            i++;
        }
        return -1;
    }

    public static Material getType(Block block, String type) {
        Material pickaxeType = Material.valueOf((type + "_PICKAXE").toUpperCase());
        Material axeType = Material.valueOf((type + "_AXE").toUpperCase());
        Material shovelType = Material.valueOf((type + "_SPADE").toUpperCase());

        if (block == null) return pickaxeType;

        if (Arrays.asList(Material.WOOD, Material.LOG, Material.LOG_2).contains(block.getType())) return axeType;
        if (Arrays.asList(Material.GRASS, Material.DIRT, Material.GRAVEL).contains(block.getType())) return shovelType;
        return pickaxeType;
    }
}

package net.chazza.levellingtools.tool;

import com.google.common.collect.Maps;
import de.tr7zw.itemnbtapi.NBTItem;
import net.chazza.levellingtools.entity.UserEntity;
import net.chazza.levellingtools.util.OmnitoolUtil;
import net.chazza.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LevellingTool {

    private static HashMap<Integer, LevellingTool> toolHashMap = Maps.newHashMap();

    public static HashMap<Integer, LevellingTool> getTools() {
        return toolHashMap;
    }

    public static LevellingTool getPlayerTool(UserEntity userEntity) {
        return toolHashMap.values().stream().filter(tool -> userEntity.getExperience() >= tool.getXpRequired()).reduce((first, second) -> second).orElse(null);
    }

    public static ItemStack getItemStack(Player player, Block block) {
        UserEntity user = UserEntity.getUser(player);
        LevellingTool tool = getPlayerTool(user);

        ItemStack toolItem = new ItemStack(OmnitoolUtil.getType(block, tool.getType()));
        ItemMeta toolMeta = toolItem.getItemMeta();

        String name = tool.getName().replace("%level%", tool.getLevel() + "").replace("%broken%", user.getBlocksBroken() + "");
        toolMeta.setDisplayName(StringUtil.translate(name));
        List<String> lore = new ArrayList<>();

        tool.getLore().forEach(localLore -> lore.add(StringUtil.translate(localLore.replace("%level%", tool.getLevel() + "").replace("%broken%", user.getBlocksBroken() + ""))));
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
    private String name;
    private List<String> lore;
    private List<String> commands;

    public LevellingTool(int level, int xpRequired, Map<Enchantment, Integer> enchantments, List<BlockXP> blockXp, String name, List<String> lore, String type, List<String> commands) {
        this.level = level;
        this.xpRequired = xpRequired;
        this.enchantments = enchantments;
        this.blockXp = blockXp;
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.commands = commands;
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

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getCommands() {
        return commands;
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
}

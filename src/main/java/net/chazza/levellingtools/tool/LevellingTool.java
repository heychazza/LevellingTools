package net.chazza.levellingtools.tool;

import com.google.common.collect.Maps;
import de.tr7zw.itemnbtapi.NBTContainer;
import de.tr7zw.itemnbtapi.NBTItem;
import net.chazza.levellingtools.entity.UserEntity;
import net.chazza.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Stream;

public class LevellingTool {

    private static HashMap<Integer, LevellingTool> toolHashMap = Maps.newHashMap();

    public static HashMap<Integer, LevellingTool> getTools() {
        return toolHashMap;
    }

    public static LevellingTool getPlayer(Player player) {
        UserEntity user = UserEntity.getUser(player);
        Optional<LevellingTool> levellingTool = toolHashMap.values().stream().filter(tool -> user.getExperience() >= tool.getXpRequired()).reduce((first, second) -> second);

        return levellingTool.orElse(null);
    }

    public static ItemStack getItemStack(Player player) {
        LevellingTool tool = getPlayer(player);

        ItemStack toolItem = new ItemStack(tool.getType());
        ItemMeta toolMeta = toolItem.getItemMeta();

        String name = tool.getName().replace("%level%", tool.getLevel() + "");
        toolMeta.setDisplayName(StringUtil.translate(name));
        List<String> lore = new ArrayList<>();

        tool.getLore().forEach(localLore -> lore.add(StringUtil.translate(localLore.replace("%level%", tool.getLevel() + ""))));
        toolMeta.setLore(lore);

        toolItem.setItemMeta(toolMeta);

        NBTItem nbtItem = new NBTItem(toolItem);
        nbtItem.setString("omnitool", player.getUniqueId().toString());

        return nbtItem.getItem();
    }

    /*
     * TODO:
     *  Tool Level
     *  Enchantments
     *  Tool Type
     */

    private int level;
    private int xpRequired;
    private Map<Enchantment, Integer> enchantments;
    private Material type;
    private String name;
    private List<String> lore;
    private List<String> commands;

    public LevellingTool(int level, int xpRequired, Map<Enchantment, Integer> enchantments, String name, List<String> lore, Material type, List<String> commands) {
        this.level = level;
        this.xpRequired = xpRequired;
        this.enchantments = enchantments;
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

    public Material getType() {
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
}

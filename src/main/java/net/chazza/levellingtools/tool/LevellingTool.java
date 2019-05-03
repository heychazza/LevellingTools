package net.chazza.levellingtools.tool;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class LevellingTool {
    /*
     * TODO:
     *  Tool Level
     *  Enchantments
     *  Tool Type
     */

    private int level;
    private Map<Enchantment, Integer> enchantments;
    private Material type;

    public LevellingTool(int level, Map<Enchantment, Integer> enchantments, Material type) {
        this.level = level;
        this.enchantments = enchantments;
        this.type = type;
    }
}

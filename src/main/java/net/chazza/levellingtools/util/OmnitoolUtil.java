package net.chazza.levellingtools.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;

public class OmnitoolUtil {

    public static Material getType(Block block, String type) {
        Material pickaxeType = Material.valueOf((type + "_PICKAXE").toUpperCase());
        Material axeType = Material.valueOf((type + "_AXE").toUpperCase());
        Material shovelType = Material.valueOf((type + "_SPADE").toUpperCase());

        if(block == null) return pickaxeType;

        if(Arrays.asList(Material.WOOD, Material.LOG, Material.LOG_2).contains(block.getType())) return axeType;
        if(Arrays.asList(Material.GRASS, Material.DIRT, Material.GRAVEL).contains(block.getType())) return shovelType;
        return pickaxeType;
    }
}

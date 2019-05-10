package net.chazza.levellingtools.util;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class OmnitoolUtil {

    public static int getOmnitoolSlot(Player player) {
        int i = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null && new NBTItem(item).hasKey("omnitool")) return i;
            i++;
        }
        return -1;
    }

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

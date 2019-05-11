package net.chazza.levellingtools.events;

import de.tr7zw.itemnbtapi.NBTItem;
import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ToolPreMineEvent implements Listener {

    public ToolPreMineEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = player.getItemInHand();
        NBTItem nbtItem = new NBTItem(item);


        if(nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            player.setItemInHand(LevellingTool.getItemStack(player, block));
        }
    }
}

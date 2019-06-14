package gg.plugins.levellingtools.events;

import de.tr7zw.itemnbtapi.NBTItem;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.ToolDamageEvent;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class PreMineEvent implements Listener {

    public PreMineEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = player.getItemInHand();
        NBTItem nbtItem = new NBTItem(item);

        if (item.getType() == Material.AIR) return;

        if (nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            ToolDamageEvent damageEvent = new ToolDamageEvent(player, item, block);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(ToolDamageEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        player.setItemInHand(LevellingTool.getItemStack(player, block));
    }
}

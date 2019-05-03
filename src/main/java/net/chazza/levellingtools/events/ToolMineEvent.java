package net.chazza.levellingtools.events;

import de.tr7zw.itemnbtapi.NBTContainer;
import de.tr7zw.itemnbtapi.NBTItem;
import net.chazza.levellingtools.LevellingTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ToolMineEvent implements Listener {

    public ToolMineEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onItemPickup(BlockDamageEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItemInHand();

        NBTContainer nbtItem = NBTItem.convertItemtoNBT(item);

        if(nbtItem.hasKey("omnitool")) {
            // Omnitool
            UUID toolOwner = UUID.fromString(nbtItem.getString("omnitool"));

            if(!player.getUniqueId().equals(toolOwner)) {
                // Update Tool
            }
        }

    }
}

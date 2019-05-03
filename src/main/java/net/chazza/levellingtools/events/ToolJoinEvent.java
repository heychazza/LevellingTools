package net.chazza.levellingtools.events;

import de.tr7zw.itemnbtapi.NBTContainer;
import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.utils.MinecraftVersion;
import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ToolJoinEvent implements Listener {

    public ToolJoinEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onItemPickup(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // TODO: Check if player already has tool
        player.setItemInHand(LevellingTool.getItemStack(player));
    }
}

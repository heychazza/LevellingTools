package gg.plugins.levellingtools.events;

import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import gg.plugins.levellingtools.api.ToolJoinEvent;
import org.bukkit.block.Block;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import gg.plugins.levellingtools.LevellingTools;
import org.bukkit.event.Listener;

public class JoinEvent implements Listener
{
    public JoinEvent(final LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final ToolJoinEvent joinEvent = new ToolJoinEvent(player, LevellingTool.getItemStack(player, null), LevellingTool.getOmnitoolSlot(player));
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final ToolJoinEvent e) {
        final Player player = e.getPlayer();
        final ItemStack tool = e.getItem();
        final int slot = e.getSlot();
        if (slot == -1) {
            player.getInventory().addItem(tool);
        }
        else {
            player.getInventory().setItem(slot, tool);
        }
    }
}

package gg.plugins.levellingtools.events;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.ToolJoinEvent;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinEvent implements Listener {

    public JoinEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ToolJoinEvent joinEvent = new ToolJoinEvent(player, LevellingTool.getItemStack(player, null), LevellingTool.getOmnitoolSlot(player));
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(ToolJoinEvent e) {
        Player player = e.getPlayer();
        ItemStack tool = e.getItem();
        int slot = e.getSlot();

        if (slot == -1) player.getInventory().addItem(tool);
        else player.getInventory().setItem(slot, tool);
    }
}

package net.chazza.levellingtools.events;

import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ToolJoinEvent implements Listener {

    public ToolJoinEvent(LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        int omnitool = LevellingTool.getOmnitoolSlot(player);

        if(omnitool == -1) {
            player.getInventory().addItem(LevellingTool.getItemStack(player, null));
            return;
        }

        player.getInventory().setItem(omnitool, LevellingTool.getItemStack(player, null));
    }
}

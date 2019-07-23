package gg.plugins.levellingtools.event;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.ToolJoinEvent;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class JoinEvent implements Listener {

    private LevellingTools plugin;

    public JoinEvent(LevellingTools plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        plugin.getStorageHandler().pullData(e.getPlayer().getUniqueId());
        final ToolJoinEvent joinEvent = new ToolJoinEvent(player, LevellingTool.getItemStack(player, null), LevellingTool.getOmnitoolSlot(player));
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final ToolJoinEvent e) {
        final Player player = e.getPlayer();
        final ItemStack tool = e.getItem();
        final int slot = e.getSlot();

        if (ConfigCache.canGiveOnJoin()) {
            if (slot == -1) {
                player.getInventory().addItem(tool);
            } else {
                player.getInventory().setItem(slot, tool);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getStorageHandler().pushData(e.getPlayer().getUniqueId());
    }
}

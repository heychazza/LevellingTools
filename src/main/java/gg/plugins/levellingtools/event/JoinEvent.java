package gg.plugins.levellingtools.event;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.Tool;
import gg.plugins.levellingtools.api.ToolJoinEvent;
import gg.plugins.levellingtools.config.CachedConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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
    public void onPlayerJoin(final AsyncPlayerPreLoginEvent e) {
        plugin.getStorageHandler().pullData(e.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final ToolJoinEvent joinEvent = new ToolJoinEvent(player, Tool.getItemStack(player, null), Tool.getSlot(player));
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final ToolJoinEvent e) {
        final Player player = e.getPlayer();
        final ItemStack tool = e.getItem();
        final int slot = e.getSlot();

        if (CachedConfig.canGiveOnJoin()) {
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

package io.felux.levellingtools.listener;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.api.Tool;
import io.felux.levellingtools.api.ToolJoinEvent;
import io.felux.levellingtools.config.CachedConfig;
import io.felux.levellingtools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {

    private LevellingTools plugin;

    public JoinListener(LevellingTools plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final AsyncPlayerPreLoginEvent e) {
        plugin.getStorageHandler().pullData(e.getName(), e.getUniqueId());
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
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getStorageHandler().pushData(e.getPlayer().getUniqueId());
                PlayerData.get().remove(e.getPlayer().getUniqueId());
            }
        }.runTaskLaterAsynchronously(plugin, 20);
    }
}

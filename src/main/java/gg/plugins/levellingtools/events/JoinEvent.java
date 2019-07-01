package gg.plugins.levellingtools.events;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.ToolJoinEvent;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.entity.PlayerEntity;
import gg.plugins.levellingtools.tool.LevellingTool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

        PlayerEntity playerEntity = ConfigCache.getDB().createQuery(PlayerEntity.class).filter("uuid", player.getUniqueId().toString()).get();

        if (playerEntity == null) {
            PlayerEntity newPlayerEntity = new PlayerEntity();
            newPlayerEntity.setUuid(player.getUniqueId().toString());
            newPlayerEntity.setUsername(player.getName());
            newPlayerEntity.setLowercaseUsername(player.getName().toLowerCase());
            newPlayerEntity.setExperience(0);
            newPlayerEntity.setBlocksBroken(0);
            newPlayerEntity.setLevel(1);
            playerEntity = newPlayerEntity;
        }

        plugin.getPlayerEntities().put(player.getUniqueId(), playerEntity);

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
        } else {
            player.getInventory().setItem(slot, tool);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if(plugin.getPlayerEntities().containsKey(player.getUniqueId())) {
            PlayerEntity playerEntity = plugin.getPlayerEntities().get(player.getUniqueId());
            ConfigCache.getDB().save(playerEntity);
            plugin.getPlayerEntities().remove(player.getUniqueId());

        }
    }
}

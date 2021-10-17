package com.codeitforyou.tools.listener;

import com.codeitforyou.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlaceListener implements Listener {
    private Tools plugin;

    public PlaceListener(Tools plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        e.getBlock().setMetadata("placed", new FixedMetadataValue(plugin, true));
    }
}

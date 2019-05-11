package net.chazza.levellingtools.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WorldGuardHook {
    public static WorldGuardPlugin getWorldGuard() {
        Plugin wgplugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(wgplugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wgplugin;
    }

    public static boolean checkIfPlayerInRegion(final Player player, final String region) {
        com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(player.getLocation().getX(), player.getLocation().getBlockY(), player.getLocation().getZ());
        return Objects.requireNonNull(getWorldGuard()).getRegionManager(player.getLocation().getWorld()).getApplicableRegionsIDs(v).contains(region);
    }


}

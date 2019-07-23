package gg.plugins.levellingtools.hook;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class WorldGuardHook {
    public static WorldGuardPlugin getWorldGuard() {
        final Plugin wgplugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(wgplugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wgplugin;
    }

    public static boolean checkIfPlayerInRegion(final Player player, final String region) {
        final Vector v = new Vector(player.getLocation().getX(), (double) player.getLocation().getBlockY(), player.getLocation().getZ());
        return Objects.requireNonNull(getWorldGuard()).getRegionManager(player.getLocation().getWorld()).getApplicableRegionsIDs(v).contains(region);
    }
}

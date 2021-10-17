package com.codeitforyou.tools.hook;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;

public class WorldGuardHook {

    private static boolean enabled;
    private static WorldGuardWrapper worldGuardWrapper;

    public WorldGuardHook() {
        enabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

        if (enabled) {
            worldGuardWrapper = WorldGuardWrapper.getInstance();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean checkIfPlayerInRegion(final Player player, final Block block, final String region) {
        return worldGuardWrapper.getRegions(block.getLocation()).stream().anyMatch(iWrappedRegion -> iWrappedRegion.getId().equalsIgnoreCase(region));
    }

}

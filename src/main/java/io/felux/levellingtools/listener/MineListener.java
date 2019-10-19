package io.felux.levellingtools.listener;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.api.*;
import io.felux.levellingtools.config.CachedConfig;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.hook.WorldGuardHook;
import io.felux.levellingtools.nbt.NBT;
import io.felux.levellingtools.storage.PlayerData;
import io.felux.levellingtools.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class MineListener implements Listener {
    private LevellingTools plugin;

    public MineListener(final LevellingTools levellingTools) {
        this.plugin = levellingTools;
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(final BlockDamageEvent e) {
        if (e.isCancelled()) {
            plugin.log("Failed to change omnitool as a plugin disabled the BlockDamageEvent.");
            return;
        }

        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR) return;
        final NBT nbtItem = NBT.get(item);
        if (item.getType() == Material.AIR) return;

        if (nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            final ToolDamageEvent damageEvent = new ToolDamageEvent(player, item, block);
            Bukkit.getServer().getPluginManager().callEvent(damageEvent);
        }
    }

    @EventHandler
    public void onBlockDamage(final ToolDamageEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();

        if (plugin.getStorageHandler().getPlayer(player.getUniqueId()) == null)
            plugin.getStorageHandler().pullData(player.getName(), player.getUniqueId());
        player.setItemInHand(Tool.getItemStack(player, block));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        if (e.isCancelled()) {
            plugin.log("Failed to break block with omnitool as a plugin disabled the BlockBreakEvent.");
            return;
        }

        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR) return;
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        final NBT nbtItem = NBT.get(item);

        if (nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            final PlayerData user = PlayerData.get().get(player.getUniqueId());
            final Tool playerTool = CachedConfig.getTools().get(user.getLevel());
            final double xpGained = playerTool.getXpFromBlock(block);
            final Booster booster = CachedConfig.getMultiplier(player);
            final ToolMineEvent mineEvent = new ToolMineEvent(player, item, xpGained, booster, block, user, playerTool);
            Bukkit.getServer().getPluginManager().callEvent(mineEvent);
        }
    }

    @EventHandler
    public void onMine(final ToolMineEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final PlayerData user = e.getPlayerData();
        Tool tool = e.getTool();
        double xpGained = tool.getXpFromBlock(block);

        plugin.log(block.getType().name() + " gives " + xpGained + " xp to " + player.getName());

        if (e.getBooster() != null) {
            xpGained = xpGained * e.getBooster().getMultiplier();
        }

        boolean canLevelUp = true;

        if (WorldGuardHook.isEnabled()) {
            for (final String region : CachedConfig.getBlacklistedRegions()) {
                if (WorldGuardHook.checkIfPlayerInRegion(player, block, region)) {
                    plugin.log(player.getName() + " wasn't able to gain xp due to '" + region + "' being a blacklisted region.");
                    canLevelUp = false;
                    break;
                }
            }
        }

        if (CachedConfig.getBlacklistedWorlds().contains(block.getWorld().getName())) {
            plugin.log(player.getName() + " wasn't able to gain xp due to being in a blacklisted world.");
            canLevelUp = false;
        }

        if (canLevelUp) {
            final double totalXp = user.getXp() + xpGained;
            if (xpGained > 0 && !Lang.EXP_GAINED.asString().isEmpty()) {
                Common.sendActionbar(player, Lang.EXP_GAINED.asString(Common.format(xpGained)));
            }
            user.setXp(totalXp);
            user.setBlocksBroken(user.getBlocksBroken() + 1);
            user.setLevel(tool.getLevel());
            for (final Tool toolObj : CachedConfig.getTools().values()) {
                if (toolObj.getLevel() == 1) {
                    continue;
                }
                if (toolObj.getLevel() <= user.getLevel()) {
                    continue;
                }
                if (totalXp < toolObj.getXpRequired()) {
                    continue;
                }
                user.setLevel(user.getLevel() + 1);
                tool = toolObj;
                Bukkit.getPluginManager().callEvent(new ToolLevelUpEvent(player, player.getItemInHand(), block, user, toolObj));
                break;
            }
            if (tool.isRestricted() && user.getXp() > tool.getXpRequired()) {
                user.setXp(tool.getXpRequired());
            }
            player.setItemInHand(Tool.getItemStack(player, e.getBlock()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final ToolLevelUpEvent e) {
        final Player player = e.getPlayer();
        final Tool tool = e.getTool();
        tool.executeActions(player, true);
    }
}
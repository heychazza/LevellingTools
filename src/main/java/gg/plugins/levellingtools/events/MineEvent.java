package gg.plugins.levellingtools.events;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import de.tr7zw.itemnbtapi.NBTItem;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.Multiplier;
import gg.plugins.levellingtools.api.ToolLevelUpEvent;
import gg.plugins.levellingtools.api.ToolMineEvent;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.entity.PlayerEntity;
import gg.plugins.levellingtools.hook.WorldGuardHook;
import gg.plugins.levellingtools.tool.LevellingTool;
import gg.plugins.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MineEvent implements Listener {
    public MineEvent(final LevellingTools levellingTools) {
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final ItemStack item = player.getItemInHand();
        final NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            if (WorldGuardHook.getWorldGuard() != null) {
                if (!WorldGuardHook.getWorldGuard().getRegionManager(block.getWorld()).getApplicableRegions(block.getLocation()).allows(DefaultFlag.BLOCK_BREAK)) {
                    if (ConfigCache.cancelBlacklistedRegion()) {
                        e.setCancelled(true);
                    }
                    return;
                }
                for (final String region : ConfigCache.getBlacklistedRegions()) {
                    if (WorldGuardHook.checkIfPlayerInRegion(player, region)) {
                        if (ConfigCache.cancelBlacklistedRegion()) {
                            e.setCancelled(true);
                        }
                        return;
                    }
                }
            }
            if (ConfigCache.getBlacklistedWorlds().contains(block.getWorld().getName())) {
                if (ConfigCache.cancelBlacklistedWorld()) {
                    e.setCancelled(true);
                }
                return;
            }
            final PlayerEntity user = PlayerEntity.getUser(player.getUniqueId());
            final LevellingTool playerTool = ConfigCache.getTools().get(user.getLevel());
            final double xpGained = playerTool.getXpFromBlock(block);
            final Multiplier multiplier = ConfigCache.getMultiplier(player);
            final ToolMineEvent mineEvent = new ToolMineEvent(player, item, xpGained, multiplier, block, user, playerTool);
            Bukkit.getServer().getPluginManager().callEvent(mineEvent);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMine(final ToolMineEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final PlayerEntity user = e.getPlayerData();
        LevellingTool tool = e.getTool();
        double xpGained = tool.getXpFromBlock(block);

        if (e.getMultiplier() != null) {
            xpGained = xpGained * e.getMultiplier().getMultiplier();
        }

        final double totalXp = user.getExperience() + xpGained;
        if (xpGained > 0) {
            StringUtil.sendActionbar(player, Lang.EXP_GAINED.asString(xpGained));
        }
        user.setExperience(totalXp);
        user.setBlocksBroken(user.getBlocksBroken() + 1);
        user.setLevel(tool.getLevel());
        for (final LevellingTool toolObj : ConfigCache.getTools().values()) {
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
        if (tool.isRestricted() && user.getExperience() > tool.getXpRequired()) {
            user.setExperience(tool.getXpRequired());
        }
        player.setItemInHand(LevellingTool.getItemStack(player, e.getBlock()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final ToolLevelUpEvent e) {
        final Player player = e.getPlayer();
        final LevellingTool tool = e.getTool();
        tool.executeActions(player, true);
    }
}

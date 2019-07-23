package gg.plugins.levellingtools.event;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import de.tr7zw.itemnbtapi.NBTItem;
import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.api.Booster;
import gg.plugins.levellingtools.api.ToolLevelUpEvent;
import gg.plugins.levellingtools.api.ToolMineEvent;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.hook.WorldGuardHook;
import gg.plugins.levellingtools.storage.PlayerData;
import gg.plugins.levellingtools.tool.LevellingTool;
import gg.plugins.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MineEvent implements Listener {
    private LevellingTools plugin;

    public MineEvent(final LevellingTools levellingTools) {
        this.plugin = levellingTools;
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR) return;

        final NBTItem nbtItem = new NBTItem(item);

        if (nbtItem.hasNBTData() && nbtItem.hasKey("omnitool")) {
            final PlayerData user = PlayerData.get().get(player.getUniqueId());
            final LevellingTool playerTool = ConfigCache.getTools().get(user.getLevel());
            final double xpGained = playerTool.getXpFromBlock(block);
            final Booster booster = ConfigCache.getMultiplier(player);
            final ToolMineEvent mineEvent = new ToolMineEvent(player, item, xpGained, booster, block, user, playerTool);
            Bukkit.getServer().getPluginManager().callEvent(mineEvent);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMine(final ToolMineEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();
        final PlayerData user = e.getPlayerData();
        LevellingTool tool = e.getTool();
        double xpGained = tool.getXpFromBlock(block);

        if (e.getBooster() != null) {
            xpGained = xpGained * e.getBooster().getMultiplier();
        }

        boolean canLevelUp = true;

        if (WorldGuardHook.getWorldGuard() != null) {
            if (!WorldGuardHook.getWorldGuard().getRegionManager(block.getWorld()).getApplicableRegions(block.getLocation()).allows(DefaultFlag.BLOCK_BREAK)) {
                e.setCancelled(true);
                plugin.log(player.getName() + " wasn't able to break '" + block.getType().name() + "', you may need to add the 'BLOCK_BREAK' flag.");
                return;
            }

            for (final String region : ConfigCache.getBlacklistedRegions()) {
                if (WorldGuardHook.checkIfPlayerInRegion(player, region)) {
                    canLevelUp = false;
                    break;
                }
            }
        }

        if (ConfigCache.getBlacklistedWorlds().contains(block.getWorld().getName())) {
            canLevelUp = false;
        }

        if (canLevelUp) {
            final double totalXp = user.getXp() + xpGained;
            if (xpGained > 0) {
                StringUtil.sendActionbar(player, Lang.EXP_GAINED.asString(xpGained));
            }
            user.setXp(totalXp);
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
            if (tool.isRestricted() && user.getXp() > tool.getXpRequired()) {
                user.setXp(tool.getXpRequired());
            }
            player.setItemInHand(LevellingTool.getItemStack(player, e.getBlock()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final ToolLevelUpEvent e) {
        final Player player = e.getPlayer();
        final LevellingTool tool = e.getTool();
        tool.executeActions(player, true);
    }
}

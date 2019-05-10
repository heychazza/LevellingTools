package net.chazza.levellingtools.events;

import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.utils.MinecraftVersion;
import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.MongoDB;
import net.chazza.levellingtools.config.Lang;
import net.chazza.levellingtools.entity.UserEntity;
import net.chazza.levellingtools.tool.LevellingTool;
import net.chazza.levellingtools.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ToolMineEvent implements Listener {

    LevellingTools levellingTools;
    public ToolMineEvent(LevellingTools levellingTools) {
        this.levellingTools = levellingTools;
        Bukkit.getPluginManager().registerEvents(this, levellingTools);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = player.getItemInHand();

        MinecraftVersion.setLogging(false);
        NBTItem nbtItem = new NBTItem(item);

        /*
        TODO:
        - Create Multiplier System (Perm based, with Redeemable Additions)
         */

        if(nbtItem.hasKey("omnitool")) {
            UserEntity user = UserEntity.getUser(player.getUniqueId());
            LevellingTool playerTool = LevellingTool.getTools().get(user.getLevel());

            int xpGained = playerTool.getXpFromBlock(block);

            if(xpGained > 0) StringUtil.sendActionbar(player, Lang.EXP_GAINED.asString(xpGained));

            int currentLvl = user.getLevel();

            for(LevellingTool tool : LevellingTool.getTools().values()) {
                if(tool.getLevel() == 1) continue;
                if(tool.getLevel() <= user.getLevel()) continue;
                if(user.getExperience() + xpGained < tool.getXpRequired()) continue;

                currentLvl++;
                tool.executeCommands(player);
            }

            user.setLevel(currentLvl);
            user.setExperience(user.getExperience() + xpGained);
            user.setBlocksBroken(user.getBlocksBroken()+1);
            MongoDB.getDatabase().save(user);
            player.setItemInHand(LevellingTool.getItemStack(player, block));
        }
    }
}

package com.codeitall.tools.command;

import com.codeitall.tools.Tools;
import com.codeitall.tools.api.Tool;
import com.codeitall.tools.command.util.Command;
import com.codeitall.tools.config.CachedConfig;
import com.codeitall.tools.config.Lang;
import com.codeitall.tools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveLevelCommand {
    @Command(aliases = {"givelevel"}, about = "Give yourself or another player level(s).", permission = "ltools.givelevel", usage = "givelevel [all|player] [levels]", requiredArgs = 2)
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        final int totalLevels = CachedConfig.getTools().size();
        final int levelArg = Integer.parseInt(args[1]);

        if (args[0].equalsIgnoreCase("ALL")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = PlayerData.get(onlinePlayer.getUniqueId());

                if ((playerData.getLevel() + levelArg) > totalLevels) {
                    playerData.setLevel(totalLevels);
                    playerData.setXp(CachedConfig.getTools().get(totalLevels).getXpRequired());
                } else {
                    final int newLevel = playerData.getLevel() + levelArg;
                    playerData.setLevel(newLevel);
                    playerData.setXp(CachedConfig.getTools().get(newLevel).getXpRequired());
                }
            }
            Lang.GIVE_LEVEL_COMMAND_ALL.send(sender, Lang.PREFIX.asString(), Bukkit.getOnlinePlayers().size(), levelArg);
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        PlayerData playerData = PlayerData.get(target.getUniqueId());

        if ((playerData.getLevel() + levelArg) > totalLevels) {
            playerData.setLevel(totalLevels);
            playerData.setXp(CachedConfig.getTools().get(totalLevels).getXpRequired());
        } else {
            final int newLevel = playerData.getLevel() + levelArg;
            playerData.setLevel(newLevel);
            playerData.setXp(CachedConfig.getTools().get(newLevel).getXpRequired());
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.getUniqueId() == target.getUniqueId()) {
                Lang.GIVE_LEVEL_COMMAND_SELF.send(player, Lang.PREFIX.asString(), levelArg);
                Tool.updateTool(player, null);
                return;
            }
        }

        Lang.GIVE_LEVEL_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), levelArg);
        Tool.updateTool(target, null);
    }
}

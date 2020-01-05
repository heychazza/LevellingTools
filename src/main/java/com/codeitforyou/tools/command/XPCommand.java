package com.codeitforyou.tools.command;

import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.command.util.Command;
import com.codeitforyou.tools.config.Lang;
import com.codeitforyou.tools.storage.PlayerData;
import com.codeitforyou.tools.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPCommand {
    @Command(aliases = {"xp"}, about = "View yourself or another players xp.", permission = "ltools.xp", usage = "xp [player]")
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        if (args.length > 0) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final PlayerData playerEntity = PlayerData.get().get(target.getUniqueId());
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (player.getUniqueId() == target.getUniqueId()) {
                    Lang.XP_COMMAND_SELF.send(player, Lang.PREFIX.asString(), Common.format(playerEntity.getXp()));
                    return;
                }
            }
            Lang.XP_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), playerEntity.getUsername(), Common.format(playerEntity.getXp()));
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player2 = (Player) sender;
            final PlayerData playerEntity = PlayerData.get().get(player2.getUniqueId());
            Lang.XP_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), Common.format(playerEntity.getXp()));
        }
    }
}

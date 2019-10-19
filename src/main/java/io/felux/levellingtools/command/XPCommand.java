package io.felux.levellingtools.command;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.command.util.Command;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.storage.PlayerData;
import io.felux.levellingtools.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPCommand {
    @Command(aliases = {"xp"}, about = "View yourself or another players xp.", permission = "ltools.xp", usage = "xp [player]")
    public static void execute(final CommandSender sender, final LevellingTools plugin, final String[] args) {
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

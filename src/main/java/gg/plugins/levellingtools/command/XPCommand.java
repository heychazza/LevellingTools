package gg.plugins.levellingtools.command;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.command.util.Command;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.entity.PlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPCommand {
    @Command(aliases = {"xp"}, about = "View yourself or another players xp.", permission = "levellingtools.xp", usage = "xp [player]")
    public static void execute(CommandSender sender, LevellingTools plugin, String[] args) {
        if (args.length > 0) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (target == null) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }

            PlayerEntity playerEntity = PlayerEntity.getUser(target.getUniqueId());

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getUniqueId() == target.getUniqueId()) {
                    Lang.XP_COMMAND_SELF.send(player, Lang.PREFIX.asString(), playerEntity.getExperience());
                    return;
                }
            }

            Lang.XP_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), playerEntity.getUsername(), playerEntity.getExperience());
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }

            Player player = (Player) sender;
            PlayerEntity playerEntity = PlayerEntity.getUser(player.getUniqueId());
            Lang.XP_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), playerEntity.getExperience());
        }
    }
}

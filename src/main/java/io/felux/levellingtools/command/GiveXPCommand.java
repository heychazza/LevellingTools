package io.felux.levellingtools.command;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.command.util.Command;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveXPCommand {
    @Command(aliases = {"givexp"}, about = "Give yourself or another player xp.", permission = "ltools.givexp", usage = "givexp [all|player] [xp]", requiredArgs = 2)
    public static void execute(final CommandSender sender, final LevellingTools plugin, final String[] args) {

        if (args[0].equalsIgnoreCase("ALL")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = PlayerData.get(onlinePlayer.getUniqueId());
                playerData.setXp(playerData.getXp() + Double.parseDouble(args[1]));
            }
            Lang.GIVE_XP_COMMAND_ALL.send(sender, Lang.PREFIX.asString(), Bukkit.getOnlinePlayers().size(), Double.parseDouble(args[1]));
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
            return;
        }

        PlayerData playerData = PlayerData.get(target.getUniqueId());
        playerData.setXp(playerData.getXp() + Double.parseDouble(args[1]));

        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;

            if (senderPlayer.getUniqueId() == target.getUniqueId()) {
                Lang.GIVE_XP_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), args[1]);
                return;
            }
        }

        Lang.GIVE_XP_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), args[1]);
    }
}

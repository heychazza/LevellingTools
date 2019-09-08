package net.chazza.levellingtools.command;

import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.command.util.Command;
import net.chazza.levellingtools.config.Lang;
import net.chazza.levellingtools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand {
    @Command(aliases = {"reset"}, about = "Reset yourself or another players data.", permission = "ltools.reset", usage = "reset [player]")
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
                    playerEntity.setBlocksBroken(0);
                    playerEntity.setXp(0);
                    playerEntity.setLevel(1);
                    Lang.RESET_COMMAND_SELF.send(player, Lang.PREFIX.asString());
                    return;
                }
            }

            playerEntity.setBlocksBroken(0);
            playerEntity.setXp(0);
            playerEntity.setLevel(1);
            Lang.RESET_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), playerEntity.getUsername());
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player2 = (Player) sender;
            final PlayerData playerEntity = PlayerData.get().get(player2.getUniqueId());
            playerEntity.setBlocksBroken(0);
            playerEntity.setXp(0);
            playerEntity.setLevel(1);
            Lang.RESET_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), playerEntity.getXp());
        }
    }
}

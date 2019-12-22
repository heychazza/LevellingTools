package io.felux.levellingtools.command;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.api.Tool;
import io.felux.levellingtools.command.util.Command;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand {
    @SuppressWarnings("deprecation")
    @Command(aliases = {"reset"}, about = "Reset yourself or another players data.", permission = "ltools.reset", usage = "reset [player]")
    public static void execute(final CommandSender sender, final LevellingTools plugin, final String[] args) {
        if (args.length > 0) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore()) {
                Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
                return;
            }

            final PlayerData playerEntity = PlayerData.get().get(target.getUniqueId());
            if ((sender instanceof Player && ((Player) sender).getUniqueId() == target.getUniqueId())) {
                playerEntity.setBlocksBroken(0);
                playerEntity.setXp(0);
                playerEntity.setLevel(1);
                Lang.RESET_COMMAND_SELF.send(sender, Lang.PREFIX.asString());
                return;
            }

            playerEntity.setBlocksBroken(0);
            playerEntity.setXp(0);
            playerEntity.setLevel(1);
            Lang.RESET_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), playerEntity.getUsername());
            if (Bukkit.getPlayer(target.getUniqueId()) != null) Tool.updateTool(((Player) target), null);
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player = (Player) sender;
            final PlayerData playerEntity = PlayerData.get().get(player.getUniqueId());
            playerEntity.setBlocksBroken(0);
            playerEntity.setXp(0);
            playerEntity.setLevel(1);
            Lang.RESET_COMMAND_SELF.send(sender, Lang.PREFIX.asString(), playerEntity.getXp());
            Tool.updateTool(player, null);
        }
    }
}

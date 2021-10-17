package com.codeitforyou.tools.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.api.Tool;
import com.codeitforyou.tools.config.Lang;
import com.codeitforyou.tools.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveXPCommand {
    @Command(aliases = {"givexp"}, about = "Give yourself or another player xp.", permission = "ltools.givexp", usage = "givexp [all|player] [xp]", requiredArgs = 2)
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {

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
            Player player = (Player) sender;

            if (player.getUniqueId() == target.getUniqueId()) {
                Lang.GIVE_XP_COMMAND_SELF.send(player, Lang.PREFIX.asString(), args[1]);
                Tool.updateTool(player, null);
                return;
            }
        }

        Lang.GIVE_XP_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), args[1]);
        Tool.updateTool(target, null);
    }
}

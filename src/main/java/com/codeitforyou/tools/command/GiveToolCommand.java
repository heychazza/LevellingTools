package com.codeitforyou.tools.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.api.Tool;
import com.codeitforyou.tools.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveToolCommand {
    @Command(aliases = {"givetool"}, about = "Give yourself or another player a tool.", permission = "ltools.givetool", usage = "givetool [player]")
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        if (args.length > 0) {
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Lang.COMMAND_UNKNOWN.send(sender, Lang.PREFIX.asString());
                return;
            }

            target.getInventory().addItem(Tool.getItemStack(target, null));

            Lang.GIVE_COMMAND_OTHER.send(sender, Lang.PREFIX.asString(), target.getName());
        } else {
            if (!(sender instanceof Player)) {
                Lang.COMMAND_PLAYER_ONLY.send(sender, Lang.PREFIX.asString());
                return;
            }
            final Player player = (Player) sender;
            player.getInventory().addItem(Tool.getItemStack(player, null));
            Lang.GIVE_COMMAND_SELF.send(sender, Lang.PREFIX.asString());
        }
    }
}

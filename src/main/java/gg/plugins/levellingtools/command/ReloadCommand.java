package gg.plugins.levellingtools.command;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.command.util.Command;
import gg.plugins.levellingtools.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "levellingtools.reload", usage = "reload")
    public static void execute(CommandSender sender, LevellingTools plugin, String[] args) {
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString());
    }
}

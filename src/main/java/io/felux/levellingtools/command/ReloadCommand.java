package io.felux.levellingtools.command;

import io.felux.levellingtools.LevellingTools;
import io.felux.levellingtools.command.util.Command;
import io.felux.levellingtools.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final LevellingTools plugin, final String[] args) {
        plugin.reloadConfig();
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString());
    }
}

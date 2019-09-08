package net.chazza.levellingtools.command;

import net.chazza.levellingtools.LevellingTools;
import net.chazza.levellingtools.command.util.Command;
import net.chazza.levellingtools.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final LevellingTools plugin, final String[] args) {
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString());
    }
}

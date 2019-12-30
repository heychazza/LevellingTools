package com.codeitall.tools.command;

import com.codeitall.tools.Tools;
import com.codeitall.tools.command.util.Command;
import com.codeitall.tools.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        plugin.reloadConfig();
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString());
    }
}

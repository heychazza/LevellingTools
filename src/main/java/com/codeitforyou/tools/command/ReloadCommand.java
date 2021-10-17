package com.codeitforyou.tools.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the plugin.", permission = "ltools.reload", usage = "reload")
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        plugin.reloadConfig();
        plugin.handleReload();
        Lang.RELOAD_COMMAND.send(sender, Lang.PREFIX.asString());
    }
}

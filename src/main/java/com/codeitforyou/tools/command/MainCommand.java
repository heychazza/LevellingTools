package com.codeitforyou.tools.command;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.tools.Tools;
import com.codeitforyou.tools.config.Lang;
import org.bukkit.command.CommandSender;

public class MainCommand {
    @Command(about = "Main command.")
    public static void execute(final CommandSender sender, final Tools plugin, final String[] args) {
        Lang.MAIN_COMMAND.send(sender, Lang.PREFIX.asString(), plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors().get(0));
    }
}

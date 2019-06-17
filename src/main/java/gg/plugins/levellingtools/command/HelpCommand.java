package gg.plugins.levellingtools.command;

import gg.plugins.levellingtools.LevellingTools;
import gg.plugins.levellingtools.command.util.Command;
import gg.plugins.levellingtools.config.Lang;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    @Command(aliases = {"help"}, about = "View this menu.", permission = "levellingtools.help", usage = "help")
    public static void execute(CommandSender sender, LevellingTools plugin, String[] args) {
        List<Method> commandMethods = new ArrayList<>();
        for (Method method : plugin.getCommandManager().getCommands().values())
            if (!commandMethods.contains(method)) commandMethods.add(method);

        Lang.HELP_COMMAND_HEADER.send(sender, Lang.PREFIX.asString(), commandMethods.size());

        for (Method commandMethod : commandMethods) {
            Command commandAnnotation = commandMethod.getAnnotation(Command.class);

            // make sure sender has permission to run the commands before showing them permissions for it
            if (!sender.hasPermission(commandAnnotation.permission())) continue;

            Lang.HELP_COMMAND_FORMAT.send(sender, String.join(",", commandAnnotation.aliases()), commandAnnotation.usage(), commandAnnotation.about());
        }

        Lang.HELP_COMMAND_FOOTER.send(sender, Lang.PREFIX.asString(), commandMethods.size());
    }
}

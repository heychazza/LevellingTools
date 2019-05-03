package net.chazza.levellingtools.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("levellingtools|tools|lvltools")
public class XPCommand extends BaseCommand {

    public XPCommand(BukkitCommandManager bukkitCommandManager) {
        bukkitCommandManager.registerCommand(this, true);
    }

    @Subcommand("xp")
    public void onCommand(Player sender, Player target) {

    }
}

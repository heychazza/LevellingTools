package gg.plugins.levellingtools.config;

import gg.plugins.levellingtools.LevellingTools;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Lang {
    EXP_GAINED("&6&l+{0} EXP"),
    PREFIX("&8[&6Tools&8]"),
    MAIN_COMMAND("{0} &7Running &f{1} &7version &6{2} &7by &e{3}&7."),
    HELP_COMMAND_HEADER("", "{0} &7Listing Commands:", "&7"),
    HELP_COMMAND_FORMAT(" &e/ltools {1} &8- &7{2}"),
    HELP_COMMAND_FOOTER("", "{0} &7Total of &f{1} &7commands."),
    RELOAD_COMMAND("{0} &7Successfully reloaded the configuration file."),
    XP_COMMAND_SELF("{0} &7You have a total of {1} xp."),
    XP_COMMAND_OTHER("{0} &7{1} has a total of {2} xp."),
    GIVE_COMMAND_SELF("{0} &7You've given yourself a levelling tool&7."),
    GIVE_COMMAND_OTHER("{0} &7You've given &e{1} &7a levelling tool."),
    PROGRESS_START("&8["),
    PROGRESS_CHARACTER(":"),
    PROGRESS_END("&8]"),
    PROGRESS_INCOMPLETE("&7"),
    PROGRESS_COMPLETE("&a"),
    COMMAND_NO_PERMISSION("{0} &cYou don't have permissions to do that."),
    COMMAND_PLAYER_ONLY("{0} &cThe command or args specified can only be used by a player."),
    COMMAND_INVALID("{0} &cThat command doesn't exist, use &f/lt help&c."),
    COMMAND_UNKNOWN("{0} &cThat player couldn't be found."),

    RESET_COMMAND_SELF("{0} &7You have reset your data."),
    RESET_COMMAND_OTHER("{0} &7You have reset &e{1}'s &7data."),

    ;

    private String message;
    private static FileConfiguration c;

    Lang(final String... def) {
        this.message = String.join("\n", def);
    }

    private String getMessage() {
        return this.message;
    }

    public static String format(String s, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            s = s.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean init(LevellingTools levellingTools) {
        Lang.c = levellingTools.getConfig();
        for (final Lang value : values()) {
            if (value.getMessage().split("\n").length == 1) {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage());
            } else {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage().split("\n"));
            }
        }
        Lang.c.options().copyDefaults(true);
        levellingTools.saveConfig();
        return true;
    }

    public String getPath() {
        return "message." + this.name().toLowerCase().toLowerCase();
    }

    public void send(final Player player, final Object... args) {
        final String message = this.asString(args);
        Arrays.stream(message.split("\n")).forEach(player::sendMessage);
    }

    public void sendRaw(final Player player, final Object... args) {
        final String message = this.asString(args);
        Arrays.stream(message.split("\n")).forEach(player::sendRawMessage);
    }

    public void send(final CommandSender sender, final Object... args) {
        if (sender instanceof Player) {
            this.send((Player) sender, args);
        } else {
            Arrays.stream(this.asString(args).split("\n")).forEach(sender::sendMessage);
        }
    }

    public String asString(final Object... objects) {
        Optional<String> opt = Optional.empty();
        if (Lang.c.contains(this.getPath())) {
            if (Lang.c.isList(getPath())) {
                opt = Optional.of(String.join("\n", Lang.c.getStringList(this.getPath())));
            } else if (Lang.c.isString(this.getPath())) {
                opt = Optional.ofNullable(Lang.c.getString(this.getPath()));
            }
        }
        return this.format(opt.orElse(this.message), objects);
    }
}

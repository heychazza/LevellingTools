package gg.plugins.levellingtools.config;

import org.bukkit.command.CommandSender;
import java.util.function.Consumer;
import java.util.Arrays;
import org.bukkit.entity.Player;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang
{
    EXP_GAINED(new String[] { "&6&l+{0} EXP" }), 
    PREFIX(new String[] { "&8[&6Tools&8]" }), 
    MAIN_COMMAND(new String[] { "{0} &7Running &f{1} &7version &6{2} &7by &e{3}&7." }), 
    HELP_COMMAND_HEADER(new String[] { "", "{0} &7Listing Commands:", "&7" }), 
    HELP_COMMAND_FORMAT(new String[] { " &e/ltools {1} &8- &7{2}" }), 
    HELP_COMMAND_FOOTER(new String[] { "", "{0} &7Total of &f{1} &7commands." }), 
    RELOAD_COMMAND(new String[] { "{0} &7Successfully reloaded the configuration file." }), 
    XP_COMMAND_SELF(new String[] { "{0} &7You have a total of {1} xp." }), 
    XP_COMMAND_OTHER(new String[] { "{0} &7{1} has a total of {2} xp." }), 
    PROGRESS_START(new String[] { "&8[" }), 
    PROGRESS_CHARACTER(new String[] { ":" }), 
    PROGRESS_END(new String[] { "&8]" }), 
    PROGRESS_INCOMPLETE(new String[] { "&7" }), 
    PROGRESS_COMPLETE(new String[] { "&a" }), 
    COMMAND_NO_PERMISSION(new String[] { "{0} &cYou don't have permissions to do that." }), 
    COMMAND_PLAYER_ONLY(new String[] { "{0} &cThe command or args specified can only be used by a player." }), 
    COMMAND_INVALID(new String[] { "{0} &cThat command doesn't exist, use &f/lt help&c." }), 
    COMMAND_UNKNOWN(new String[] { "{0} &cThat player couldn't be found." });
    
    private String message;
    private static Config config;
    private static FileConfiguration c;
    
    Lang(final String[] def) {
        this.message = String.join("\n", (CharSequence[])def);
    }
    
    private String getMessage() {
        return this.message;
    }
    
    public String getPath() {
        return this.name();
    }
    
    private String format(String s, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            s = s.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    
    public String asString(final Object... objects) {
        Optional<String> opt = Optional.empty();
        if (Lang.c.contains(this.name())) {
            if (Lang.c.isList(this.name())) {
                opt = Optional.ofNullable(Lang.c.getStringList(this.name()).stream().collect(Collectors.joining("\n")));
            }
            else if (Lang.c.isString(this.name())) {
                opt = Optional.ofNullable(Lang.c.getString(this.name()));
            }
        }
        return this.format(opt.orElse(this.message), objects);
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
            this.send((Player)sender, args);
        }
        else {
            Arrays.stream(this.asString(args).split("\n")).forEach(sender::sendMessage);
        }
    }
    
    public static Config getConfig() {
        return Lang.config;
    }
    
    public static boolean init(final Config wrapper) {
        wrapper.loadConfig();
        if (wrapper.getConfig() == null) {
            return false;
        }
        Lang.config = wrapper;
        Lang.c = wrapper.getConfig();
        for (final Lang value : values()) {
            if (value.getMessage().split("\n").length == 1) {
                Lang.c.addDefault(value.getPath(), value.getMessage());
            }
            else {
                Lang.c.addDefault(value.getPath(), value.getMessage().split("\n"));
            }
        }
        Lang.c.options().copyDefaults(true);
        wrapper.saveConfig();
        return true;
    }
    
    public static void reload() {
        Lang.config.loadConfig();
        Lang.c = Lang.config.getConfig();
    }
}

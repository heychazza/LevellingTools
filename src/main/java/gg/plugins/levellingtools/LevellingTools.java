package gg.plugins.levellingtools;

import de.tr7zw.itemnbtapi.NBTAPI;
import gg.plugins.levellingtools.command.util.CommandExecutor;
import gg.plugins.levellingtools.command.util.CommandManager;
import gg.plugins.levellingtools.config.Config;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.events.JoinEvent;
import gg.plugins.levellingtools.events.MineEvent;
import gg.plugins.levellingtools.events.PreMineEvent;
import gg.plugins.levellingtools.hook.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LevellingTools extends JavaPlugin {

    public void onEnable() {
        saveDefaultConfig();

        new JoinEvent(this);
        new PreMineEvent(this);
        new MineEvent(this);

        getLogger().info("");
        getLogger().info("");
        getLogger().info("  _                        _  _  _                 _                 _      ");
        getLogger().info(" | |                      | || |(_)               | |               | |     ");
        getLogger().info(" | |      ___ __   __ ___ | || | _  _ __    __ _  | |_  ___    ___  | | ___ ");
        getLogger().info(" | |     / _ \\ \\ / // _ \\| || || || '_ \\  / _` | | __|/ _ \\  / _ \\ | |/ __|");
        getLogger().info(" | |____|  __/ \\ V /|  __/| || || || | | || (_| | | |_| (_) || (_) || |\\__ \\");
        getLogger().info(" |______|\\___|  \\_/  \\___||_||_||_||_| |_| \\__, |  \\__|\\___/  \\___/ |_||___/");
        getLogger().info("                                            __/ |                           ");
        getLogger().info("                                           |___/                            ");
        getLogger().info("");
        getLogger().info("");

        NBTAPI.setLogging(false);

        if (hook("PlaceholderAPI")) new PlaceholderAPIHook(this).register();
        hook("WorldGuard");

        handleReload();
        registerCommands();

    }

    public void onDisable() {

    }

    private CommandManager commandManager;

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private void registerCommands() {
        commandManager = new CommandManager(this);
        this.getCommand("levellingtools").setExecutor(new CommandExecutor(this));

        if (getCommand("levellingtools").getPlugin() != this) {
            getLogger().warning("/levellingtools command is being handled by plugin other than " + getDescription().getName() + ". You must use /levellingtools:levellingtools instead.");
        }
    }

    public void handleReload() {
        reloadConfig();
        Lang.init(new Config(this, "lang.yml"));
        new ConfigCache(this);
        ConfigCache.setup();
    }


    private boolean hook(String plugin) {
        boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) getLogger().info(String.format("Located and hooked into %s.", plugin));
        return enabled;
    }

}

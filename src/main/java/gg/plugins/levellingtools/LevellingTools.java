package gg.plugins.levellingtools;

import de.tr7zw.itemnbtapi.NBTAPI;
import gg.plugins.levellingtools.command.util.CommandExecutor;
import gg.plugins.levellingtools.command.util.CommandManager;
import gg.plugins.levellingtools.config.Config;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.entity.PlayerEntity;
import gg.plugins.levellingtools.events.JoinEvent;
import gg.plugins.levellingtools.events.MineEvent;
import gg.plugins.levellingtools.events.PreMineEvent;
import gg.plugins.levellingtools.hook.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevellingTools extends JavaPlugin {
    private CommandManager commandManager;
    private Map<UUID, PlayerEntity> playerEntities;

    public Map<UUID, PlayerEntity> getPlayerEntities() {
        return playerEntities;
    }

    public void onEnable() {
        this.saveDefaultConfig();
        new JoinEvent(this);
        new PreMineEvent(this);
        new MineEvent(this);
        this.getLogger().info("");
        this.getLogger().info("");
        this.getLogger().info("  _                        _  _  _                 _                 _      ");
        this.getLogger().info(" | |                      | || |(_)               | |               | |     ");
        this.getLogger().info(" | |      ___ __   __ ___ | || | _  _ __    __ _  | |_  ___    ___  | | ___ ");
        this.getLogger().info(" | |     / _ \\ \\ / // _ \\| || || || '_ \\  / _` | | __|/ _ \\  / _ \\ | |/ __|");
        this.getLogger().info(" | |____|  __/ \\ V /|  __/| || || || | | || (_| | | |_| (_) || (_) || |\\__ \\");
        this.getLogger().info(" |______|\\___|  \\_/  \\___||_||_||_||_| |_| \\__, |  \\__|\\___/  \\___/ |_||___/");
        this.getLogger().info("                                            __/ |                           ");
        this.getLogger().info("                                           |___/                            ");
        this.getLogger().info("");
        this.getLogger().info("");
        NBTAPI.setLogging(false);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        if (this.hook("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
        this.hook("WorldGuard");
        this.handleReload();
        this.registerCommands();
        playerEntities = new HashMap<>();
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getPlayerEntities().containsKey(player.getUniqueId()))
                getPlayerEntities().get(player.getUniqueId()).save();
        }
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private void registerCommands() {
        this.commandManager = new CommandManager(this);
        this.getCommand("levellingtools").setExecutor(new CommandExecutor(this));
        if (this.getCommand("levellingtools").getPlugin() != this) {
            this.getLogger().warning("/levellingtools command is being handled by plugin other than " + this.getDescription().getName() + ". You must use /levellingtools:levellingtools instead.");
        }
    }

    public void handleReload() {
        this.reloadConfig();
        Lang.init(new Config(this, "lang.yml"));
        new ConfigCache(this);
        ConfigCache.setup();
    }

    private boolean hook(final String plugin) {
        final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) {
            this.getLogger().info(String.format("Located and hooked into %s.", plugin));
        }
        return enabled;
    }
}

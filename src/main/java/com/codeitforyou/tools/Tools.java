package com.codeitforyou.tools;

import com.codeitforyou.lib.api.actions.ActionManager;
import com.codeitforyou.tools.command.util.CommandExecutor;
import com.codeitforyou.tools.command.util.CommandManager;
import com.codeitforyou.tools.config.CachedConfig;
import com.codeitforyou.tools.config.Lang;
import com.codeitforyou.tools.hook.PlaceholderAPIHook;
import com.codeitforyou.tools.hook.WorldGuardHook;
import com.codeitforyou.tools.listener.JoinListener;
import com.codeitforyou.tools.listener.MineListener;
import com.codeitforyou.tools.maven.LibraryLoader;
import com.codeitforyou.tools.maven.MavenLibrary;
import com.codeitforyou.tools.maven.Repository;
import com.codeitforyou.tools.nbt.NBT;
import com.codeitforyou.tools.storage.PlayerData;
import com.codeitforyou.tools.storage.StorageHandler;
import com.codeitforyou.tools.storage.mongodb.MongoDBHandler;
import com.codeitforyou.tools.storage.mysql.MySQLHandler;
import com.codeitforyou.tools.storage.sqlite.SQLiteHandler;
import com.codeitforyou.tools.util.Common;
import com.codeitforyou.tools.util.ConsoleFilter;
import com.codeitforyou.tools.util.Metrics;
import com.codeitforyou.tools.util.StorageType;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@MavenLibrary(groupId = "dev.morphia.morphia", artifactId = "core", version = "1.5.2")
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-core", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "com.github.j256", artifactId = "ormlite-jdbc", version = "4.43", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "org.apache.logging.log4j", artifactId = "log4j-core", version = "2.7")
@MavenLibrary(groupId = "com.github.CodeMC.WorldGuardWrapper", artifactId = "worldguardwrapper", version = "master-5e50edd862-1", repo = @Repository(url = "https://jitpack.io"))
@MavenLibrary(groupId = "org.xerial", artifactId = "sqlite-jdbc", version = "3.7.2")
public class Tools extends JavaPlugin {

    private StorageHandler storageHandler;
    private CommandManager commandManager;
    private final ActionManager actionManager = new ActionManager(this);

    public ActionManager getActionManager() {
        return actionManager;
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public void log(String message) {
        if (CachedConfig.debugMode()) this.getLogger().info("[DEBUG] " + message);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        LibraryLoader.loadAll(Tools.class);
        actionManager.addDefaults();

        new JoinListener(this);
        new MineListener(this);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
        handleReload();

        registerCommands();

        hook("PlaceholderAPI");
        hook("WorldGuard");

        new Metrics(this);

        if (getConfig().getBoolean("settings.autosave", true))
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        getStorageHandler().pushData(player.getUniqueId());
                    }
                }
            }.runTaskTimerAsynchronously(this, (20L * 60) * 10, (20L * 60) * 10);

        if (getConfig().getBoolean("settings.stats.enabled", true))
            new BukkitRunnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getItemInHand().getType() != Material.AIR && (NBT.get(player.getItemInHand()) != null && NBT.get(player.getItemInHand()).hasKey("omnitool"))) {
                            PlayerData playerData = PlayerData.get(player.getUniqueId());
                            Common.sendActionbar(player, Lang.ACTIONBAR_STATS.asString(Lang.PREFIX.asString(), playerData.getLevel(), Common.getProgressBar(playerData), Common.getProgress(playerData)));
                        }
                    }
                }
            }.runTaskTimer(this, 20L, 20L);
    }


    @Override
    public void onDisable() {
        for (UUID player : PlayerData.users.keySet()) {
            getStorageHandler().pushData(player);
        }
    }

    private void setupStorage() {
        StorageType storageType = StorageType.valueOf(getConfig().getString("settings.storage.type", "SQLITE"));

        String prefix = getConfig().getString("settings.storage.prefix", "lt_");
        String host = getConfig().getString("settings.storage.host", "localhost");
        int port = getConfig().getInt("settings.storage.port");
        String database = getConfig().getString("settings.storage.database", "levellingtools");
        String username = getConfig().getString("settings.storage.username", "");
        String password = getConfig().getString("settings.storage.password", "");

        switch (storageType) {
            case SQLITE:
                storageHandler = new SQLiteHandler(getDataFolder().getPath());
                break;
            case MYSQL:
                if (port == 0) port = 3306;
                storageHandler = new MySQLHandler(prefix, host, port, database, username, password);
                break;
            case MONGODB:
                if (port == 0) port = 27017;
                storageHandler = new MongoDBHandler(prefix, host, port, database, username, password);
                break;
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    private void registerCommands() {
        commandManager = new CommandManager(this);
        getCommand(getDescription().getName().toLowerCase()).setExecutor(new CommandExecutor(this));
        if (getCommand(getDescription().getName().toLowerCase()).getPlugin() != this) {
            getLogger().warning("/" + getDescription().getName().toLowerCase() + " command is being handled by plugin other than " + getDescription().getName() + ". You must use /" + getDescription().getName().toLowerCase() + ":" + getDescription().getName().toLowerCase() + " instead.");
        }
    }

    public void handleReload() {
        CachedConfig.setup();
        Lang.init(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                setupStorage();
            }
        }.runTaskAsynchronously(this);
    }

    private void hook(final String plugin) {
        final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) {
            if (plugin.equalsIgnoreCase("PlaceholderAPI")) new PlaceholderAPIHook(this).register();
            if (plugin.equalsIgnoreCase("WorldGuard")) new WorldGuardHook();
        }
    }
}

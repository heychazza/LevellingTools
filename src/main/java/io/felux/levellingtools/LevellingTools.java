package io.felux.levellingtools;

import io.felux.levellingtools.command.util.CommandExecutor;
import io.felux.levellingtools.command.util.CommandManager;
import io.felux.levellingtools.config.CachedConfig;
import io.felux.levellingtools.config.Lang;
import io.felux.levellingtools.hook.PlaceholderAPIHook;
import io.felux.levellingtools.hook.WorldGuardHook;
import io.felux.levellingtools.listener.JoinListener;
import io.felux.levellingtools.listener.MineListener;
import io.felux.levellingtools.maven.LibraryLoader;
import io.felux.levellingtools.maven.MavenLibrary;
import io.felux.levellingtools.maven.Repository;
import io.felux.levellingtools.nbt.NBT;
import io.felux.levellingtools.storage.PlayerData;
import io.felux.levellingtools.storage.StorageHandler;
import io.felux.levellingtools.storage.mongodb.MongoDBHandler;
import io.felux.levellingtools.storage.mysql.MySQLHandler;
import io.felux.levellingtools.storage.sqlite.SQLiteHandler;
import io.felux.levellingtools.util.Common;
import io.felux.levellingtools.util.ConsoleFilter;
import io.felux.levellingtools.util.Metrics;
import io.felux.levellingtools.util.StorageType;
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
public class LevellingTools extends JavaPlugin {

    private StorageHandler storageHandler;
    private CommandManager commandManager;

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public void log(String message) {
        if (CachedConfig.debugMode()) this.getLogger().info("[DEBUG] " + message);
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        saveDefaultConfig();
        getBanner();

        Common.loading("libraries");
        LibraryLoader.loadAll(LevellingTools.class);

        Common.loading("events");
        new JoinListener(this);
        new MineListener(this);

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        org.apache.logging.log4j.core.Logger logger;
        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
        handleReload();

        Common.loading("commands");
        registerCommands();

        Common.loading("hooks");
        hook("PlaceholderAPI");
        hook("WorldGuard");

        Common.loading("metrics");
        new Metrics(this);

        Common.sendConsoleMessage(" ");

        if (getConfig().getBoolean("settings.make-console-talkative", true))
            getLogger().info("Successfully enabled in " + (System.currentTimeMillis() - start) + "ms.");

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

    private void getBanner() {
        Common.sendConsoleMessage("&b ");
        Common.sendConsoleMessage("&b    __ ______");
        Common.sendConsoleMessage("&b   / //_  __/");
        Common.sendConsoleMessage("&b  / /__/ /   " + "  &7" + getDescription().getName() + " v" + getDescription().getVersion());
        Common.sendConsoleMessage("&b /____/_/    " + "  &7Running on Bukkit - " + getServer().getName());
        Common.sendConsoleMessage("&b ");
        Common.sendMessage("Created by Felux.io Development.");
        Common.sendConsoleMessage("&b ");
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

//        Common.loading(storageType.toLowerCase() + " storage");

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
        getCommand("levellingtools").setExecutor(new CommandExecutor(this));
        if (getCommand("levellingtools").getPlugin() != this) {
            getLogger().warning("/levellingtools command is being handled by plugin other than " + getDescription().getName() + ". You must use /levellingtools:levellingtools instead.");
        }
    }

    public void handleReload() {
        Common.loading("config");
        Lang.init(this);
        CachedConfig.setup();

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

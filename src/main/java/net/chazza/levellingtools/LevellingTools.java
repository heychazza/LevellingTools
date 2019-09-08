package net.chazza.levellingtools;

import net.chazza.levellingtools.command.util.CommandExecutor;
import net.chazza.levellingtools.command.util.CommandManager;
import net.chazza.levellingtools.config.CachedConfig;
import net.chazza.levellingtools.config.Lang;
import net.chazza.levellingtools.hook.PlaceholderAPIHook;
import net.chazza.levellingtools.hook.WorldGuardHook;
import net.chazza.levellingtools.listener.JoinListener;
import net.chazza.levellingtools.listener.MineListener;
import net.chazza.levellingtools.maven.LibraryLoader;
import net.chazza.levellingtools.maven.MavenLibrary;
import net.chazza.levellingtools.maven.Repository;
import net.chazza.levellingtools.storage.PlayerData;
import net.chazza.levellingtools.storage.StorageHandler;
import net.chazza.levellingtools.storage.mongodb.MongoDBHandler;
import net.chazza.levellingtools.storage.mysql.MySQLHandler;
import net.chazza.levellingtools.storage.sqlite.SQLiteHandler;
import net.chazza.levellingtools.util.Common;
import net.chazza.levellingtools.util.ConsoleFilter;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
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

        Common.sendConsoleMessage(" ");
        getLogger().info("Successfully enabled in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private void getBanner() {
        Common.sendConsoleMessage("&b ");
        Common.sendConsoleMessage("&b    __ ______");
        Common.sendConsoleMessage("&b   / //_  __/");
        Common.sendConsoleMessage("&b  / /__/ /   " + "  &7" + getDescription().getName() + " v" + getDescription().getVersion());
        Common.sendConsoleMessage("&b /____/_/    " + "  &7Running on Bukkit - " + getServer().getName());
        Common.sendConsoleMessage("&b ");
    }

    @Override
    public void onDisable() {
        PlayerData.users.forEach(((uuid, playerData) -> {
            getStorageHandler().pushData(uuid);
        }));
    }

    private void setupStorage() {
        String storageType = Objects.requireNonNull(getConfig().getString("settings.storage.type", "SQLITE")).toUpperCase();

        if (!Arrays.asList("SQLITE", "MYSQL", "MONGODB").contains(storageType)) {
            storageType = "SQLITE";
        }

        Common.loading(storageType.toLowerCase() + " storage");

        switch (storageType) {
            case "SQLITE":
                storageHandler = new SQLiteHandler(getDataFolder().getPath());
                break;
            case "MYSQL":
                storageHandler = new MySQLHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 3306),
                        getConfig().getString("settings.storage.database", "levellingtools"),
                        getConfig().getString("settings.storage.username", "root"),
                        getConfig().getString("settings.storage.password", "qwerty123"));
                break;
            case "MONGODB":
                storageHandler = new MongoDBHandler(
                        getConfig().getString("settings.storage.prefix", ""),
                        getConfig().getString("settings.storage.host", "localhost"),
                        getConfig().getInt("settings.storage.port", 27017),
                        getConfig().getString("settings.storage.database", "levellingtools"),
                        getConfig().getString("settings.storage.username", ""),
                        getConfig().getString("settings.storage.password", "")
                );
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
        reloadConfig();

        Common.loading("config");
        Lang.init(this);
        new CachedConfig(this);
        CachedConfig.setup();

        setupStorage();
    }

    private void hook(final String plugin) {
        final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) {
            if (plugin.equalsIgnoreCase("PlaceholderAPI")) new PlaceholderAPIHook(this).register();
            if (plugin.equalsIgnoreCase("WorldGuard")) new WorldGuardHook();
        }
    }
}

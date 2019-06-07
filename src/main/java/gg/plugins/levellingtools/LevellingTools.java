package gg.plugins.levellingtools;

import de.tr7zw.itemnbtapi.NBTAPI;
import gg.plugins.levellingtools.config.Config;
import gg.plugins.levellingtools.config.ConfigCache;
import gg.plugins.levellingtools.config.Lang;
import gg.plugins.levellingtools.events.ToolJoinEvent;
import gg.plugins.levellingtools.events.ToolMineEvent;
import gg.plugins.levellingtools.events.ToolPreMineEvent;
import gg.plugins.levellingtools.hook.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LevellingTools extends JavaPlugin {

    public void onEnable() {
        saveDefaultConfig();
        Lang.init(new Config(this, "lang.yml"));

        new ToolJoinEvent(this);
        new ToolPreMineEvent(this);
        new ToolMineEvent(this);

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

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("[HOOK] Configuring WorldGuard.");
            getLogger().warning(String.format("Hooking into %s.", "WorldGuard"));
            new PlaceholderAPIHook(this).register();
        }

        new ConfigCache(this);
        ConfigCache.setup();
    }

    public void onDisable() {
    }

    private boolean hook(String plugin) {
        boolean enabled = Bukkit.getPluginManager().isPluginEnabled(plugin);
        if (enabled) getLogger().info(String.format("Hooking into %s.", "WorldGuard"));
        return enabled;
    }

}

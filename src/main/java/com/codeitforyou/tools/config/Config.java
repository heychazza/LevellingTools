package com.codeitforyou.tools.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private final String folderName;
    private final String fileName;

    public Config(final JavaPlugin instance, final String folderName, final String fileName) {
        this.plugin = instance;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    public Config(final JavaPlugin instance, final String fileName) {
        this.plugin = instance;
        this.fileName = fileName;
        this.folderName = null;
    }

    public void setConfig(final FileConfiguration c) {
        this.config = c;
    }

    public FileConfiguration getConfig() {
        if (this.config == null) {
            this.loadConfig();
        }
        return this.config;
    }

    public FileConfiguration loadConfig() {
        if (this.configFile == null) {
            if (this.folderName != null && !this.folderName.isEmpty()) {
                this.configFile = new File(this.plugin.getDataFolder() + File.separator + this.folderName, this.fileName);
            } else {
                this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
            }
        }
        return this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void saveConfig() {
        if (this.config == null || this.configFile == null) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
        }
    }
}

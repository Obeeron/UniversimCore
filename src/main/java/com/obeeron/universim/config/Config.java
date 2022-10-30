package com.obeeron.universim.config;

import com.obeeron.universim.Universim;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {
    private final Universim plugin;
    private final String configPath;
    private File configFile = null;
    private FileConfiguration config = null;

    public Config(String configPath){
        this.plugin = Universim.getInstance();
        this.configPath = configPath;
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), configPath);

        this.config = YamlConfiguration.loadConfiguration(configFile);

        // Load default config
        InputStream defaultStream = this.plugin.getResource(configPath);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.config.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.config == null)
            reloadConfig();
        return this.config;
    }

    public void saveConfig() {
        if (this.config == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Could not save config to " + this.configFile);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), configPath);

        if (!this.configFile.exists()) {
            this.plugin.saveResource(configPath, false);
        }
    }

}

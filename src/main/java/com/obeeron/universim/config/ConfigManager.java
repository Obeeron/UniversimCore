package com.obeeron.universim.config;

import java.util.HashMap;

public class ConfigManager {
    private static ConfigManager instance;

    private final HashMap<String, Config> configs = new HashMap<>();

    public static ConfigManager getInstance() {
        if (instance == null)
            instance = new ConfigManager();
        return instance;
    }

    public Config getConfig(String configPath) {
        if (configs.containsKey(configPath))
            return configs.get(configPath);

        Config config = new Config(configPath);
        configs.put(configPath, config);
        return config;
    }

    public void reloadAll() {
        for (Config config : configs.values())
            config.reloadConfig();
    }
}

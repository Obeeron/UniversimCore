package com.obeeron.universim.modules.navy;

import org.bukkit.Bukkit;

import com.obeeron.universim.Universim;

public class BoatManager {
        private static BoatManager instance;

    public static void initialize() {
        Bukkit.getServer().getPluginManager().registerEvents(new BoatListener(), Universim.getInstance());
        Universim.getInstance().getLogger().info("Boat manager successfully initialized");
    }

    // SINGLETON

    public static BoatManager getInstance() {
        if (instance == null)
            instance = new BoatManager();
        return instance;
    }

}

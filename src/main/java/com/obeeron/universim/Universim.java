package com.obeeron.universim;

import com.obeeron.universim.modules.itemHolder.ItemHolderManager;
import com.obeeron.universim.modules.universimItems.UnivItemManager;
import com.obeeron.universim.commands.UniversimCommand;
import com.obeeron.universim.config.ConfigManager;
import com.obeeron.universim.modules.recipes.CraftManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Objects;

public final class Universim extends JavaPlugin {

    private static Universim instance;
    public final String NAMESPACE;

    public static final String PERM_PREFIX = "universim";
    public static final String PERM_STAFF_PREFIX = PERM_PREFIX + ".staff";
    public static final String PERM_RELOAD = PERM_STAFF_PREFIX + ".reload";
    public static final String PERM_GIVE = PERM_STAFF_PREFIX + ".give";
    public static final String PERM_MANAGE_UNIVID = PERM_STAFF_PREFIX + ".univid";
    public static final String PERM_SHOWMETA = PERM_STAFF_PREFIX + ".showmeta";

    public Universim() {
        super();
        instance = this;
        NAMESPACE = getName().toLowerCase(Locale.ROOT);
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling Universim...");

        UnivItemManager.initialize();
        CraftManager.initialize();
        ItemHolderManager.initialize();

        registerCommands();

        getLogger().info("Universim is enabled!");
    }

    private void registerCommands() {
        PluginCommand universimPluginCommand = Objects.requireNonNull(getCommand("universim"));
        universimPluginCommand.setExecutor(new UniversimCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Universim is disabled!");
    }

    public static Universim getInstance() {
        return instance;
    }

    public static String getNamespace() {
        return Universim.getInstance().NAMESPACE;
    }

    public void reload() {
        getLogger().info("Reloading Universim...");

        this.onDisable();
        this.reloadConfig();
        ConfigManager.getInstance().reloadAll();
        this.onEnable();

        getLogger().info("Universim reloaded!");
    }
}

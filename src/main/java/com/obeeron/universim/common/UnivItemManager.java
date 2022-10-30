package com.obeeron.universim.common;

import com.obeeron.universim.Universim;
import com.obeeron.universim.config.Config;
import com.obeeron.universim.config.ConfigManager;
import com.obeeron.universim.config.parsers.UnivItemParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class UnivItemManager {
    private static UnivItemManager instance;
    private HashMap<NamespacedKey, ItemStack> univItems = new HashMap<>();

    // INITIALIZATION

    public static void initialize() {
        getInstance().registerUnivItems();
    }

    private void registerUnivItems() {
        Config univItemsConfig = ConfigManager.getInstance().getConfig("univItems.yml");
        univItemsConfig.saveDefaultConfig();
        univItems = UnivItemParser.parseUnivItems(univItemsConfig.getConfig());
    }

    public static Material getDefaultMaterial() {
        return Material.STICK;
    }

    // GETTERS

    public ItemStack getUnivItem(NamespacedKey resultNSK) {
        return univItems.get(resultNSK);
    }

    @Nullable
    public Material getMaterialFromNSK(NamespacedKey ingredientNSK) {
        if (ingredientNSK == null)
            return null;
        if (ingredientNSK.getNamespace().equals(Universim.getNamespace())){
            ItemStack ingredient = getUnivItem(ingredientNSK);
            if (ingredient != null)
                return ingredient.getType();
            return null;
        }
        return Material.getMaterial(ingredientNSK.getKey().toUpperCase());
    }

    public Collection<String> getAllUnivIds() {
        return univItems.keySet().stream().map(NamespacedKey::getKey).toList();
    }

    // SINGLETON

    public static UnivItemManager getInstance() {
        if (instance == null)
            instance = new UnivItemManager();
        return instance;
    }
}

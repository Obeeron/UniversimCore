package com.obeeron.universim;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class UVSCore {

    public static NamespacedKey univNSK(String key) {
        if (key == null)
            return null;
        return new NamespacedKey(Universim.getInstance(), key.toLowerCase());
    }

    @Nullable
    public static NamespacedKey getUnivId(ItemStack item) {
        if (item == null)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;
        return univNSK(meta.getPersistentDataContainer().get(univNSK("id"), PersistentDataType.STRING));
    }

    public static void setUnivId(ItemMeta meta, String univ_id) {
        meta.getPersistentDataContainer().set(univNSK("id"), PersistentDataType.STRING, univ_id);
    }

    public static void setUnivId(ItemStack item, String univ_id) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        setUnivId(meta, univ_id);
        item.setItemMeta(meta);
    }

    public static void removeUnivId(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        meta.getPersistentDataContainer().remove(univNSK("id"));
        item.setItemMeta(meta);
    }

    // If @str has a namespace key format, then return the corresponding namespace key
    // Else, if @str is a valid material, return its minecraft namespace key
    // Else, return an universim namespace key
    public static NamespacedKey getNSKFromStr(String str) {
        if (str == null)
            return null;
        // If str is already a NamespacedKey, return it
        if (str.contains(":"))
            return NamespacedKey.fromString(str);

        Material minecraftMaterial = Material.getMaterial(str.toUpperCase());
        if (minecraftMaterial != null)
            return minecraftMaterial.getKey();
        return UVSCore.univNSK(str);
    }
}

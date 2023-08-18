package com.obeeron.universim.modules.itemHolder;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;
import com.obeeron.universim.modules.itemHolder.listeners.ItemHolderListener;
import com.obeeron.universim.modules.universimItems.UnivItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;

public class ItemHolderManager {
    public static final NamespacedKey ITEM_HOLDER_NSK = UVSCore.univNSK("item_holder");

    public static void initialize() {
        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(new ItemHolderListener(), Universim.getInstance());
    }

    public static void handleItemHolderPlaceEvent(ItemFrame itemFrame) {
        UVSCore.setUnivId(itemFrame, ITEM_HOLDER_NSK.getKey());
    }

    public static void handleItemHolderInteractEvent(ItemFrame itemFrame, ItemStack itemInHand) {
        if (itemInHand.getType().isAir()) {
            return;
        }

        if (itemFrame.getItem().getType().isAir()) {
            // itemFrame is empty and item is being placed inside
            itemFrame.setVisible(false);
        }
    }

    public static boolean isItemHolder(PersistentDataHolder itemFrame) {
        return UVSCore.hasUnivId(itemFrame, ITEM_HOLDER_NSK.getKey());
    }

    public static void handleItemHolderRemoveItemEvent(ItemFrame itemFrame) {
        itemFrame.setVisible(true);
    }

    public static void handleItemHolderBreakEvent(ItemFrame itemFrame, HangingBreakEvent event) {
        ItemStack itemHolderItem =  UnivItemManager.getInstance().getUnivItem(ITEM_HOLDER_NSK);
        if (itemHolderItem == null) {
            return;
        }
        event.setCancelled(true);
        Location location = itemFrame.getLocation();
        World world = location.getWorld();
        if (world == null)
            return;

        world.dropItem(location, itemHolderItem);
        ItemStack item = itemFrame.getItem();
        if (!item.getType().isAir()) {
            world.dropItem(location, item);
        }
        itemFrame.remove();
    }
}

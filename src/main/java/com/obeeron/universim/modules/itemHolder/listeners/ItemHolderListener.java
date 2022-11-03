package com.obeeron.universim.modules.itemHolder.listeners;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.modules.itemHolder.ItemHolderManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemHolderListener implements Listener {

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            Player player = event.getPlayer();
            if (player == null)
                return;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() != Material.ITEM_FRAME){
                itemInHand = player.getInventory().getItemInOffHand();
                if (itemInHand.getType() != Material.ITEM_FRAME)
                    return;
            }
            if (UVSCore.hasUnivId(itemInHand.getItemMeta(), ItemHolderManager.ITEM_HOLDER_NSK.getKey())) {
                ItemHolderManager.handleItemHolderPlaceEvent(itemFrame);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame itemFrame){
            if (ItemHolderManager.isItemHolder(itemFrame)){
            // Get the item in the hand that triggered the event
            ItemStack itemInHand = event.getHand() == EquipmentSlot.HAND ? event.getPlayer().getInventory().getItemInMainHand() : event.getPlayer().getInventory().getItemInOffHand();
            ItemHolderManager.handleItemHolderInteractEvent(itemFrame, itemInHand);
            }
        }
    }

    // Event for when an item is removed from an item frame
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (ItemHolderManager.isItemHolder(itemFrame)) {
                ItemHolderManager.handleItemHolderRemoveItemEvent(itemFrame);
            }
        }
    }

    // Event for when an item is removed from an item frame
    @EventHandler
    public void onEntityHangingBreak(HangingBreakEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY)
            return;

        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (ItemHolderManager.isItemHolder(itemFrame)) {
                ItemHolderManager.handleItemHolderBreakEvent(itemFrame,event);
            }
        }
    }

    @EventHandler
    public void onEntityHangingBreakByEntity(HangingBreakByEntityEvent event){
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY)
            return;

        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (ItemHolderManager.isItemHolder(itemFrame)) {
                if (event.getRemover() instanceof Player player && player.getGameMode() == org.bukkit.GameMode.CREATIVE)
                    return;
                ItemHolderManager.handleItemHolderBreakEvent(itemFrame,event);
            }
        }
    }
}

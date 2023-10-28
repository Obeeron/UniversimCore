package com.obeeron.universim.modules.navy;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.obeeron.universim.UVSCore;

public class BoatListener implements Listener {
    private HashMap<String, String> waitingEvent = new HashMap<String, String>(20);

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            EquipmentSlot hand = event.getHand();
            if (hand != null && hand == EquipmentSlot.HAND) {
                String key = UVSCore.getItemId(event.getItem()).asString();
                if (key.contains("universim")) {
                    waitingEvent.put(event.getPlayer().getUniqueId().toString(), key.split(":")[1]);
                }
            }
        }
    }


    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        if (event.getEntity() instanceof Entity boat) {
            if (waitingEvent.containsKey(playerId)) {
                // this should be better way to do it, but bukkit nbt tags seems to not be client side
                // so optifine cannot use them
                // PersistentDataContainer pdc = boat.getPersistentDataContainer();
                // NamespacedKey namespacedKey = new NamespacedKey("universim", "id");
                // pdc.set(namespacedKey, PersistentDataType.STRING, "canoe");

                // using the name instead
                boat.setCustomName("Canoe");
                waitingEvent.remove(playerId);
            }
        }
    }
}

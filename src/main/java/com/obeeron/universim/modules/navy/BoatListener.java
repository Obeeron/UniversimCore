package com.obeeron.universim.modules.navy;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.Universim;

public class BoatListener implements Listener {
    private HashMap<String, String> waitingEvent = new HashMap<String, String>(20);

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
            String key = UVSCore.getItemId(event.getItem()).asString();
            if (key.contains("universim")) {
                waitingEvent.put(event.getPlayer().getUniqueId().toString(), key.split(":")[1]);
            }
        }
    }


    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        if (waitingEvent.containsKey(playerId)) {
            Universim.getInstance().getLogger().info(waitingEvent.get(playerId));
            waitingEvent.remove(playerId);
        }
    }
}

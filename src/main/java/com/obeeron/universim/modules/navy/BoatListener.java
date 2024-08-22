package com.obeeron.universim.modules.navy;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Boat.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.obeeron.universim.UVSCore;
import com.obeeron.universim.modules.universimItems.UnivItemManager;

public class BoatListener implements Listener {
    private HashMap<String, String> waitingEvent = new HashMap<String, String>(20);
    private List<Material> boatMaterials = List.of(Material.OAK_BOAT, Material.DARK_OAK_BOAT, Material.BIRCH_BOAT,
            Material.ACACIA_BOAT, Material.JUNGLE_BOAT, Material.SPRUCE_BOAT);

    private boolean isBoatItem(ItemStack item) {
        return (boatMaterials.contains(item.getType()));
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        EquipmentSlot hand = event.getHand();
        if (hand == null || !event.hasItem()) {
            return;
        }
        if (hand == EquipmentSlot.HAND) {
            ItemStack item = event.getItem();
            if (isBoatItem(item)) {
                String key = UVSCore.getItemId(item).asString();
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
                String key = waitingEvent.get(playerId);
                if (key.length() > 0) {
                    // this should be better way to do it, but bukkit nbt tags seems to not be
                    // client side
                    // so optifine cannot use them
                    // PersistentDataContainer pdc = boat.getPersistentDataContainer();
                    // NamespacedKey namespacedKey = new NamespacedKey("universim", "id");
                    // pdc.set(namespacedKey, PersistentDataType.STRING, "canoe");

                    // using the name instead
                    String name = key.substring(key.lastIndexOf("_") + 1, key.length());
                    boat.setCustomName(name);
                }
                waitingEvent.remove(playerId);
            }
        }
    }

    @EventHandler
    public void DropBoat(VehicleDestroyEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getVehicle().getType() == EntityType.BOAT) {
            Boat boat = (Boat) event.getVehicle();
            Type type = boat.getBoatType();
            String name = boat.getCustomName();
            if (name != null) {
                String univId = type.toString().toLowerCase() + "_" + name;
                ItemStack item = UnivItemManager.getInstance().getUnivItem(UVSCore.univNSK(univId), false);
                Location loc1 = boat.getLocation();
                loc1.getWorld().dropItem(loc1, item);
                event.setCancelled(true);
                boat.remove();
            }
        }
    }
}

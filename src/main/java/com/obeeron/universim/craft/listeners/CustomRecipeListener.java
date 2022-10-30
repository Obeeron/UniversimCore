package com.obeeron.universim.craft.listeners;

import com.obeeron.universim.craft.CraftManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class CustomRecipeListener implements Listener {
    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        CraftManager.getInstance().handlePrepareEvent(event);
    }
}

package me.array.ArrayPractice.util.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() == null) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof InventoryUI.InventoryUIHolder)) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        InventoryUI.InventoryUIHolder inventoryUIHolder = (InventoryUI.InventoryUIHolder) event.getInventory().getHolder();
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getInventory().equals(event.getClickedInventory())) {
            return;
        }
        InventoryUI ui = inventoryUIHolder.getInventoryUI();
        InventoryUI.ClickableItem item = ui.getCurrentUI().getItem(event.getSlot());
        if (item == null) {
            return;
        }
        item.onClick(event);
    }
}

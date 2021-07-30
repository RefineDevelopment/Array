package xyz.refinedev.practice.profile.hotbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.refinedev.practice.Array;

public class HotbarListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            final Player player = event.getPlayer();

            final HotbarType hotbarType = plugin.getHotbarManager().fromItemStack(event.getItem());
            final HotbarItem hotbarItem = plugin.getHotbarManager().getItem(event.getItem());

            if (hotbarType == null || hotbarItem == null) return;

            event.setCancelled(true);
            player.updateInventory();

            String command  = hotbarItem.getCommand();
            if (command != null) {
                player.chat(command);
            }
        }
    }
}


package me.drizzy.practice.util.menu;

import me.drizzy.practice.util.bootstrap.BootstrappedListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class MenuListener extends BootstrappedListener {

    public MenuListener(me.drizzy.practice.Array Array) {
        super(Array);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }

                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Button button = openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getClick());

                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }

                button.clicked(player, event.getClick());
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());

                    if (newMenu == openMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

                        if (buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            newMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    openMenu.openMenu(player);
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(Array, player::updateInventory, 1L);
                }
            } else {
                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                }

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            openMenu.onClose(player);

            Menu.currentlyOpenedMenus.remove(player.getName());
        }
        player.setMetadata("editorglitch", new FixedMetadataValue(Array, true));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("editorglitch")) {
            player.removeMetadata("editorglitch", Array);

            for ( ItemStack it : player.getInventory().getContents()) {
                if (it != null) {
                    ItemMeta meta = it.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {

                        if (meta.getDisplayName().contains("§b§c§d§e")) {
                            player.getInventory().remove(it);
                        }
                    }
                }
            }
        }
    }

}
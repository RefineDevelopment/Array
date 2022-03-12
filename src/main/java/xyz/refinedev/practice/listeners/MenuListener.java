package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.MenuHandler;
import xyz.refinedev.practice.util.other.TaskUtil;

@RequiredArgsConstructor
public class MenuListener implements Listener {

    private final Array plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        MenuHandler menuHandler = this.plugin.getMenuHandler();

        Player player = (Player) event.getWhoClicked();
        Menu openMenu = menuHandler.getOpenedMenus().get(player.getUniqueId());

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

                button.clicked(plugin, player, event.getClick());
                button.clicked(plugin, player, event.getSlot(), event.getClick(), event.getHotbarButton());

                if (menuHandler.getOpenedMenus().containsKey(player.getUniqueId())) {
                    Menu newMenu = menuHandler.getOpenedMenus().get(player.getUniqueId());

                    if (newMenu == openMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

                        if (buttonUpdate) {
                            openMenu.setClosedByMenu(true);
                            menuHandler.openMenu(openMenu, player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getClick())) {
                    openMenu.setClosedByMenu(true);
                    menuHandler.openMenu(openMenu, player);
                }

                if (event.isCancelled()) {
                    TaskUtil.runLater(player::updateInventory, 1L);
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
        MenuHandler menuHandler = this.plugin.getMenuHandler();
        Player player = (Player) event.getPlayer();
        Menu openMenu = menuHandler.getOpenedMenus().get(player.getUniqueId());

        if (openMenu != null) {
            openMenu.onClose(this.plugin, player);

            menuHandler.getOpenedMenus().remove(player.getUniqueId());
        }
    }

}
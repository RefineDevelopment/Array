package me.drizzy.practice.kiteditor;

import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.kiteditor.menu.KitManagementMenu;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitEditorListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getKitEditor().isRenaming()) {
            event.setCancelled(true);

            if (event.getMessage().length() > 16) {
                event.getPlayer().sendMessage(CC.RED + "A kit name cannot be more than 16 characters long.");
                return;
            }
            if (event.getMessage().contains("&")) {
                event.getPlayer().sendMessage(CC.RED + "Please Don't Use Color Codes or Symbols.");
                return;
            }

            if (!profile.isInFight()) {
                new KitManagementMenu(profile.getKitEditor().getSelectedKit()).openMenu(event.getPlayer());
            }

            profile.getKitEditor().getSelectedKitInventory().setCustomName(event.getMessage());
            profile.getKitEditor().setActive(false);
            profile.getKitEditor().setRename(false);
            profile.getKitEditor().setSelectedKit(null);
            event.getPlayer().closeInventory();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully renamed");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    event.setCancelled(true);
                    return;
                }
            }

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (!profile.isInSomeSortOfFight() && player.getGameMode() == GameMode.SURVIVAL) {
                if (!profile.isInBrackets() && !profile.isInLMS()) {
                    Inventory clicked = event.getClickedInventory();

                    if (profile.getKitEditor().isActive()) {
                        if (clicked == null) {
                            event.setCancelled(true);
                            event.setCursor(null);
                            player.updateInventory();
                        } else if (clicked.equals(player.getOpenInventory().getTopInventory())) {
                            if (event.getCursor().getType() != Material.AIR &&
                                    event.getCurrentItem().getType() == Material.AIR ||
                                    event.getCursor().getType() != Material.AIR &&
                                            event.getCurrentItem().getType() != Material.AIR) {
                                event.setCancelled(true);
                                event.setCursor(null);
                                player.updateInventory();
                            }
                        }
                    } else {
                        if (clicked != null && clicked.equals(player.getInventory())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryInteractEvent event) {
        Player player=(Player) event.getWhoClicked();
        Profile profile=Profile.getByUuid(player);
        if (profile.isInLobby() && player.getOpenInventory() == null) {
            if (profile.getKitEditor().isActive()) {
                PlayerUtil.reset(player, false);
                profile.refreshHotbar();
                profile.getKitEditor().setActive(false);
                player.sendMessage(CC.translate("&7You were caught doing the kit editor bug!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClose(InventoryClickEvent event) {
        Player player=(Player) event.getWhoClicked();
        Profile profile=Profile.getByUuid(player);
        if (profile.isInLobby() && player.getOpenInventory() == null) {
            if (profile.getKitEditor().isActive()) {
                PlayerUtil.reset(player, false);
                profile.refreshHotbar();
                profile.getKitEditor().setActive(false);
                player.sendMessage(CC.translate("&7You were caught doing the kit editor bug!"));
            }
        }
    }

}

package me.drizzy.practice.event.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.event.EventType;
import org.bukkit.Material;
import me.drizzy.practice.event.types.brackets.command.BracketsHostCommand;
import me.drizzy.practice.event.types.lms.command.LMSHostCommand;
import me.drizzy.practice.event.types.parkour.command.ParkourHostCommand;
import me.drizzy.practice.event.types.skywars.command.SkyWarsHostCommand;
import me.drizzy.practice.event.types.spleef.command.SpleefHostCommand;
import me.drizzy.practice.event.types.sumo.command.SumoHostCommand;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSelectEventMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&bSelect an event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        final List<Integer> occupied = new ArrayList<>();
        final int[] taken = {11,12,13,14,15,20,21,22,23,24,29,30,31,32,33};
        for ( int take : taken ) {
            occupied.add(take);
        }
        for ( int glassslots = 0; glassslots < 45; ++glassslots ) {
            if (!occupied.contains(glassslots)) {
                buttons.put(glassslots, new GlassButton());
            }
        }
        buttons.put(13, new SelectEventButton(EventType.LMS));
        buttons.put(12, new SelectEventButton(EventType.BRACKETS));
        buttons.put(11, new SelectEventButton(EventType.SUMO));
        buttons.put(14, new SelectEventButton(EventType.PARKOUR));
        buttons.put(15, new SelectEventButton(EventType.SKYWARS));
        buttons.put(30, new SelectEventButton(EventType.SPLEEF));
        buttons.put(31, new SelectEventButton(EventType.RUNNER));
        buttons.put(32, new SelectEventButton(EventType.KOTH));
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectEventButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.MENU_BAR);
            switch (eventType.getTitle()) {
                case "&b&lBrackets":
                    lore.add(CC.GRAY + "Fight through rounds and");
                    lore.add(CC.GRAY + "beat your opponent in 1v1");
                    lore.add(CC.GRAY + "duels. The last player wins!");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&b&lSumo":
                    lore.add(CC.GRAY + "Knockback everyone off the");
                    lore.add(CC.GRAY + "platform until your are");
                    lore.add(CC.GRAY + "the last player alive");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&b&lLMS":
                    lore.add(CC.GRAY + "Fight for your life");
                    lore.add(CC.GRAY + "and kill everyone to");
                    lore.add(CC.GRAY + "be the last man standing");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&b&lParkour":
                    lore.add(CC.GRAY + "Make your way through the");
                    lore.add(CC.GRAY + "course and beat the others!");
                    lore.add(CC.GRAY + "The player to reach the goal wins");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&b&lSpleef":
                    lore.add(CC.GRAY + "Break the snow blocks");
                    lore.add(CC.GRAY + "and avoid falling into");
                    lore.add(CC.GRAY + "water, the last player wins!");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&b&lSkywars":
                    lore.add(CC.GRAY + "Loot the chests and fight");
                    lore.add(CC.GRAY + "your way through victory,");
                    lore.add(CC.GRAY + "the last player wins!");
                    lore.add("");
                    lore.add("&bClick to host");
                    break;
                case "&c&lKoTH":
                    lore.add(CC.GRAY + "Capture the KoTH point");
                    lore.add(CC.GRAY + "with your team, the last");
                    lore.add(CC.GRAY + "team standing until timer wins!");
                    lore.add("");
                    lore.add("&c&lThis event is in development!");
                    break;
                case "&c&lRunner":
                    lore.add(CC.GRAY + "Run for your life and");
                    lore.add(CC.GRAY + "beat your opponents in a");
                    lore.add(CC.GRAY + "Foot Race by avoiding the water.");
                    lore.add("");
                    lore.add("&c&lThis event is in development!");
                    break;
            }
            lore.add(CC.MENU_BAR);


            return new ItemBuilder(eventType.getMaterial())
                    .name(eventType.getTitle())
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            if (!eventType.isEnabled()) {
                player.sendMessage(CC.translate("&7This event is disabled, Please contact an admin to enable this event."));
                return;
            }

                switch (eventType.getTitle()) {
                    case "&b&lBrackets":
                        if (player.hasPermission("practice.host.brackets")) {
                            BracketsHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&b&lSumo":
                        if (player.hasPermission("practice.host.sumo")) {
                            SumoHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&b&lLMS":
                        if (player.hasPermission("practice.host.lms")) {
                            LMSHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&b&lParkour":
                        if (player.hasPermission("practice.host.parkour")) {
                            ParkourHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&b&lSpleef":
                        if (player.hasPermission("practice.host.spleef")) {
                            SpleefHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&b&lSkywars":
                        if (player.hasPermission("practice.host.skywars")) {
                            SkyWarsHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider upgrading your Rank at &b&ostore.purgemc.club &7!"));
                        }
                        break;
                    case "&c&lRunner":
                    case "&c&lKoTH":
                        player.sendMessage(CC.translate("&cThis event is currently in development, please try again!"));
                        break;
                }
            }

        }
    @AllArgsConstructor
    private static class GlassButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE).name("").durability(3).build();
        }
    }
}
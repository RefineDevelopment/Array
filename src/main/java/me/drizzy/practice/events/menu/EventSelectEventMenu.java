package me.drizzy.practice.events.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.Locale;
import me.drizzy.practice.enums.EventType;
import me.drizzy.practice.events.types.gulag.command.GulagHostCommand;
import org.bukkit.Material;
import me.drizzy.practice.events.types.brackets.command.BracketsHostCommand;
import me.drizzy.practice.events.types.lms.command.LMSHostCommand;
import me.drizzy.practice.events.types.parkour.command.ParkourHostCommand;
import me.drizzy.practice.events.types.spleef.command.SpleefHostCommand;
import me.drizzy.practice.events.types.sumo.command.SumoHostCommand;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
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
        return "&cSelect an events";
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
        buttons.put(31, new SelectEventButton(EventType.LMS));
        buttons.put(13, new SelectEventButton(EventType.BRACKETS));
        buttons.put(12, new SelectEventButton(EventType.SUMO));
        buttons.put(14, new SelectEventButton(EventType.PARKOUR));
        buttons.put(32, new SelectEventButton(EventType.GULAG));
        buttons.put(30, new SelectEventButton(EventType.SPLEEF));
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectEventButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore=new ArrayList<>();
            lore.add(CC.MENU_BAR);
            switch (eventType.getTitle()) {
                case "&c&lBrackets":
                    lore.add(CC.GRAY + "Fight through rounds and");
                    lore.add(CC.GRAY + "beat your opponent in 1v1");
                    lore.add(CC.GRAY + "duels. The last player wins!");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lSumo":
                    lore.add(CC.GRAY + "Knockback everyone off the");
                    lore.add(CC.GRAY + "platform until your are");
                    lore.add(CC.GRAY + "the last player alive");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lLMS":
                    lore.add(CC.GRAY + "Fight for your life");
                    lore.add(CC.GRAY + "and kill everyone to");
                    lore.add(CC.GRAY + "be the last man standing");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lParkour":
                    lore.add(CC.GRAY + "Make your way through the");
                    lore.add(CC.GRAY + "course and beat the others!");
                    lore.add(CC.GRAY + "The player to reach the goal wins");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lSpleef":
                    lore.add(CC.GRAY + "Break the snow blocks");
                    lore.add(CC.GRAY + "and avoid falling into");
                    lore.add(CC.GRAY + "water, the last player wins!");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lGulag":
                    lore.add(CC.GRAY + "Fight for your life and");
                    lore.add(CC.GRAY + "beat your opponent in");
                    lore.add(CC.GRAY + "1v1 Duels with guns!");
                    lore.add("");
                    lore.add("&cClick to host...");
                    break;
                case "&c&lKoTH":
                    lore.add(CC.GRAY + "Capture the koth point");
                    lore.add(CC.GRAY + "with your team, the last");
                    lore.add(CC.GRAY + "team standing until timer wins!");
                    lore.add("");
                    lore.add("&c&lThis event is in development!");
                    break;
                case "&c&lOITC":
                    lore.add(CC.GRAY + "Run for your life and");
                    lore.add(CC.GRAY + "beat your opponents in a");
                    lore.add(CC.GRAY + "FFA with a One Hit Bow!");
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
                player.sendMessage(CC.translate("&7This event is disabled, Please contact an Administrator to enable this events."));
                return;
            }

            switch (eventType.getTitle()) {
                case "&c&lBrackets":
                    if (player.hasPermission("array.host.brackets")) {
                        BracketsHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lSumo":
                    if (player.hasPermission("array.host.sumo")) {
                        SumoHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lLMS":
                    if (player.hasPermission("array.host.lms")) {
                        LMSHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lParkour":
                    if (player.hasPermission("array.host.parkour")) {
                        ParkourHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lSpleef":
                    if (player.hasPermission("array.host.spleef")) {
                        SpleefHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lGulag":
                    if (player.hasPermission("array.host.gulag")) {
                        GulagHostCommand.execute(player);
                    } else {
                        Locale.EVENT_NO_PERMISSION.toList().forEach(player::sendMessage);
                    }
                    break;
                case "&c&lRunner":
                    player.sendMessage(CC.translate("&cThis events is currently in development, please try again!"));
                    break;
            }
        }
    }

    @AllArgsConstructor
    private static class GlassButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE).name("").durability(8).build();
        }
    }
}
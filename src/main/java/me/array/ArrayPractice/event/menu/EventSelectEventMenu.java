package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsHostCommand;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.lms.command.LMSHostCommand;
import me.array.ArrayPractice.event.impl.lms.command.LMSJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourHostCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsHostCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefHostCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoHostCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
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
        buttons.put(3, new SelectEventButton(EventType.LMS));
        buttons.put(2, new SelectEventButton(EventType.BRACKETS));
        buttons.put(1, new SelectEventButton(EventType.SUMO));
        buttons.put(7, new SelectEventButton(EventType.PARKOUR));
        buttons.put(6, new SelectEventButton(EventType.SKYWARS));
        buttons.put(5, new SelectEventButton(EventType.SPLEEF));
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
                case "Brackets":
                    lore.add(CC.WHITE + "A Mini-Tournament event for other");
                    lore.add(CC.WHITE + "kits like Combo and BuildUHC etc.");
                    break;
                case "Sumo":
                    lore.add(CC.WHITE + "One by one players fight in a sumo arena");
                    lore.add(CC.WHITE + "This is a fun tournament to host casually.");
                    break;
                case "LMS":
                    lore.add(CC.WHITE + "Unleash all participants in a FFA");
                    lore.add(CC.WHITE + "Match, The last player remaining wins!");
                    break;
                case "Parkour":
                    lore.add(CC.WHITE + "Compete other players in a parkour arena,");
                    lore.add(CC.WHITE + "The First player to reach the end wins!");
                    break;
                case "Spleef":
                    lore.add(CC.WHITE + "Compete other players in a spleef arena,");
                    lore.add(CC.WHITE + "The last player remaining wins!");
                    break;
                case "Skywars":
                    lore.add(CC.WHITE + "Compete other players in a Skywars Map with");
                    lore.add(CC.WHITE + "OP Loot, The last player remaining wins!");
                    break;
            }
            lore.add("");
            lore.add("&7(Left-Click to host)");
            lore.add("&7(Right-Click to join)");
            lore.add(CC.MENU_BAR);


            return new ItemBuilder(eventType.getMaterial())
                    .name("&b" + eventType.getTitle())
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            if (clickType.isRightClick()) {
                switch (eventType.getTitle()) {
                    case "Brackets":
                        BracketsJoinCommand.execute(player);
                        break;
                    case "Sumo":
                        SumoJoinCommand.execute(player);
                        break;
                    case "LMS":
                        LMSJoinCommand.execute(player);
                        break;
                    case "Parkour":
                        ParkourJoinCommand.execute(player);
                        break;
                    case "Spleef":
                        SpleefJoinCommand.execute(player);
                        break;
                    case "Skywars":
                        SkyWarsJoinCommand.execute(player);
                }
            } else if (clickType.isLeftClick()) {
                switch (eventType.getTitle()) {
                    case "Brackets":
                        if (player.hasPermission("practice.host")) {
                            BracketsHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                    case "Sumo":
                        if (player.hasPermission("practice.host+")) {
                            SumoHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                    case "LMS":
                        if (player.hasPermission("practice.host")) {
                            LMSHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                    case "Parkour":
                        if (player.hasPermission("practice.host+")) {
                            ParkourHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                    case "Spleef":
                        if (player.hasPermission("practice.host")) {
                            SpleefHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                    case "Skywars":
                        if (player.hasPermission("practice.host+")) {
                            SkyWarsHostCommand.execute(player);
                        } else {
                            player.sendMessage(CC.translate("&7You do not have permission to execute this command."));
                            player.sendMessage(CC.translate("&7&oPlease consider buying a Rank at &b&ostore.resolve.rip &7!"));
                        }
                        break;
                }
            }

        }
    }
}
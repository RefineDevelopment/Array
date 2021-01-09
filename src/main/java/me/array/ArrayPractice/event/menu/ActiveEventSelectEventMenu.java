package me.array.ArrayPractice.event.menu;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.lms.command.LMSJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveEventSelectEventMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&7Select an active tournament";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for ( EventType eventType : EventType.values()) {
            if (eventType.getTitle().equals("FFA")) {
                if (Practice.getInstance().getLMSManager().getActiveLMS() != null && Practice.getInstance().getLMSManager().getActiveLMS().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.LMS));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Brackets")) {
                if (Practice.getInstance().getBracketsManager().getActiveBrackets() != null && Practice.getInstance().getBracketsManager().getActiveBrackets().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.BRACKETS));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Sumo")) {
                if (Practice.getInstance().getSumoManager().getActiveSumo() != null && Practice.getInstance().getSumoManager().getActiveSumo().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SUMO));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Parkour")) {
                if (Practice.getInstance().getParkourManager().getActiveParkour() != null && Practice.getInstance().getParkourManager().getActiveParkour().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.PARKOUR));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Spleef")) {
                if (Practice.getInstance().getSpleefManager().getActiveSpleef() != null && Practice.getInstance().getSpleefManager().getActiveSpleef().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SPLEEF));
                    i++;
                }
            }
            if (eventType.getTitle().equals("Skywars")) {
                if (Practice.getInstance().getSkyWarsManager().getActiveSkyWars() != null && Practice.getInstance().getSkyWarsManager().getActiveSkyWars().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SKYWARS));
                    i++;
                }
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectEventButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();

            switch (eventType.getTitle()) {
                case "Brackets":
                    lore=Practice.getInstance().getBracketsManager().getActiveBrackets().getLore();
                    break;
                case "Sumo":
                    lore=Practice.getInstance().getSumoManager().getActiveSumo().getLore();
                    break;
                case "LMS":
                    lore=Practice.getInstance().getLMSManager().getActiveLMS().getLore();
                    break;
                case "Parkour":
                    lore=Practice.getInstance().getParkourManager().getActiveParkour().getLore();
                    break;
                case "Spleef":
                    lore=Practice.getInstance().getSpleefManager().getActiveSpleef().getLore();
                    break;
                case "Skywars":
                    lore=Practice.getInstance().getSkyWarsManager().getActiveSkyWars().getLore();
                    break;
            }

            lore.add(CC.MENU_BAR);
            lore.add("&7(Left-Click to join)");
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
                    break;
            }
        }

    }

}

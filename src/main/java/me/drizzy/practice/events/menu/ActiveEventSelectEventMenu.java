package me.drizzy.practice.events.menu;

import me.drizzy.practice.Array;
import me.drizzy.practice.enums.EventType;
import me.drizzy.practice.events.types.brackets.command.BracketsJoinCommand;
import me.drizzy.practice.events.types.gulag.command.GulagJoinCommand;
import me.drizzy.practice.events.types.lms.command.LMSJoinCommand;
import me.drizzy.practice.events.types.parkour.command.ParkourJoinCommand;
import me.drizzy.practice.events.types.spleef.command.SpleefJoinCommand;
import me.drizzy.practice.events.types.sumo.command.SumoJoinCommand;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
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
        return "&7Select an active Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for ( EventType eventType : EventType.values()) {
            if (eventType.getTitle().equals("&c&lLMS")) {
                if (Array.getInstance().getLMSManager().getActiveLMS() != null && Array.getInstance().getLMSManager().getActiveLMS().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.LMS));
                    i++;
                }
            }
            if (eventType.getTitle().equals("&c&lBrackets")) {
                if (Array.getInstance().getBracketsManager().getActiveBrackets() != null && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.BRACKETS));
                    i++;
                }
            }
            if (eventType.getTitle().equals("&c&lSumo")) {
                if (Array.getInstance().getSumoManager().getActiveSumo() != null && Array.getInstance().getSumoManager().getActiveSumo().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SUMO));
                    i++;
                }
            }
            if (eventType.getTitle().equals("&c&lParkour")) {
                if (Array.getInstance().getParkourManager().getActiveParkour() != null && Array.getInstance().getParkourManager().getActiveParkour().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.PARKOUR));
                    i++;
                }
            }
            if (eventType.getTitle().equals("&c&lSpleef")) {
                if (Array.getInstance().getSpleefManager().getActiveSpleef() != null && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.SPLEEF));
                    i++;
                }
            }
            if (eventType.getTitle().equals("&c&lGulag")) {
                if (Array.getInstance().getGulagManager().getActiveGulag() != null && Array.getInstance().getGulagManager().getActiveGulag().isWaiting()) {
                    buttons.put(i, new SelectEventButton(EventType.GULAG));
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
                case "&c&lBrackets":
                    lore=Array.getInstance().getBracketsManager().getActiveBrackets().getLore();
                    break;
                case "&c&lSumo":
                    lore=Array.getInstance().getSumoManager().getActiveSumo().getLore();
                    break;
                case "&c&lLMS":
                    lore=Array.getInstance().getLMSManager().getActiveLMS().getLore();
                    break;
                case "&c&lParkour":
                    lore=Array.getInstance().getParkourManager().getActiveParkour().getLore();
                    break;
                case "&c&lSpleef":
                    lore=Array.getInstance().getSpleefManager().getActiveSpleef().getLore();
                    break;
                case "&c&lGulag":
                    lore=Array.getInstance().getGulagManager().getActiveGulag().getLore();
                    break;
            }
            lore.add("&cClick to join");
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
            switch (eventType.getTitle()) {
                case "&c&lBrackets":
                    BracketsJoinCommand.execute(player);
                    break;
                case "&c&lSumo":
                    SumoJoinCommand.execute(player);
                    break;
                case "&c&lLMS":
                    LMSJoinCommand.execute(player);
                    break;
                case "&c&lParkour":
                    ParkourJoinCommand.execute(player);
                    break;
                case "&c&lSpleef":
                    SpleefJoinCommand.execute(player);
                    break;
                case "&c&lGulag":
                    GulagJoinCommand.execute(player);
                    break;
            }
        }

    }

}

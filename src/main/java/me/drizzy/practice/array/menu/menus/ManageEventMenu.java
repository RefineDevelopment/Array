package me.drizzy.practice.array.menu.menus;

import lombok.AllArgsConstructor;
import me.drizzy.practice.event.EventType;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.sumo.Sumo;
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

public class ManageEventMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&bYou are editing Events";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(2, new EventsButton(EventType.LMS));
        buttons.put(3, new EventsButton(EventType.BRACKETS));
        buttons.put(4, new EventsButton(EventType.SUMO));
        buttons.put(5, new EventsButton(EventType.PARKOUR));
        buttons.put(11, new EventsButton(EventType.SKYWARS));
        buttons.put(13, new EventsButton(EventType.SPLEEF));
        return buttons;
    }

    @AllArgsConstructor
    private static class EventsButton extends Button {

        private final EventType eventType;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore=new ArrayList<>();
            lore.add(CC.MENU_BAR);
            switch (eventType.getTitle()) {
                case "&b&lBrackets":
                    lore.add(CC.GRAY + "Fight through rounds and");
                    lore.add(CC.GRAY + "beat your opponent in 1v1");
                    lore.add(CC.GRAY + "duels. The last player wins!");
                    lore.add("");
                    lore.add((Brackets.isEnabled() ? " &cClick to disable Brackets" : " &aClick to enable Brackets"));
                    break;
                case "&b&lSumo":
                    lore.add(CC.GRAY + "Knockback everyone off the");
                    lore.add(CC.GRAY + "platform until your are");
                    lore.add(CC.GRAY + "the last player alive");
                    lore.add("");
                    lore.add((Sumo.isEnabled() ? " &cClick to disable Sumo" : " &aClick to enable Sumo"));
                    break;
                case "&b&lLMS":
                    lore.add(CC.GRAY + "Fight for your life");
                    lore.add(CC.GRAY + "and kill everyone to");
                    lore.add(CC.GRAY + "be the last man standing");
                    lore.add("");
                    lore.add((LMS.isEnabled() ? " &cClick to disable LMS" : " &aClick to enable LMS"));
                    break;
                case "&b&lParkour":
                    lore.add(CC.GRAY + "Make your way through the");
                    lore.add(CC.GRAY + "course and beat the others!");
                    lore.add(CC.GRAY + "The player to reach the goal wins");
                    lore.add("");
                    lore.add((Parkour.isEnabled() ? " &cClick to disable Parkour" : " &aClick to enable Parkour"));
                    break;
                case "&b&lSpleef":
                    lore.add(CC.GRAY + "Break the snow blocks");
                    lore.add(CC.GRAY + "and avoid falling into");
                    lore.add(CC.GRAY + "water, the last player wins!");
                    lore.add("");
                    lore.add((Spleef.isEnabled() ? " &cClick to disable Spleef" : " &aClick to enable Spleef"));
                    break;
                case "&b&lSkywars":
                    lore.add(CC.GRAY + "Loot the chests and fight");
                    lore.add(CC.GRAY + "your way through victory,");
                    lore.add(CC.GRAY + "the last player wins!");
                    lore.add("");
                    lore.add((SkyWars.isEnabled() ? " &cClick to disable SkyWars" : " &aClick to enable SkyWars"));
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
            if (eventType == EventType.BRACKETS) {
                Brackets.setEnabled(!Brackets.isEnabled());
            }
            if (eventType == EventType.SPLEEF) {
                Spleef.setEnabled(!Spleef.isEnabled());
            }
            if (eventType == EventType.SKYWARS) {
                SkyWars.setEnabled(!SkyWars.isEnabled());
            }
            if (eventType == EventType.SUMO) {
                Sumo.setEnabled(!Sumo.isEnabled());
            }
            if (eventType == EventType.LMS) {
                LMS.setEnabled(!LMS.isEnabled());
            }
            if (eventType == EventType.PARKOUR) {
                Parkour.setEnabled(!Parkour.isEnabled());
            }
            player.closeInventory();
            new ManageEventMenu().openMenu(player);
        }
    }
}


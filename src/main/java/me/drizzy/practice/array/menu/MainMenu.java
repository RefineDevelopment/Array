package me.drizzy.practice.array.menu;

import me.drizzy.practice.array.menu.buttons.KitsButton;
import me.drizzy.practice.array.menu.buttons.QueueButton;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import me.drizzy.practice.array.menu.buttons.ArenaButton;

import java.util.HashMap;
import java.util.Map;

public class MainMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&bArray Manage Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(3, new QueueButton());
        buttons.put(5, new ArenaButton());
        buttons.put(7, new KitsButton());
        return buttons;
    }



}

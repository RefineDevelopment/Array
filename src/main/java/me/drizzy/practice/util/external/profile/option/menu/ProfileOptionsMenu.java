package me.drizzy.practice.util.external.profile.option.menu;

import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import me.drizzy.practice.util.external.profile.option.event.OptionsOpenedEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProfileOptionsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&bOptions";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        OptionsOpenedEvent event = new OptionsOpenedEvent(player);
        event.call();

        if (!event.getButtons().isEmpty()) {
            for (ProfileOptionButton button : event.getButtons()) {
                buttons.put(buttons.size(), button);
            }
        }

        return buttons;
    }

}

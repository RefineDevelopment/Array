package xyz.refinedev.practice.profile.menu;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.menu.buttons.WorldButton;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

public class WorldsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&aWorlds");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( World world : Bukkit.getWorlds() ) {
            buttons.put(buttons.size(), new WorldButton(world));
        }
        return buttons;
    }
}

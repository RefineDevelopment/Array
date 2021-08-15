package me.drizzy.practice.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 4/15/2021
 */
public class MenuUpdateTask implements Runnable {

    @Override
    public void run() {
        Menu.currentlyOpenedMenus.forEach((key, value) -> {
            final Player player = Bukkit.getPlayer(key);

            if (player != null) {
                if (value.isAutoUpdate()) {
                    value.openMenu(player);
                }
            }
        });
    }

}
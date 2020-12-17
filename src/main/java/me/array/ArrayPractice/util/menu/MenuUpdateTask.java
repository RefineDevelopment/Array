package me.array.ArrayPractice.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

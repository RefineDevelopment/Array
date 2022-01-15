package xyz.refinedev.practice.util.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/15/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MenuUpdateTask implements Runnable {

    private final Array plugin;

    @Override
    public void run() {
        plugin.getMenuHandler().getOpenedMenus().forEach((key, value) -> {
            final Player player = Bukkit.getPlayer(key);

            if (player != null) {
                if (value.isAutoUpdate()) {
                    value.openMenu(plugin, player);
                }
            }
        });
    }

}
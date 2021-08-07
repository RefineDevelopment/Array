package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.config.MenuConfig;
import xyz.refinedev.practice.util.menu.custom.MenuData;

import java.io.File;
import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MenuManager {

    private final List<MenuData> menuData = new ArrayList<>();
    private final Array plugin;

    public void init() {
        File file = new File(plugin.getDataFolder() + "/menus");
        File[] files = file.getParentFile().listFiles();
        if (files == null) return;

        for ( File menuFile :  files) {
            MenuConfig menuConfig = new MenuConfig(menuFile.getName(), plugin);
            MenuData menu = new MenuData();

            menu.setName(menuFile.getName());

            menuData.add(menu);
        }
    }

    public void generateDefaultMenus() {
        String[] configs = {"settings", "party_events"};

        for ( String config : configs )
            new BasicConfigurationFile(plugin, config);
    }

    public MenuData getByName(String name) {
        return menuData.stream().filter(menu -> menu.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

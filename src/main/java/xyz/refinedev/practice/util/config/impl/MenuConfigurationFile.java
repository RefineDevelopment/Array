package xyz.refinedev.practice.util.config.impl;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

public class MenuConfigurationFile extends FoldersConfigurationFile {

    public MenuConfigurationFile(String name, JavaPlugin plugin) {
        super(plugin, "menu", name , false, false);

    }
}
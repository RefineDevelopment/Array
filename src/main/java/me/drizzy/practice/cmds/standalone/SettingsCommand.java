package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.profile.settings.SettingsMenu;
import me.drizzy.practice.util.command.annotation.Command;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

public class SettingsCommand {

    @Command(name = "", desc = "Open Array Settings Menu")
    public void settings(Player player) {
        new SettingsMenu().openMenu(player);
    }
}

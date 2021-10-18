package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.settings.menu.ProfileSettingsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

public class SettingsCommand {

    @Command(name = "", desc = "Open Array Settings Menu")
    public void settings(@Sender Player player) {
        new ProfileSettingsMenu().openMenu(player);
    }
}

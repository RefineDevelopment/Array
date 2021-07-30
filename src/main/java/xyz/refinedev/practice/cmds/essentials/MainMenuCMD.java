package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.menu.MainMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/29/2021
 * Project: Array
 */

public class MainMenuCMD {

    @Command(name = "", desc = "Open Main Menu")
    public void onCMD(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (!profile.isInLobby()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        new MainMenu().openMenu(player);
    }
}

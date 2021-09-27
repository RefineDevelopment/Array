package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
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

@RequiredArgsConstructor
public class MainMenuCMD {

    private final Array plugin;

    @Command(name = "", desc = "Open Main Menu")
    public void onCMD(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        if (!profile.isInLobby()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        new MainMenu().openMenu(player);
    }
}

package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.kiteditor.menu.KitEditorSelectKitMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/28/2021
 * Project: Array
 */

public class KitEditorCMD {

    @Command(name = "", desc = "Open the kit editor via command")
    public void kitEditor(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);

        if (profile.isInLobby() || profile.isInQueue()) {
            new KitEditorSelectKitMenu().openMenu(player);
        } else {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
    }
}

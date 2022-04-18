package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.kiteditor.menu.KitEditorSelectKitMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/28/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class KitEditorCMD {

    private final Array plugin;

    @Command(name = "", desc = "Open the kit editor via command")
    public void kitEditor(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfile(player);

        if (!profile.isInLobby() || profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        Menu menu = new KitEditorSelectKitMenu();
        plugin.getMenuHandler().openMenu(menu, player);
    }
}

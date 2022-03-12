package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.menu.MatchSpectateMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.MenuHandler;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SpectateMenuCommand {

    private final Array plugin;

    @Command(name = "", desc = "Open spectate menu")
    public void spectateMenu(@Sender Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        MenuHandler menuHandler = plugin.getMenuHandler();
        Profile profile = profileManager.getProfile(player);

        if (profile.getMatch() == null) {
            player.sendMessage(Locale.ERROR_SELF_NOT_IN_MATCH.toString());
            return;
        }
        Menu menu = new MatchSpectateMenu(menuHandler.getConfigByName("general"), profile.getMatch());
        menuHandler.openMenu(menu, player);
    }
}

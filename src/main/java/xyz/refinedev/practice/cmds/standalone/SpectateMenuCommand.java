package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.menu.MatchSpectateMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

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
        Profile profile = plugin.getProfileManager().getProfile(player);
        if (profile.getMatch() == null) {
            player.sendMessage(Locale.MATCH_NOT_IN_SELF.toString());
            return;
        }
        MatchSpectateMenu menu = new MatchSpectateMenu(plugin, profile.getMatch());
        menu.openMenu(player);
    }
}

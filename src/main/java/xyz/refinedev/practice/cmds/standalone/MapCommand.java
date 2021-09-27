package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MapCommand {

    private final Array plugin;

    @Command(name = "", desc = "View the arena you are playing on")
    public void mapCommand(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        Match match = profile.getMatch();

        if (profile.isInFight()) {
            player.sendMessage(CC.translate("&fMap: &a" + match.getArena().getDisplayName()));
        } else {
            player.sendMessage(Locale.ERROR_NOTMATCH.toString());
        }
    }
}

package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class MapCommand {

    @Command(name = "", desc = "View the arena you are playing on")
    public void mapCommand(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        Match match = profile.getMatch();

        if (profile.isInFight()) {
            player.sendMessage(CC.translate("&fMap: &a" + match.getArena().getDisplayName()));
        } else {
            player.sendMessage(Locale.ERROR_NOTMATCH.toString());
        }
    }
}

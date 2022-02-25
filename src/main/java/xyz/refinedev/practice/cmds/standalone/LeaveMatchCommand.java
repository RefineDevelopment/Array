package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Require;
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
public class LeaveMatchCommand {

    private final Array plugin;

    @Command(name = "", desc = "Cancel your on-going match")
    @Require("array.profile.forfeit")
    public void forfeitMatch(@Sender Player player, @OptArg() Player target) {

        Profile profile = plugin.getProfileManager().getProfile(target == null ? player.getUniqueId() : target.getUniqueId());
        if (!profile.isInFight()) {
            player.sendMessage(target == null ? Locale.ERROR_NOTMATCH.toString() : Locale.ERROR_TARGET_NOTMATCH.toString());
            return;
        }

        Match match = profile.getMatch();
        plugin.getMatchManager().handleDeath(match, player);
    }
}

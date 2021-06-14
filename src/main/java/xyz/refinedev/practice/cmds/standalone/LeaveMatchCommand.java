package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class LeaveMatchCommand {

    @Command(name = "", desc = "Cancel your on-going match")
    @Require("array.profile.forfeit")
    public void forfeitMatch(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInFight()) {
            Match match = profile.getMatch();
            match.handleDeath(player);
        } else {
            player.sendMessage(Locale.ERROR_NOTMATCH.toString());
        }

    }
}

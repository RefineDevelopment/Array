package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.Locale;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Require;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
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

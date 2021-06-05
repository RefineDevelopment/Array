package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.Locale;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
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

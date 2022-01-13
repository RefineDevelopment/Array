package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
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
public class AbortMatchCommand {

    private final Array plugin;

    @Command(name = "", usage = "<target>", desc = "Cancel a player's Match")
    @Require("array.staff.match")
    public void cancelMatch(@Sender CommandSender player, Player target) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(target);
        if (!profile.isInFight()) {
            player.sendMessage(Locale.MATCH_NOT_IN.toString());
            return;
        }
        Match match = profile.getMatch();
        this.plugin.getMatchManager().end(match);
        player.sendMessage(CC.translate("&7Successfully cancelled &c" + profile.getName() + "'s &7Match!"));
    }

}

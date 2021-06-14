package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class AbortMatchCommand {

    @Command(name = "", usage = "<target>", desc = "Cancel a player's Match")
    @Require("array.staff.match")
    public void cancelMatch(@Sender CommandSender player, Player target) {
        Profile profile = Profile.getByPlayer(target);
        if (!profile.isInFight()) {
            player.sendMessage(CC.translate("&7That player is not in a fight!"));
            return;
        }
        profile.getMatch().end();
        player.sendMessage(CC.translate("&7Successfully cancelled &c" + profile.getName() + "'s &7Match!"));
    }

}

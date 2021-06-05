package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Require;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
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

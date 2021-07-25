package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueProfile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/4/2021
 * Project: Array
 */

public class LeaveQueueCMD {

    @Command(name = "", desc = "Leave your current queue.")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        QueueProfile queueProfile = profile.getQueueProfile();

        if (!profile.isInQueue()) {
            player.sendMessage(CC.translate("&7You are not part of any queue."));
            return;
        }

        profile.getQueue().removePlayer(queueProfile);
    }
}

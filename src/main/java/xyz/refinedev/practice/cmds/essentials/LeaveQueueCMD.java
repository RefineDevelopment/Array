package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueProfile;
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

@RequiredArgsConstructor
public class LeaveQueueCMD {

    private final Array plugin;

    @Command(name = "", desc = "Leave your current queue.")
    public void leave(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        Queue queue = plugin.getQueueManager().getByUUID(player.getUniqueId());
        QueueProfile queueProfile = plugin.getQueueManager().getProfileByUUID(player.getUniqueId());

        if (!profile.isInQueue()) {
            player.sendMessage(Locale.ERROR_NOT_IN_QUEUE.toString());
            return;
        }

        plugin.getQueueManager().removePlayer(queue, queueProfile);
    }
}

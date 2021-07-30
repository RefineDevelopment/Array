package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.queue.menu.QueueSelectKitMenu;
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

public class ClanQueueCMD {

    @Command(name = "", desc = "Open Clan Queue Menu")
    public void queue(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (!profile.hasClan()) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }
        if (!profile.isBusy()) {
            new QueueSelectKitMenu(QueueType.CLAN).openMenu(player);
        }
    }
}

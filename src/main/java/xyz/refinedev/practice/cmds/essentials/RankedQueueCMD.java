package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.entity.Player;
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

public class RankedQueueCMD {

    @Command(name = "", desc = "Open ranked queue menu")
    public void queue(@Sender Player player) {
        new QueueSelectKitMenu(QueueType.RANKED).openMenu(player);
    }
}

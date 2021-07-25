package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.profile.statistics.menu.StatsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Flag;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

public class StatsCommand {

    @Command(name = "", desc = "View Statistics of your profile")
    public void stats(@Sender Player player, @OptArg() Player target) {
        if (target == null) {
            new StatsMenu(player).openMenu(player);
            return;
        }
        new StatsMenu(target).openMenu(player);
    }
}

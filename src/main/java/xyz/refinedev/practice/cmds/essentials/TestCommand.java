package xyz.refinedev.practice.cmds.essentials;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 2/25/2022
 * Project: Array
 */

public class TestCommand {

    @Command(name = "", desc = "testing")
    public void test(@Sender CommandSender player) {
        Array.getInstance().getProfileManager().getProfiles().values().forEach(profile -> {
            System.out.println(profile.getName());
        });
    }
}

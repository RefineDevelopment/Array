package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
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
 * Created at 5/31/2021
 * Project: Array
 */

public class FlyCommand {

    @Command(name = "", desc = "Allow Donators to Fly")
    @Require("array.profile.fly")
    public void fly(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInLobby() || profile.isInQueue()) {
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.updateInventory();
                player.sendMessage(CC.GRAY + "You are no longer flying.");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.updateInventory();
                player.sendMessage(CC.GREEN + "You are now flying.");
            }
        } else {
            player.sendMessage(CC.RED + "You cannot fly right now.");
        }
    }
}

package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class RematchCommand {

    @Command(name = "", desc = "Rematch a Player")
    public void rematch(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "You do not have anyone to rematch.");
            return;
        }

        profile.checkForHotbarUpdate();

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "That player is no longer available.");
            return;
        }

        RematchProcedure rematchProcedure = profile.getRematchData();

        if (rematchProcedure.isReceive()) {
            rematchProcedure.accept();
        } else {
            if (rematchProcedure.isSent()) {
                player.sendMessage(CC.RED + "You have already sent a rematch request to that player.");
                return;
            }
            rematchProcedure.request();
        }
    }
}

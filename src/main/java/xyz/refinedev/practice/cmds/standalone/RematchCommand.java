package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

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
            player.sendMessage(Locale.ERROR_NOREMATCH.toString());
            return;
        }
        profile.checkForHotbarUpdate();

        RematchProcedure rematchProcedure = profile.getRematchData();

        if (rematchProcedure.isReceive()) {
            rematchProcedure.accept();
        } else {
            if (rematchProcedure.isSent()) {
                player.sendMessage(Locale.ERROR_EXPIREREMATCH.toString());
                return;
            }
            rematchProcedure.request();
        }
    }
}

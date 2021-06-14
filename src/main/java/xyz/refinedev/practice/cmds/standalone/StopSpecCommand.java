package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
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

public class StopSpecCommand {

    @Command(name = "", desc = "Stop spectating")
    public void stopSpec(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isSpectating()) {
            profile.setSpectating(null);

            if (profile.getMatch() != null) {
                profile.getMatch().removeSpectator(player);
            } else if (profile.getSumo() != null) {
                profile.getSumo().removeSpectator(player);
            } else if (profile.getBrackets() != null) {
                profile.getBrackets().removeSpectator(player);
            } else if (profile.getLms() != null) {
                profile.getLms().removeSpectator(player);
            } else if (profile.getParkour() != null) {
                profile.getParkour().removeSpectator(player);
            } else if (profile.getSpleef() != null) {
                profile.getSpleef().removeSpectator(player);
            }
        } else {
            player.sendMessage(Locale.ERROR_FREE.toString());
        }
    }
}

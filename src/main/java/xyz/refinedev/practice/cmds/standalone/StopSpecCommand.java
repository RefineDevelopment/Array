package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
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

public class StopSpecCommand {

    @Command(name = "", desc = "Stop spectating")
    public void stopSpec(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInFight() && !profile.getMatch().getTeamPlayer(player).isAlive()) {
            profile.getMatch().getTeamPlayer(player).setDisconnected(true);
            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);
            return;
        }

        if (profile.isSpectating()) {
            profile.setSpectating(null);
            if (profile.getMatch() != null) {
                profile.getMatch().removeSpectator(player);
            } else if (profile.isInEvent()) {
                profile.getEvent().removeSpectator(player);
            }
        } else {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
        }
    }
}

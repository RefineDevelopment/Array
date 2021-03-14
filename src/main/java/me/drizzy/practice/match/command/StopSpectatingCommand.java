package me.drizzy.practice.match.command;

import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "stopspectating")
public class StopSpectatingCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInFight() && !profile.getMatch().getTeamPlayer(player).isAlive()) {
            profile.getMatch().getTeamPlayer(player).setDisconnected(true);
            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);

        } else if (profile.isSpectating()) {

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
            player.sendMessage(CC.RED + "You are not spectating a match or event.");
        }
    }

}

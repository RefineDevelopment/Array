package me.array.ArrayPractice.match.command;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
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
			if (profile.getMatch() != null) {
				profile.getMatch().removeSpectator(player);
			} else if (profile.getSumo() != null) {
				profile.getSumo().removeSpectator(player);
			} else if (profile.getBrackets() != null) {
				profile.getBrackets().removeSpectator(player);
			} else if (profile.getFfa() != null) {
				profile.getFfa().removeSpectator(player);
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

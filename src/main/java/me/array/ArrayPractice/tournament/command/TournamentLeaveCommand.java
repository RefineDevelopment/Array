package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.TournamentManager;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament leave")
public class TournamentLeaveCommand {

	public void execute(Player player) {
		if (TournamentManager.CURRENT_TOURNAMENT == null || TournamentManager.CURRENT_TOURNAMENT.hasStarted()) {
			player.sendMessage(ChatColor.RED + "There isn't a TournamentManager you can leave");
			return;
		}
		Party party = Profile.getByUuid(player.getUniqueId()).getParty();
		if (party == null) {
			player.sendMessage(ChatColor.RED + "You aren't currently in a TournamentManager");
			return;
		}
		if (!TournamentManager.CURRENT_TOURNAMENT.isParticipating(player)) {
			player.sendMessage(ChatColor.RED + "You aren't currently in a TournamentManager");
			return;
		}
		if (!party.isLeader(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "Only Leaders can do this");
			return;
		}
		TournamentManager.CURRENT_TOURNAMENT.leave(party);
	}
}



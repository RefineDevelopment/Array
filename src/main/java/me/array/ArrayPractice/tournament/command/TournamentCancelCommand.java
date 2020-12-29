package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.TournamentManager;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament cancel", permission = "practice.staff")
public class TournamentCancelCommand {

	public void execute(Player player) {
		if(TournamentManager.CURRENT_TOURNAMENT == null){
			player.sendMessage(ChatColor.RED + "There isn't an active Tournament right now");
			return;
		}
		if(TournamentManager.RUNNABLE != null){
			TournamentManager.RUNNABLE.cancel();
		}
		TournamentManager.CURRENT_TOURNAMENT.cancel();
		TournamentManager.CURRENT_TOURNAMENT = null;
		player.sendMessage(ChatColor.RED + "The Tournament has been cancelled");
	}
}



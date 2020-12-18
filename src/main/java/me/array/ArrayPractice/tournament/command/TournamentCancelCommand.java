package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.Tournament;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament cancel", permission = "tournament.cancel")
public class TournamentCancelCommand {

	public void execute(Player player) {
		if(Tournament.CURRENT_TOURNAMENT == null){
			player.sendMessage(ChatColor.RED + "There isn't an active Tournament right now");
			return;
		}
		if(Tournament.RUNNABLE != null){
			Tournament.RUNNABLE.cancel();
		}
		Tournament.CURRENT_TOURNAMENT.cancel();
		Tournament.CURRENT_TOURNAMENT = null;
		player.sendMessage(ChatColor.RED + "The Tournament has been cancelled");
	}
}



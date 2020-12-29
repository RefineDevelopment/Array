package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.TournamentManager;
import me.array.ArrayPractice.util.external.CC;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label ={ "tournaments", "tournament list", "tournament show"})
public class TournamentListCommand {

	public void execute(Player player) {
		if (TournamentManager.CURRENT_TOURNAMENT != null) {
			TournamentManager tournament = TournamentManager.CURRENT_TOURNAMENT;
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.BLUE).append("Tournament ").append(tournament.getTeamCount() + "v" + tournament.getTeamCount()).append("'s matches:");
			builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
			builder.append(CC.AQUA + "Ladder: ").append(ChatColor.WHITE + tournament.getLadder().getName()).append("\n");
			builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
			for ( TournamentManager.TournamentMatch match : tournament.getTournamentMatches()) {
				String teamANames = match.getTeamA().getLeader().getPlayer().getName();
				String teamBNames = match.getTeamB().getLeader().getPlayer().getName();
				builder.append(ChatColor.YELLOW + teamANames + "'s Party").append(ChatColor.WHITE + " vs. ").append(ChatColor.AQUA + teamBNames + "'s Party").append("\n");
			}
			builder.append(ChatColor.BLUE).append(" ").append(ChatColor.BLUE).append("\n");
			builder.append(CC.AQUA).append("Round: ").append(ChatColor.WHITE).append(tournament.getRound());
			builder.append("\n");
			builder.append(CC.AQUA).append("Players: ").append(ChatColor.WHITE).append(tournament.getParticipatingCount()).append("\n");
			player.sendMessage(builder.toString());
		}else{
			player.sendMessage(ChatColor.BLUE + "There aren't any active Tournaments");
		}
	}
}



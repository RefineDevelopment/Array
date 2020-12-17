package me.array.ArrayPractice.util.nametag;

import me.array.ArrayPractice.util.external.CC;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

public class NameTags {

	public static void color(Player player, Player other, ChatColor color, boolean showHealth) {
		color(player , other , color.toString() , "" , showHealth);
	}
	public static synchronized void color(Player player, Player other, String prefix , String suffix ,  boolean showHealth) {
		if (player.equals(other)) {
			return;
		}

		Team team = player.getScoreboard().getTeam(other.getName());

		if (team == null) {
			team = player.getScoreboard().registerNewTeam(other.getName());
		}
		team.setPrefix(prefix);
		team.setSuffix(suffix);

		if (!team.hasEntry(other.getName())) {
			reset(player, other);

			team.addEntry(other.getName());

			if (showHealth) {
				Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

				if (objective == null) {
					objective = player.getScoreboard().registerNewObjective("showhealth", "health");
				}

				objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
				objective.setDisplayName(CC.RED + StringEscapeUtils.unescapeJava("\u2764"));
				objective.getScore(other.getName()).setScore((int) other.getHealth());
			}
		}
	}

	public static void reset(Player player, Player other) {
		if (player != null && other != null && !player.equals(other)) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

			if (objective != null) {
				objective.unregister();
			}

			for (Team team : player.getScoreboard().getTeams()) {
				team.removeEntry(other.getName());
			}
		}
	}

}

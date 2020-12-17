package me.joeleoli.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@Getter
public class FrameBoard {

	// We assign a unique identifier (random string of ChatColor values)
	// to each board entry to: bypass the 32 char limit, using
	// a team's prefix & suffix and a team entry's display name, and to
	// track the order of entries;
	private final List<FrameBoardEntry> entries;
	private final List<String> identifiers;
	private Scoreboard scoreboard;
	private Objective objective;

	public FrameBoard(Player player) {
		this.entries = new ArrayList<>();
		this.identifiers = new ArrayList<>();

		this.setup(player);
	}

	private void setup(Player player) {
		// Register new scoreboard if needed
		if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		} else {
			this.scoreboard = player.getScoreboard();
		}

		// Setup sidebar objective
		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(Frame.getInstance().getAdapter().getTitle(player));

		// Update scoreboard
		player.setScoreboard(this.scoreboard);
	}

	public FrameBoardEntry getEntryAtPosition(int pos) {
		if (pos >= this.entries.size()) {
			return null;
		} else {
			return this.entries.get(pos);
		}
	}

	public String getUniqueIdentifier(String text) {
		String identifier = getRandomChatColor() + ChatColor.WHITE;

		while (this.identifiers.contains(identifier)) {
			identifier = identifier + getRandomChatColor() + ChatColor.WHITE;
		}

		// This is rare, but just in case, make the method recursive
		if (identifier.length() > 16) {
			return this.getUniqueIdentifier(text);
		}

		// Add our identifier to the list so there are no duplicates
		this.identifiers.add(identifier);

		return identifier;
	}

	// Gets a random ChatColor and returns the String value of it
	private static String getRandomChatColor() {
		return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)].toString();
	}

}

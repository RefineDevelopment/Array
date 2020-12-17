package me.joeleoli.frame;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@Getter
public class Frame {

	@Getter
	private static Frame instance;

	private JavaPlugin plugin;
	private FrameAdapter adapter;
	private Map<UUID, FrameBoard> boards;

	public Frame(JavaPlugin plugin, FrameAdapter adapter) {
		if (instance != null) {
			throw new RuntimeException("Frame has already been instantiated!");
		}

		instance = this;

		this.plugin = plugin;
		this.adapter = adapter;
		this.boards = new HashMap<>();

		this.setup();
	}

	private void setup() {
		this.plugin.getServer().getPluginManager().registerEvents(new FrameListener(), this.plugin);
		this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			for (Player player : this.plugin.getServer().getOnlinePlayers()) {
				final FrameBoard board = this.boards.get(player.getUniqueId());

				// This shouldn't happen, but just in case
				if (board == null) {
					continue;
				}

				final Scoreboard scoreboard = board.getScoreboard();
				final Objective objective = board.getObjective();
				final String title = ChatColor.translateAlternateColorCodes('&', this.adapter.getTitle(player));

				// Update the title if needed
				if (!objective.getDisplayName().equals(title)) {
					objective.setDisplayName(title);
				}

				final List<String> newLines = this.adapter.getLines(player);

				// Allow adapter to return null/empty list to display nothing
				if (newLines == null || newLines.isEmpty()) {
					board.getEntries().forEach(FrameBoardEntry::remove);
					board.getEntries().clear();
				} else {
					// Reverse the lines because scoreboard scores are in descending order
					Collections.reverse(newLines);

					// Remove excessive amount of board entries
					if (board.getEntries().size() > newLines.size()) {
						for (int i = newLines.size(); i < board.getEntries().size(); i++) {
							final FrameBoardEntry entry = board.getEntryAtPosition(i);

							if (entry != null) {
								entry.remove();
							}
						}
					}

					// Update existing entries / add new entries
					for (int i = 0; i < newLines.size(); i++) {
						FrameBoardEntry entry = board.getEntryAtPosition(i);

						// Translate any colors
						final String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));

						// If the entry is null, just create a new one.
						// Creating a new FrameBoardEntry instance will add
						// itself to the provided board's entries list.
						if (entry == null) {
							entry = new FrameBoardEntry(board, line);
						}

						// Update text, setup the team, and update the display values
						entry.setText(line);
						entry.setup();
						entry.send(i);
					}
				}

				player.setScoreboard(scoreboard);
			}
		}, 20L, 20L);
	}

}

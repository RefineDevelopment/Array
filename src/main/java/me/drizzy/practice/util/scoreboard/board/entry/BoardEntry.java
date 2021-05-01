package me.blazingtide.pistol.board.entry;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Data
public class BoardEntry {

    private final String id;
    private final Team team;

    private String fullString;

    private String prefix;
    private String suffix = "";

    public static BoardEntry of(Scoreboard scoreboard, String id) {
        final Team team = scoreboard.getTeam(id) != null ? scoreboard.getTeam(id) : scoreboard.registerNewTeam(id);
        team.addEntry(id);

        return new BoardEntry(id, team);
    }

    public void update(String line, boolean restrict) {
        //Don't need to update the line if they're both the same.
        if (line.equals(fullString)) {
            return;
        }

        if (restrict) {
            prefix = line.substring(0, Math.min(16, line.length()));

            if (prefix.length() != line.length()) {
                String lastColors = ChatColor.getLastColors(prefix);

                suffix = lastColors + line.substring(16);
                suffix = suffix.substring(0, Math.min(16, suffix.length()));
            } else if (!suffix.isEmpty()) {
                suffix = "";
            }
        } else {
            prefix = line;
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        fullString = line;
    }

}
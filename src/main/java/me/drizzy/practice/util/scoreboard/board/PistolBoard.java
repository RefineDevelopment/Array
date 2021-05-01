package me.blazingtide.pistol.board;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.blazingtide.pistol.Pistol;
import me.blazingtide.pistol.adapter.impl.KohiPistolAdapter;
import me.blazingtide.pistol.adapter.impl.NegativePistolAdapter;
import me.blazingtide.pistol.board.entry.BoardEntry;
import me.blazingtide.pistol.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class PistolBoard {

    private final Map<String, BoardEntry> entries = Maps.newHashMap();

    private final Pistol pistol;
    private final Player player;
    private final Scoreboard scoreboard;

    private Objective objective;

    public PistolBoard(Pistol pistol, Player player) {
        this.pistol = pistol;
        this.player = player;
        this.scoreboard = player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard() ? player.getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void init() {
        objective = scoreboard.registerNewObjective(player.getName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        update();
    }

    private void buildEntries() {
        //Reset the scores every time you build entries
        final List<String> lines = pistol.getAdapter().getLines(player);
        final List<String> entriesList = new ArrayList<>(entries.keySet());

        if (lines == null || lines.isEmpty()) {
            entriesList.forEach(str -> {
                scoreboard.resetScores(str);
                entries.get(str).getTeam().unregister();
            });
            entries.clear();
            return;
        }

        if (!(pistol.getAdapter() instanceof NegativePistolAdapter)) {
            Collections.reverse(lines); //have to reverse it since minecraft's scoreboard scores are in ascending order.
        }

        //Remove all lines that are unneeded
        if (lines.size() < entries.size()) {
            for (int i = lines.size(); i < entriesList.size(); i++) {
                final String str = entriesList.get(i);

                scoreboard.resetScores(str);
                entries.get(str).getTeam().unregister();
                entries.remove(str);
            }
        }

        //MC limits to 15 lines per scoreboard so we only want to do the first 15 lines
        int kohi = 15; //Kohi counter
        for (int i = 0; i < Math.min(lines.size(), 16); i++) {
            final String line = lines.get(i);
            final BoardEntry entry = entriesList.size() > i ? entries.get(entriesList.get(i)) : BoardEntry.of(scoreboard, findId(""));
            final Score score = objective.getScore(entry.getId());

            entry.update(ColorUtil.translate(line), pistol.getAdapter().restrictLines());
            score.setScore(pistol.getAdapter() instanceof NegativePistolAdapter ? i * -1 : pistol.getAdapter() instanceof KohiPistolAdapter ? kohi : i);

            entries.putIfAbsent(entry.getId(), entry);
            kohi--;
        }
    }

    /**
     * Creates a unique ID from chatcolor every time it's needed.
     *
     * @param start the start of the code.
     * @return the unique Id
     */
    private String findId(String start) {
        start += start + ChatColor.values()[ThreadLocalRandom.current().nextInt(0, ChatColor.values().length)];

        if (entries.containsKey(start) || scoreboard.getTeam(start) != null) {
            return findId(start);
        }

        return start;
    }

    public void update() {
        String title = pistol.getAdapter().getTitle(player);

        if (title == null) {
            title = "";
        }

        objective.setDisplayName(ColorUtil.translate(title));
        buildEntries();
        player.setScoreboard(scoreboard);
    }
}
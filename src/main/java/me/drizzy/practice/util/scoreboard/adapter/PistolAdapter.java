package me.blazingtide.pistol.adapter;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Main adapter class for determining how the library builds
 * the scoreboard.
 */
public interface PistolAdapter {

    /**
     * Determines what the title for the scoreboard should be.
     *
     * @param player the player who needs a scoreboard title
     * @return the title for the scoreboard
     */
    String getTitle(Player player);

    /**
     * Determines what the scoreboard lines for a player should be.
     *
     * @param player the player who needs the scoreboard lines
     * @return the lines for the scoreboard
     */
    List<String> getLines(Player player);

    /**
     * Restricts scoreboard lines to be max 32 chars
     *
     * @return
     */
    default boolean restrictLines() {
        return true;
    }

}

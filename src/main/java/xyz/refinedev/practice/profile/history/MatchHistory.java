package xyz.refinedev.practice.profile.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.MatchSnapshot;

import java.util.Date;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class MatchHistory {

    private final Array plugin;

    private final Date date;
    private final MatchSnapshot playerSnapshot;
    private final MatchSnapshot opponentSnapshot;

    private final Kit kit;
    private final boolean won;
    private final boolean ranked;

    private final int winnerChangedELO;
    private final int looserChangedELO;

    /**
     * Generate a match history using a {@link Document}
     *
     * @param document The match history document from mongo
     */
    public MatchHistory(Array plugin, Document document) {
        this.plugin = plugin;
        this.date = document.getDate("date");
        this.playerSnapshot = Array.GSON.fromJson(document.getString("player"), MatchSnapshot.class);
        this.opponentSnapshot = Array.GSON.fromJson(document.getString("opponent"), MatchSnapshot.class);

        this.kit = plugin.getKitManager().getByName(document.getString("kit"));
        this.won = document.getBoolean("won");
        this.ranked = document.getBoolean("ranked");

        this.winnerChangedELO = document.getInteger("winnerELO");
        this.looserChangedELO = document.getInteger("looserELO");
    }

    /**
     * Get the whole history in a {@link Document} mainly
     * for mongo and saving in mongo
     *
     * @return {@link Document}
     */
    public Document toBson() {
        return new Document()
                .append("date", date)
                .append("player", Array.GSON.toJson(playerSnapshot))
                .append("opponent", Array.GSON.toJson(opponentSnapshot))
                .append("kit", kit.getName())
                .append("won", won)
                .append("ranked", ranked)
                .append("winnerELO", winnerChangedELO)
                .append("looserELO", looserChangedELO);
    }
}

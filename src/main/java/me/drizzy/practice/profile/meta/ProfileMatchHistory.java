package me.drizzy.practice.profile.meta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.util.ConfigurationSerializableTypeAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Date;


@RequiredArgsConstructor
public class ProfileMatchHistory {
    @Getter
    private final MatchSnapshot fighter;
    @Getter
    private final MatchSnapshot opponent;

    @Getter
    private final boolean won;

    @Getter
    private final String matchType;

    @Getter
    private final String kit;

    @Getter
    private final int eloChangeWinner;

    @Getter
    private final int eloChangeLoser;

    @Getter
    private final Date createdAt;

    @Getter
    private int winnerPoints = 0;

    @Getter
    private int loserPoints = 0;

    public ProfileMatchHistory(Document document) {
        this.fighter = createGson().fromJson(document.getString("fighter"), MatchSnapshot.class);
        this.opponent = createGson().fromJson(document.getString("opponent"), MatchSnapshot.class);
        this.won = document.getBoolean("won");
        this.matchType = document.getString("matchType");
        this.kit = document.getString("kit");
        this.eloChangeWinner = document.getInteger("eloChangeWinner");
        this.eloChangeLoser = document.getInteger("eloChangeLoser");
        this.createdAt = document.getDate("createdAt");
        if(kit.equalsIgnoreCase("sumo") && (matchType.equalsIgnoreCase("unranked") || matchType.equalsIgnoreCase("ranked"))) {
            this.winnerPoints = document.getInteger("winnerPoints");
            this.loserPoints = document.getInteger("loserPoints");
        }
    }

    public ProfileMatchHistory(MatchSnapshot fighter, MatchSnapshot opponent, boolean won, String matchType, String kit, int eloChangeWinner, int eloChangeLoser, Date createdAt, int winnerPoints, int loserPoints) {
        this.fighter = fighter;
        this.opponent = opponent;
        this.won = won;
        this.matchType = matchType;
        this.kit = kit;
        this.eloChangeWinner = eloChangeWinner;
        this.eloChangeLoser = eloChangeLoser;
        this.createdAt = createdAt;
        this.winnerPoints = winnerPoints;
        this.loserPoints = loserPoints;
    }

    public Document toDocument() {
        Document document = new Document();
        document.put("fighter", createGson().toJson(fighter));
        document.put("opponent", createGson().toJson(opponent));
        document.put("won", won);
        document.put("matchType", matchType);
        document.put("kit", kit);
        document.put("eloChangeWinner", eloChangeWinner);
        document.put("eloChangeLoser", eloChangeLoser);
        document.put("createdAt", createdAt);
        if(kit.equalsIgnoreCase("sumo") && (matchType.equalsIgnoreCase("unranked") || matchType.equalsIgnoreCase("ranked"))) {
            document.put("winnerPoints", winnerPoints);
            document.put("loserPoints", loserPoints);
        }
        return document;
    }

    private Gson createGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableTypeAdapter())
                .create();
    }
}

package xyz.refinedev.practice.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.leaderboards.GlobalLeaderboardsUpdateEvent;
import xyz.refinedev.practice.api.events.leaderboards.KitLeaderboardsUpdateEvent;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.task.ClanLeaderboardsTask;
import xyz.refinedev.practice.task.GlobalLeaderboardsTask;
import xyz.refinedev.practice.task.KitLeaderboardsTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/6/2021
 * Project: Array
 */

@Getter
public class LeaderboardsManager {

    private final Array plugin;
    private final Executor mongoExecutor;
    private final MongoCollection<Document> profiles, clans;

    private final List<LeaderboardsAdapter> globalLeaderboards = new LinkedList<>();
    private final List<LeaderboardsAdapter> clanLeaderboards = new LinkedList<>();

    private BukkitRunnable globalTask, kitTask, clanTask;

    public LeaderboardsManager(Array plugin) {
        this.plugin = plugin;
        this.mongoExecutor = plugin.getMongoThread();
        this.profiles = plugin.getMongoDatabase().getCollection("profiles");
        this.clans = plugin.getMongoDatabase().getCollection("clans");
    }

    public void init() {
        //Load the leaderboards so that they aren't empty until the
        //the task is ran after 3 minutes
        Kit.getKits().forEach(this::loadKitLeaderboards);
        this.loadClanLeaderboards();
        this.loadGlobalLeaderboards();

        globalTask = new GlobalLeaderboardsTask(plugin);
        kitTask = new KitLeaderboardsTask(plugin);
        clanTask = new ClanLeaderboardsTask(plugin);

        //Run the task async every 1 minute
        globalTask.runTaskTimerAsynchronously(plugin, 60 * 20L, 60 * 20L);
        kitTask.runTaskTimerAsynchronously(plugin, 60 * 20L, 60 * 20L);
        clanTask.runTaskTimerAsynchronously(plugin, 60 * 20L, 60 * 20L);
    }

    public void loadGlobalLeaderboards() {
        new GlobalLeaderboardsUpdateEvent().call();
        if (!this.globalLeaderboards.isEmpty()) this.globalLeaderboards.clear();

        List<Document> top10 = profiles.find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>());
        for ( Document document : top10 ) {
            LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
            leaderboardsAdapter.setName(document.getString("name"));
            leaderboardsAdapter.setUuid(UUID.fromString(document.getString("uuid")));
            leaderboardsAdapter.setElo(document.getInteger("globalElo"));

            //Sometimes the leaderboard entries are duplicated, this is what I am checking here
            if (!this.globalLeaderboards.isEmpty()) {
                this.globalLeaderboards.removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
            }
            this.globalLeaderboards.add(leaderboardsAdapter);
        }
    }

    public void loadKitLeaderboards(Kit kit) {
        new KitLeaderboardsUpdateEvent().call();

        List<LeaderboardsAdapter> eloLB = kit.getEloLeaderboards();
        List<LeaderboardsAdapter> winLB = kit.getWinLeaderboards();

        //Clear out the previous leaderboards
        if (!eloLB.isEmpty()) eloLB.clear();
        if (!winLB.isEmpty()) winLB.clear();

        List<Document> elo = this.profiles.find().sort(Sorts.descending("kitStatistics." + kit.getName() + ".elo")).limit(10).into(new ArrayList<>());
        for (Document document : elo) {
            Document kitStatistics = (Document) document.get("kitStatistics");
            if (kitStatistics.containsKey(kit.getName())) {
                Document kitDocument = (Document) kitStatistics.get(kit.getName());

                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setUuid(UUID.fromString(document.getString("uuid")));
                leaderboardsAdapter.setElo(kitDocument.getInteger("elo"));

                //Sometimes the leaderboard entries are duplicated, this is what I am checking here
                if (!eloLB.isEmpty()) {
                    eloLB.removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                }
                eloLB.add(leaderboardsAdapter);
            }
        }

        List<Document> won = this.profiles.find().sort(Sorts.descending("kitStatistics." + kit.getName() + ".won")).limit(10).into(new ArrayList<>());
        for (Document document : won) {
            Document kitStatistics = (Document) document.get("kitStatistics");
            if (kitStatistics.containsKey(kit.getName())) {
                Document kitDocument = (Document) kitStatistics.get(kit.getName());

                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setUuid(UUID.fromString(document.getString("uuid")));
                leaderboardsAdapter.setElo(kitDocument.getInteger("won"));

                //Sometimes the leaderboard entries are duplicated, this is what I am checking here
                if (!winLB.isEmpty()) {
                    winLB.removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                }
                winLB.add(leaderboardsAdapter);
            }
        }
    }

    public void loadClanLeaderboards() {
        for ( Document document : clans.find().sort(Sorts.descending("elo")).limit(10).into(new ArrayList<>()) ) {
            LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
            leaderboardsAdapter.setName(document.getString("name"));
            leaderboardsAdapter.setUuid(UUID.fromString(document.getString("_id")));
            leaderboardsAdapter.setElo(document.getInteger("elo"));
            getClanLeaderboards().add(leaderboardsAdapter);
        }
    }

}

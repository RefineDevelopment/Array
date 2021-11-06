package xyz.refinedev.practice.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.leaderboards.GlobalLeaderboardsUpdateEvent;
import xyz.refinedev.practice.api.events.leaderboards.KitLeaderboardsUpdateEvent;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.task.clan.ClanLeaderboardsTask;
import xyz.refinedev.practice.task.other.GlobalLeaderboardsTask;
import xyz.refinedev.practice.task.other.KitLeaderboardsTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private final MongoCollection<Document> profiles, clans;

    private final List<LeaderboardsAdapter> globalLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> clanLeaderboards = new ArrayList<>();

    private BukkitRunnable globalTask, kitTask, clanTask;

    public LeaderboardsManager(Array plugin) {
        this.plugin = plugin;
        this.profiles = plugin.getMongoManager().getProfiles();
        this.clans = plugin.getMongoManager().getClans();
    }

    public void init() {
        //Load the leaderboards so that they aren't empty until the
        //the task is ran after 3 minutes
        plugin.getKitManager().getKits().forEach(this::loadKitLeaderboards);
        this.loadClanLeaderboards();
        this.loadGlobalLeaderboards();

        globalTask = new GlobalLeaderboardsTask(plugin);
        kitTask = new KitLeaderboardsTask(plugin);
        clanTask = new ClanLeaderboardsTask(plugin);

        //Run the task async every 3 minute
        globalTask.runTaskTimer(plugin, 180 * 20L, 180 * 20L);
        kitTask.runTaskTimer(plugin, 180 * 20L, 180 * 20L);
        clanTask.runTaskTimer(plugin, 180 * 20L, 180 * 20L);
    }

    /**
     * Cancel all leaderboard tasks
     * This method is usually called on {@link JavaPlugin#onDisable()}
     */
    public void shutdown() {
        globalTask.cancel();
        kitTask.cancel();
        clanTask.cancel();
    }

    /**
     * Update and Reload Global Leaderboards
     * directly from mongo, this runs every 3 minutes
     */
    public void loadGlobalLeaderboards() {
        GlobalLeaderboardsUpdateEvent event = new GlobalLeaderboardsUpdateEvent();
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        if (!this.globalLeaderboards.isEmpty()) this.globalLeaderboards.clear();

        plugin.submitToThread(() -> {
            List<Document> leaderboard = profiles.find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>());
            for ( Document document : leaderboard ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setUuid(UUID.fromString(document.getObjectId("_id").toString()));
                leaderboardsAdapter.setElo(document.getInteger("globalElo"));

                synchronized (this.globalLeaderboards) {
                    this.globalLeaderboards.add(leaderboardsAdapter);
                }
            }
        });
    }

    /**
     * Update and Reload Kit Leaderboards
     * directly from mongo, this runs every 3 minutes
     */
    public void loadKitLeaderboards(Kit kit) {
        KitLeaderboardsUpdateEvent event = new KitLeaderboardsUpdateEvent();
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        //Clear out the previous leaderboards
        if (!kit.getEloLeaderboards().isEmpty()) kit.getEloLeaderboards().clear();
        if (!kit.getWinLeaderboards().isEmpty()) kit.getWinLeaderboards().clear();

        plugin.submitToThread(() -> {
            List<Document> elo = this.profiles.find().sort(Sorts.descending("kitStatistics." + kit.getName() + ".elo")).limit(10).into(new ArrayList<>());
            List<Document> won = this.profiles.find().sort(Sorts.descending("kitStatistics." + kit.getName() + ".won")).limit(10).into(new ArrayList<>());

            for ( Document document : elo ) {
                Document kitStatistics = (Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(kit.getName())) {
                    Document kitDocument = (Document) kitStatistics.get(kit.getName());

                    LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                    leaderboardsAdapter.setName(document.getString("name"));
                    leaderboardsAdapter.setUuid(UUID.fromString(document.getObjectId("_id").toString()));
                    leaderboardsAdapter.setElo(kitDocument.getInteger("elo"));

                    synchronized (kit.getEloLeaderboards()) {
                        kit.getEloLeaderboards().add(leaderboardsAdapter);
                    }
                }
            }

            for ( Document document : won ) {
                Document kitStatistics = (Document) document.get("kitStatistics");
                if (kitStatistics.containsKey(kit.getName())) {
                    Document kitDocument = (Document) kitStatistics.get(kit.getName());

                    LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                    leaderboardsAdapter.setName(document.getString("name"));
                    leaderboardsAdapter.setUuid(UUID.fromString(document.getString("_id")));
                    leaderboardsAdapter.setElo(kitDocument.getInteger("won"));

                    synchronized (kit.getWinLeaderboards()) {
                        kit.getWinLeaderboards().add(leaderboardsAdapter);
                    }
                }
            }
        });
    }

    /**
     * Update and Reload Clan Leaderboards
     * directly from mongo, this runs every 3 minutes
     */
    public void loadClanLeaderboards() {
        plugin.submitToThread(() -> {
            List<Document> leaderboard = clans.find().sort(Sorts.descending("elo")).limit(10).into(new ArrayList<>());
            for ( Document document : leaderboard ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setUuid(UUID.fromString(document.getObjectId("_id").toString()));
                leaderboardsAdapter.setElo(document.getInteger("elo"));

                synchronized (clanLeaderboards) {
                    clanLeaderboards.add(leaderboardsAdapter);
                }
            }
        });
    }
}

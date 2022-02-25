package xyz.refinedev.practice.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.events.profile.ProfileLoadEvent;
import xyz.refinedev.practice.api.events.profile.ProfileSaveEvent;
import xyz.refinedev.practice.api.events.profile.SpawnTeleportEvent;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.profile.history.ProfileHistory;
import xyz.refinedev.practice.profile.hotbar.HotbarLayout;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.rank.TablistRank;
import xyz.refinedev.practice.profile.settings.ProfileSettings;
import xyz.refinedev.practice.profile.statistics.ProfileStatistics;
import xyz.refinedev.practice.task.profile.ProfileHotbarTask;
import xyz.refinedev.practice.task.profile.ProfileQueryTask;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.text.DecimalFormat;
import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class ProfileManager {

    private final Array plugin;
    private final MongoCollection<Document> collection;

    private final Map<UUID, Profile> profiles = new HashMap<>();

    /**
     * This method merely just initiates
     * the profile tasks which are required
     * to run on a timer, we don't actually
     * fetch all the profiles from mongo as they are
     * fetched during {@link AsyncPlayerPreLoginEvent}
     */
    public void init() {
        ProfileQueryTask profileQueryTask = new ProfileQueryTask(this);
        profileQueryTask.runTaskTimerAsynchronously(plugin, 3600L, 3600L);

        ProfileHotbarTask profileHotbarTask = new ProfileHotbarTask(this);
        profileHotbarTask.runTaskTimer(plugin, 60L, 60L);
    }

    /**
     * Load the profile from the mongo
     * database, usually asynchronously.
     * The method heavily uses mongo and google's GSON
     *
     * @param profile {@link Profile} being loaded
     */
    public void load(Profile profile) {
        ProfileLoadEvent event = new ProfileLoadEvent(profile);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        for ( Kit kit : plugin.getKitManager().getKits() ) {
            profile.getStatisticsData().put(kit, new ProfileStatistics());
        }

        plugin.submitToThread(() -> {
            Document document = collection.find(Filters.eq("_id", profile.getUniqueId().toString())).first();

            if (document == null) {
                this.save(profile);
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(profile.getUniqueId());
            profile.setName(offlinePlayer.getName());

            profile.setKills(document.getInteger("kills"));
            profile.setDeaths(document.getInteger("deaths"));
            profile.setGlobalElo(document.getInteger("globalElo"));
            profile.setExperience(document.getInteger("experience"));
            profile.setSettings(Array.GSON.fromJson(document.getString("settings"), ProfileSettings.class));

            String killEffectString = document.getString("killEffect");
            if (killEffectString != null) {
                profile.setKillEffect(KillEffect.getByName(killEffectString));
            }

            String clanString = document.getString("clan");
            if (clanString != null) {
                profile.setClan(UUID.fromString(clanString));
            }

            Document kitStatistics = (Document) document.get("kitStatistics");
            for ( String key : kitStatistics.keySet() ) {
                Document kitDocument = (Document) kitStatistics.get(key);

                Kit kit = plugin.getKitManager().getByName(key);
                if (kit == null) continue;

                ProfileStatistics profileStatistics = new ProfileStatistics();

                profileStatistics.setElo(kitDocument.getInteger("elo") == null ? 1000 : kitDocument.getInteger("elo"));
                profileStatistics.setWon(kitDocument.getInteger("won"));
                profileStatistics.setLost(kitDocument.getInteger("lost"));

                profile.getStatisticsData().put(kit, profileStatistics);
            }

            Document kitsDocument = (Document) document.get("kitInventory");
            for ( String key : kitsDocument.keySet() ) {
                Kit kit = plugin.getKitManager().getByName(key);
                if (kit == null) continue;

                String jsonString = kitsDocument.getString(key);
                JsonArray kitsArray = JsonParser.parseString(jsonString).getAsJsonArray();
                KitInventory[] kitInventory = new KitInventory[4];

                for ( JsonElement kitElement : kitsArray ) {
                    JsonObject kitObject = kitElement.getAsJsonObject();

                    KitInventory loadout = new KitInventory(kitObject.get("name").getAsString());
                    loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                    loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                    kitInventory[kitObject.get("index").getAsInt()] = loadout;
                }

                profile.getStatisticsData().get(kit).setKitInventories(kitInventory);
            }

            if (document.getList("unrankedHistory", String.class) != null) {
                List<String> history = document.getList("unrankedHistory", String.class);
                for ( String matchHistory : history ) {
                    profile.getUnrankedMatchHistory().add(Array.GSON.fromJson(matchHistory, ProfileHistory.class));
                }
            }
            if (document.getList("rankedHistory", String.class) != null) {
                List<String> history = document.getList("rankedHistory", String.class);
                for ( String matchHistory : history ) {
                    profile.getRankedMatchHistory().add(Array.GSON.fromJson(matchHistory, ProfileHistory.class));
                }
            }
        });

        this.calculateGlobalElo(profile);
    }

    /**
     * Save the profile to mongo
     * mainly asynchronously, this method
     * heavily uses mongo and google's GSON
     *
     * @param profile {@link Profile} being saved
     */
    public void save(Profile profile) {
        ProfileSaveEvent event = new ProfileSaveEvent(profile);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        Document document = new Document();

        document.put("_id", profile.getUniqueId().toString());
        document.put("name", profile.getName());
        document.put("globalElo", profile.getGlobalElo());
        document.put("experience", profile.getExperience());
        document.put("kills", profile.getKills());
        document.put("deaths", profile.getDeaths());

        if (profile.getKillEffect() != null) document.put("killEffect", profile.getKillEffect().name());
        if (profile.hasClan()) document.put("clan", profile.getClan().toString());

        document.put("settings", Array.GSON.toJson(profile.getSettings()));

        Document kitStatisticsDocument = new Document();
        for ( Map.Entry<Kit, ProfileStatistics> entry : profile.getStatisticsData().entrySet() ) {
            Document kitDocument = new Document();

            if (entry.getKey().equals(plugin.getKitManager().getTeamFight())) continue;

            kitDocument.put("elo", entry.getValue().getElo());
            kitDocument.put("won", entry.getValue().getWon());
            kitDocument.put("lost", entry.getValue().getLost());

            kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
        }

        document.put("kitStatistics", kitStatisticsDocument);

        Document kitsDocument = new Document();
        for ( Map.Entry<Kit, ProfileStatistics> entry : profile.getStatisticsData().entrySet() ) {
            JsonArray kitsArray = new JsonArray();

            for ( int i = 0; i < 4; i++ ) {
                KitInventory loadout = entry.getValue().getLoadout(i);

                if (loadout != null) {
                    JsonObject kitObject = new JsonObject();
                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", loadout.getCustomName());
                    kitObject.addProperty("armor", InventoryUtil.serializeInventory(loadout.getArmor()));
                    kitObject.addProperty("contents", InventoryUtil.serializeInventory(loadout.getContents()));
                    kitsArray.add(kitObject);
                }
            }
            kitsDocument.put(entry.getKey().getName(), kitsArray.toString());
        }

        document.put("kitInventory", kitsDocument);

        if (!profile.getUnrankedMatchHistory().isEmpty()) {
            List<String> history = new ArrayList<>();
            for ( ProfileHistory matchHistory : profile.getUnrankedMatchHistory() ) {
                history.add(Array.GSON.toJson(matchHistory));
            }
            document.put("unrankedHistory", history);
        }
        if (!profile.getRankedMatchHistory().isEmpty()) {
            List<String> history = new ArrayList<>();
            for ( ProfileHistory matchHistory : profile.getRankedMatchHistory() ) {
                history.add(Array.GSON.toJson(matchHistory));
            }
            document.put("rankedHistory", history);
        }

        plugin.submitToThread(() -> collection.replaceOne(Filters.eq("_id", profile.getUniqueId().toString()), document, new ReplaceOptions().upsert(true)));
    }

    public void handleJoin(Profile profile) {
        Player player = profile.getPlayer();
        profile.setName(player.getName());

        this.calculateTabRank(profile);
        this.refreshHotbar(profile);
        this.teleportToSpawn(profile);

        for (Profile otherProfile : profiles.values()) {
            this.handleVisibility(otherProfile);
        }

        if (player.hasPermission("array.profile.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    public void handleLeave(Profile profile) {
        this.save(profile);

        if (profile.getRematchData() == null) return;
        Player target = plugin.getServer().getPlayer(profile.getRematchData().getTarget());
        
        if (target != null && target.isOnline()) {
            this.checkForHotbarUpdate(profile);
        }
    }

    /**
     * Recalculate the profile's global elo
     */
    public void calculateGlobalElo(Profile profile) {
        int globalElo = 0;
        int kitCounter = 0;
        for (Kit kit : profile.getStatisticsData().keySet()) {
            if (kit.getGameRules().isRanked()) {
                globalElo += profile.getStatisticsData().get(kit).getElo();
                kitCounter++;
            }
        }
        profile.setGlobalElo(Math.round((float) globalElo / kitCounter));
    }

    /**
     * Update the profile's hotbar
     *
     * @param profile {@link Profile} whose hotbar we are refreshing
     */
    public void refreshHotbar(Profile profile) {
        Player player = profile.getPlayer();
        if (player == null) return;

        PlayerUtil.reset(player);

        if (profile.isInLobby()) {
            if (profile.hasParty()) {
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.PARTY, profile));
            } else {
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.LOBBY, profile));
            }
        } else if (profile.isInQueue()) {
            player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.QUEUE, profile));
        /*} else if (profile.isInEvent()) {
            Event event = this.plugin.getEventManager().getEventByUUID(profile.getEvent());
            if (event.getEventPlayer(profile.getUniqueId()).getState().equals(EventPlayerState.ELIMINATED)) {
                PlayerUtil.spectator(player);
            }
            player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.EVENT, profile));*/
        } else if (profile.isInMatch()) {
            Match match = profile.getMatch();
            if (match.getTeamPlayer(player) != null && !match.getTeamPlayer(player).isAlive() && match.isTeamMatch()) {
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.MATCH_SPECTATE, profile));
            }
            if (profile.getState().equals(ProfileState.SPECTATING)) {
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.MATCH_SPECTATE, profile));
            }
        }
        player.updateInventory();
    }

    /**
     * Check if the profile requires a
     * hotbar refresh for event or rematch items
     *
     * @param profile {@link Profile} being checked
     */
    public void checkForHotbarUpdate(Profile profile) {
        Player player = profile.getPlayer();
        if (player == null) return;

        if (profile.isInLobby() && !profile.getKitEditor().isActive()) {
            boolean update = this.refreshRematch(profile);

            boolean activeEvent = false; //plugin.getEventManager().getActiveEvent() != null && plugin.getEventManager().getActiveEvent().isWaiting();
            int eventSlot = player.getInventory().first(plugin.getHotbarManager().getHotbarItem(HotbarType.EVENT_JOIN).getItem());

            if (eventSlot == -1 && activeEvent) {
                update = true;
            } else if (eventSlot != -1 && !activeEvent) {
                update = true;
            }

            if (update) {
                player.sendMessage(CC.translate("&cRefreshing hotbar"));
                this.refreshHotbar(profile);
            }
        }
    }

    /**
     * Teleport the profile to spawn
     * and refresh its visibility and hotbar
     */
    public void teleportToSpawn(Profile profile) {
        Player player = profile.getPlayer();

        SpawnTeleportEvent event = new SpawnTeleportEvent(player, plugin.getConfigHandler().getSpawn());
        plugin.getServer().getPluginManager().callEvent(event);

        this.handleVisibility(profile);
        this.refreshHotbar(profile);

        if (!event.isCancelled() && event.getLocation() != null) {
            player.teleport(event.getLocation());
        }
        this.applyLevels(profile, player);
    }

    public void applyLevels(Profile profile, Player player) {
        if (!plugin.getDivisionsManager().isXPBased()) return;
        if (!profile.getState().equals(ProfileState.IN_LOBBY)) return;

        //Just to make it look cool, we just apply the experience to the player's hotbar
        player.setLevel(this.getDivision(profile).getXpLevel());
        player.setExp((float) (profile.getExperience() % 100) / 100.0f);
    }

    /**
     * Get the profile's win/loose ratio
     *
     * @param profile {@link Profile} whose WLR we are fetching
     * @return Returns the WLR in {@link String} format
     */
    public String getWLR(Profile profile) {
        double totalWins = profile.getTotalWins();
        double totalLosses = profile.getTotalLost();

        double ratio = totalWins / Math.max(totalLosses, 1);
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(ratio);
    }

    /**
     * Refresh the {@link Profile}'s rematch data
     * and possibly reset it if needed
     *
     * @param profile {@link Profile} the profile being refreshed
     * @return {@link Boolean} whether the data was refreshed or not
     */
    public boolean refreshRematch(Profile profile) {
        Player player = profile.getPlayer();
        if (player == null) return false;

        if (profile.getRematchData() != null) {
            RematchProcedure rematchData = profile.getRematchData();
            Player target = Bukkit.getPlayer(rematchData.getTarget());

            if (System.currentTimeMillis() - rematchData.getTimestamp() >= 30_000) {
                profile.setRematchData(null);
                return true;
            } else if (target == null || !target.isOnline()) {
                profile.setRematchData(null);
                return true;
            } else {
                Profile targetProfile = this.getProfile(target.getUniqueId());
                if (!(targetProfile.isInLobby() || targetProfile.isInQueue())) {
                    profile.setRematchData(null);
                    return true;
                } else if (!rematchData.getKey().equals(profile.getRematchData().getKey())) {
                    profile.setRematchData(null);
                    return true;
                } else if (rematchData.isReceive() || rematchData.isSent()) {
                    int requestSlot = player.getInventory().first(plugin.getHotbarManager().getHotbarItem(HotbarType.REMATCH_REQUEST).getItem());
                    return requestSlot != -1;
                }
            }
        }
        return false;
    }

    /**
     * Calculate the tablist rank of the given profile
     *
     * @param profile {@link Profile} the profile whose tab rank we are calculating
     */
    public void calculateTabRank(Profile profile) {
        if (plugin.getConfigHandler().getTablistRanks() == null || plugin.getConfigHandler().getTablistRanks().isEmpty()) return;
        LinkedList<TablistRank> ranks = new LinkedList<>(plugin.getConfigHandler().getTablistRanks());
        ranks.sort(Comparator.comparingInt(TablistRank::getPriority));

        while ( ranks.size() > 0 ) {
            TablistRank rank = ranks.poll();
            if (!profile.getPlayer().hasPermission(rank.getPermission())) continue;

            profile.setTablistRank(rank);
        }
    }

    /**
     * More simpler and direct method to access
     * and update profile's visibility
     *
     * @param profile The profile whose visibility is being updated
     */
    public void handleVisibility(Profile profile) {
        if (profile.getPlayer() == null) return;

        for ( Player otherPlayer : plugin.getServer().getOnlinePlayers() ) {
            this.handleVisibility(profile, otherPlayer);
        }
    }

    /**
     * Handle and update the player's visibility
     *
     * @param profile The profile whose visibility is being updated
     * @param otherPlayer The viewer of the player
     */
    public void handleVisibility(Profile profile, Player otherPlayer) {
        Player player = profile.getPlayer();
        if (player == null || otherPlayer == null) return;

        boolean hide = true;
        if (profile.isInLobby() || profile.isInQueue()) {
            hide = !profile.getSettings().isShowPlayers();
            if (profile.hasParty()) {
                Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getUniqueId());
                hide = !party.containsPlayer(player);
            }
        } else if (profile.isInFight()) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);

            if (teamPlayer != null && teamPlayer.isAlive()) {
                hide = false;
            }
        } else if (profile.isSpectating()) {
            /*if (profile.isInEvent()) {
                Event event = plugin.getEventManager().getEventByUUID(profile.getEvent());
                EventPlayer eventPlayer = event.getEventPlayer(otherPlayer.getUniqueId());
                if (eventPlayer != null && eventPlayer.getState() == EventPlayerState.WAITING) {
                    hide = false;
                }
            } else*/ {
                Match match = profile.getMatch();
                TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);
                if (teamPlayer != null && teamPlayer.isAlive()) {
                    hide = false;
                } else if (match.getSpectatorList().contains(otherPlayer.getUniqueId())) {
                    if (profile.getSettings().isShowSpectator()) {
                        hide = false;
                    }
                }
            }
        }/* else if (profile.isInEvent()) {
            Event event = plugin.getEventManager().getEventByUUID(profile.getEvent());
            if (!event.isSpectating(otherPlayer.getUniqueId())) {
                EventPlayer eventPlayer = event.getEventPlayer(otherPlayer.getUniqueId());
                if (eventPlayer != null && eventPlayer.getState() == EventPlayerState.WAITING) {
                    hide = false;
                }
            }
        }*/

        if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
            plugin.getNameTagHandler().reloadPlayer(player, otherPlayer);
            plugin.getNameTagHandler().reloadOthersFor(player);
        }

        if (hide) {
            TaskUtil.run(() -> player.hidePlayer(otherPlayer));
        } else {
            TaskUtil.run(() -> player.showPlayer(otherPlayer));
        }
    }

    /**
     * Get a {@link Profile}'s division either
     * by ELO or experience
     *
     * @param profile {@link Profile} whose division we are getting
     * @return {@link ProfileDivision}
     */
    public ProfileDivision getDivision(Profile profile) {
        if (plugin.getDivisionsManager().isXPBased()) {
            return plugin.getDivisionsManager().getDivisionByXP(profile.getExperience());
        }
        return plugin.getDivisionsManager().getDivisionByELO(profile.getGlobalElo());
    }

    /**
     * Get a Profile by its UniqueId
     *
     * @param uuid {@link UUID} the uniqueId of the profile
     * @return {@link Profile} the profile requested
     */
    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    /**
     * Get a Profile by its player
     *
     * @param player {@link Player} the player of the profile
     * @return {@link Profile} the profile requested
     */
    public Profile getProfile(Player player) {
       return profiles.get(player.getUniqueId());
    }

    /**
     * Can the profile not send a duel request
     * to another player
     *
     * @param player The player receiving the duel request
     * @return {@link Boolean}
     */
    public boolean cannotSendDuelRequest(Profile profile, Player player) {
        if (!profile.getDuelRequests().containsKey(player.getUniqueId())) {
            return false;
        }

        DuelRequest request = profile.getDuelRequests().get(player.getUniqueId());
        if (request.isExpired()) {
            profile.getDuelRequests().remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    /**
     * Does the profile have a pending duel request
     *
     * @param player The player whose request is pending to this profile
     * @return {@link Boolean}
     */
    public boolean isPendingDuelRequest(Profile profile, Player player) {
        if (!profile.getDuelRequests().containsKey(player.getUniqueId())) {
            return false;
        }
        DuelRequest request = profile.getDuelRequests().get(player.getUniqueId());
        if (request.isExpired()) {
            profile.getDuelRequests().remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    /**
     * Get a profile's core color from the Core Hook
     * in ChatColor format
     *
     * @return {@link ChatColor}
     */
    public ChatColor getColor(Profile profile) {
        return plugin.getCoreHandler().getRankColor(profile.getPlayer());
    }


    /**
     * Get a profile's coloured color from the Core Hook
     *
     * @return {@link String}
     */
    public String getColouredName(Profile profile) {
        return plugin.getCoreHandler().getFullName(profile.getPlayer());
    }
}

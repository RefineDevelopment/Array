package xyz.refinedev.practice.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;

import lombok.Getter;
import lombok.Setter;

import org.bson.Document;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.api.ArrayCache;
import xyz.refinedev.practice.api.events.leaderboards.GlobalLeaderboardsUpdateEvent;
import xyz.refinedev.practice.api.events.profile.ProfileGlobalEloCalculateEvent;
import xyz.refinedev.practice.api.events.profile.ProfileLoadEvent;
import xyz.refinedev.practice.api.events.profile.ProfileSaveEvent;
import xyz.refinedev.practice.api.events.profile.SpawnTeleportEvent;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.brawl.Brawl;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.duel.DuelProcedure;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.meta.player.EventPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayerState;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.kit.kiteditor.KitEditor;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.hotbar.HotbarLayout;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.profile.settings.meta.SettingsMeta;
import xyz.refinedev.practice.profile.statistics.StatisticsData;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueProfile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Executor;

@SuppressWarnings("all")
@Getter @Setter
public class Profile {

    @Getter private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter private static final List<LeaderboardsAdapter> globalEloLeaderboards = new ArrayList<>();
    @Getter public static final List<Player> playerList = new ArrayList<>();

    private final Array plugin = Array.getInstance();

    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    private final Map<Kit, StatisticsData> statisticsData = new LinkedHashMap<>();
    private final List<ClanInvite> clanInviteList = new ArrayList<>();
    private final List<Location> plates = new ArrayList<>();

    /*
     * Profile Mongo
     */
    @Getter private static MongoCollection<Document> collection = Array.getInstance().getMongoDatabase().getCollection("profiles");
    private Executor mongoThread = plugin.getMongoThread();


    /*
     * Part of Constructor
     */
    private String name;
    private final UUID uuid;

    /*
     * Integer Values
     */
    int globalElo = 1000;
    int bridgeRounds = 0;

    /*
     * Profile Meta
     */
    private ProfileState state;
    private Party party;
    private Match match;
    private Queue queue;
    private Brawl brawl;
    private Clan clan;
    private Event event;

    /*
     * Fight Meta
     */
    private ClanProfile clanProfile;
    private QueueProfile queueProfile;
    private DuelProcedure duelProcedure;
    private RematchProcedure rematchData;

    /*
     * Spectator Mode
     */
    private Player spectating;

    /*
     * Cooldowns
     */
    private Cooldown enderpearlCooldown = new Cooldown(0);
    private Cooldown bowCooldown = new Cooldown(0);

    /*
     * Essential Meta
     */
    private final SettingsMeta settings = new SettingsMeta();
    private final KitEditor kitEditor = new KitEditor();
    private boolean canIssueRating = false;
    private Arena ratingArena;

    /**
     * The main constructor for the Profile
     *
     * @param uuid The {@link UUID} of the Player
     */
    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.IN_LOBBY;

        for (Kit kit : Kit.getKits()) {
            this.statisticsData.put(kit, new StatisticsData());
        }
        this.calculateGlobalElo();
    }

    public static void preload() {
        // Players might have joined before the plugin finished loading
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            Profile profile = new Profile(player.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {
                player.kickPlayer(CC.RED + "The server is loading...");
                continue;
            }

            profiles.put(player.getUniqueId(), profile);
        }
        if (!Array.getInstance().isDisabling()) {
            //Save prevent data loss
            TaskUtil.runTimerAsync(() -> {
                Profile.getProfiles().values().forEach(Profile::save);
                Profile.getProfiles().values().forEach(Profile::load);
            }, 36000L, 36000L);

            //Reload Leaderboards
            TaskUtil.runTimerAsync(() -> {
                Profile.loadUUIDCache();
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
                Profile.loadGlobalLeaderboards();
            }, 180 * 20L, 180 * 20L);

            //Refresh players' hotbeds every 3 seconds
            TaskUtil.runTimerAsync(() -> Profile.getProfiles().values().forEach(Profile::checkForHotbarUpdate), 40L, 40L);
        }
    }

    /**
     * Returns a Profile from a uuid
     *
     * @param uuid The uuid whose profile is being returned
     * @return {@link Profile}
     */
    public static Profile getByUuid(UUID uuid) {
        Profile profile = profiles.get(uuid);

        if (profile == null) {
            profile = new Profile(uuid);
        }

        return profile;
    }

    /**
     * Returns a Profile from a player
     *
     * @param player The player whose profile is being returned
     * @return {@link Profile}
     */
    public static Profile getByPlayer(Player player) {
        Profile profile = profiles.get(player.getUniqueId());

        if (profile == null) {
            profile = new Profile(player.getUniqueId());
        }

        return profile;
    }

    /**
     * Load the global leaderboards from the mongo database
     */
    public static void loadGlobalLeaderboards() {
        if (!getGlobalEloLeaderboards().isEmpty()) getGlobalEloLeaderboards().clear();
            Array.getInstance().getMongoThread().execute(() -> {
            for ( Document document : Profile.getCollection().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setElo(document.getInteger("globalElo"));
                if (!globalEloLeaderboards.isEmpty()) {
                    globalEloLeaderboards.removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                }
                getGlobalEloLeaderboards().add(leaderboardsAdapter);
            }
        });
        new GlobalLeaderboardsUpdateEvent().call();
    }

    public static void loadUUIDCache() {
        ArrayCache.getPlayerCache().clear();
        for (Document document : Profile.getCollection().find()) {
            if (document == null) return;

            String name = document.getString("name");
            UUID uuid = UUID.fromString(document.getString("uuid"));

            ArrayCache.getPlayerCache().put(name, uuid);
        }
    }

    /**
     * Get a profile's rank color from the Core Hook
     * in ChatColor format
     *
     * @return {@link ChatColor}
     */
    public ChatColor getColor() {
        return Rank.getRankType().getRankColor(this.getPlayer());
    }

    /**
     * Load the profile from the mongo database
     */
    public void load() {
        mongoThread.execute(() -> {
            try {

                Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

                if (document  ==  null) {
                    this.save();
                    return;
                }

                this.globalElo = document.getInteger("globalElo");

                if (document.getString("clan") != null) this.clan = Clan.getByName(document.getString("clan"));

                Document options = (Document) document.get("settings");

                this.settings.setScoreboardEnabled(options.getBoolean("showScoreboard"));
                this.settings.setAllowSpectators(options.getBoolean("allowSpectators"));
                this.settings.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
                this.settings.setPingFactor(options.getBoolean("pingFactor"));
                this.settings.setDeathLightning(options.getBoolean("toggleLightning"));
                this.settings.setPingScoreboard(options.getBoolean("pingScoreboard"));
                this.settings.setCpsScoreboard(options.getBoolean("cpsScoreboard"));
                this.settings.setTmessagesEnabled(options.getBoolean("tmessagesEnabled"));
                this.settings.setVanillaTab(options.getBoolean("usingVanillaTab"));
                this.settings.setShowPlayers(options.getBoolean("showPlayers"));

                Document kitStatistics = (Document) document.get("kitStatistics");

                for ( String key : kitStatistics.keySet() ) {
                    Document kitDocument = (Document) kitStatistics.get(key);
                    Kit kit = Kit.getByName(key);

                    if (kit !=  null) {
                        StatisticsData statisticsData = new StatisticsData();
                        if (kitDocument.getInteger("elo") !=  null) {
                            statisticsData.setElo(kitDocument.getInteger("elo"));
                        } else {
                            kitDocument.put("elo", 0);
                        }
                        if (kitDocument.getInteger("won") !=  null) {
                            statisticsData.setWon(kitDocument.getInteger("won"));
                        } else {
                            kitDocument.put("won", 0);
                        }
                        if (kitDocument.getInteger("lost") !=  null) {
                            statisticsData.setLost(kitDocument.getInteger("lost"));
                        } else {
                            kitDocument.put("lost", 0);
                        }
                        this.statisticsData.put(kit, statisticsData);
                    }
                }

                Document kitsDocument = (Document) document.get("loadouts");

                for ( String key : kitsDocument.keySet() ) {
                    Kit kit = Kit.getByName(key);

                    if (kit !=  null) {
                        JsonArray kitsArray = new JsonParser().parse(kitsDocument.getString(key)).getAsJsonArray();
                        KitInventory[] loadouts = new KitInventory[4];

                        for ( JsonElement kitElement : kitsArray ) {
                            JsonObject kitObject = kitElement.getAsJsonObject();

                            KitInventory loadout = new KitInventory(kitObject.get("name").getAsString());
                            loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                            loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                            loadouts[kitObject.get("index").getAsInt()] = loadout;
                        }

                        statisticsData.get(kit).setLoadouts(loadouts);
                    }
                }
            } catch (Exception e) {
                this.save();
            }
        });
        new ProfileLoadEvent(this).call();
    }

    /**
     * Save the profile to the mongo database
     */
    public void save() {
        mongoThread.execute(() -> {
            Document document = new Document();
            document.put("uuid", uuid.toString());
            document.put("name", Bukkit.getOfflinePlayer(uuid).getName());
            document.put("globalElo", globalElo);

            if (clan != null) document.put("clan", clan.getName());

            Document optionsDocument = new Document();
            optionsDocument.put("showScoreboard", settings.isScoreboardEnabled());
            optionsDocument.put("allowSpectators", settings.isAllowSpectators());
            optionsDocument.put("receiveDuelRequests", settings.isReceiveDuelRequests());
            optionsDocument.put("pingFactor", settings.isPingFactor());
            optionsDocument.put("toggleLightning", settings.isDeathLightning());
            optionsDocument.put("pingScoreboard", settings.isPingScoreboard());
            optionsDocument.put("tmessagesEnabled", settings.isTmessagesEnabled());
            optionsDocument.put("usingVanillaTab", settings.isVanillaTab());
            optionsDocument.put("cpsScoreboard", settings.isCpsScoreboard());
            optionsDocument.put("showPlayers", settings.isShowPlayers());

            document.put("settings", optionsDocument);

            Document kitStatisticsDocument = new Document();
            for ( Map.Entry<Kit, StatisticsData> entry : statisticsData.entrySet() ) {
                Document kitDocument = new Document();
                kitDocument.put("elo", entry.getValue().getElo());
                kitDocument.put("won", entry.getValue().getWon());
                kitDocument.put("lost", entry.getValue().getLost());
                kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
            }

            document.put("kitStatistics", kitStatisticsDocument);

            Document kitsDocument = new Document();
            for ( Map.Entry<Kit, StatisticsData> entry : statisticsData.entrySet() ) {
                JsonArray kitsArray = new JsonArray();

                for ( int i=0; i < 4; i++ ) {
                    KitInventory loadout=entry.getValue().getLoadout(i);

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

            document.put("loadouts", kitsDocument);

            collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
        });
        new ProfileSaveEvent(this).call();
    }

    /**
     * Recalculate the profile's global elo
     */
    public void calculateGlobalElo() {
        int globalElo = 0;
        int kitCounter = 0;
        for (Kit kit : this.statisticsData.keySet()) {
            if (kit.getGameRules().isRanked()) {
                globalElo += this.statisticsData.get(kit).getElo();
                kitCounter++;
            }
        }
        this.globalElo = Math.round(globalElo / kitCounter);
        new ProfileGlobalEloCalculateEvent(this).call();
    }

    /**
     * Can the profile send a duel request
     * to another player
     *
     * @param player The player receiving the duel request
     * @return {@link Boolean}
     */
    public boolean canSendDuelRequest(Player player) {
        if (!sentDuelRequests.containsKey(player.getUniqueId())) {
            return true;
        }

        DuelRequest request = sentDuelRequests.get(player.getUniqueId());
        if (request.isExpired()) {
            sentDuelRequests.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    /**
     * Does the profile have a pending duel request
     *
     * @param player The player whose request is pending to this profile
     * @return {@link Boolean}
     */
    public boolean isPendingDuelRequest(Player player) {
        if (!sentDuelRequests.containsKey(player.getUniqueId())) {
            return false;
        }

        DuelRequest request = sentDuelRequests.get(player.getUniqueId());

        if (request.isExpired()) {
            sentDuelRequests.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    /**
     * Execute join tasks for the profile
     */
    public void handleJoin() {
        Player player = getPlayer();

        getPlayerList().removeIf(players -> players.getName().equalsIgnoreCase(player.getName()));

        Profile.getPlayerList().add(player);
        Profile.getProfiles().values().forEach(Profile::handleVisibility);

        plugin.getTaskThread().execute(() -> {
            ArrayCache.getPlayerCache().putIfAbsent(player.getName(), this.getUuid());

            this.setName(player.getName());
            this.refreshHotbar();
        });

        this.teleportToSpawn();

        //Visibility Bug Fix
        TaskUtil.runLater(this::handleVisibility, 5L);
    }

    /**
     * Execute leave tasks for the profile
     */
    public void handleLeave() {
       //If they are in a queue, remove them
        if (this.isInQueue()) this.getQueue().removePlayer(this.getQueueProfile());

        plugin.getTaskThread().execute(() -> {
            //Remove from the Tab PlayerList
            Profile.getPlayerList().remove(this.getPlayer());
            //Save their Profile
            this.save();
            //If there is a rematch request pending for them, remove it
            if (this.getRematchData() != null) {
                Player target = plugin.getServer().getPlayer(this.getRematchData().getTarget());
                if (target != null && target.isOnline()) {
                    Profile.getByUuid(target.getUniqueId()).checkForHotbarUpdate();
                }
            }
            //Bug fix for tournament party leave
            if (this.getParty() !=null && Tournament.CURRENT_TOURNAMENT !=null && Tournament.CURRENT_TOURNAMENT.isParticipating(this.getPlayer())) {
                Tournament.CURRENT_TOURNAMENT.leave(this.getParty());
            }
        });
    }

    /**
     * Teleport the profile to spawn
     */
    public void teleportToSpawn() {
        final Player player = getPlayer();
        SpawnTeleportEvent event = new SpawnTeleportEvent(player, plugin.getConfigHandler().getSpawn());
        event.call();

        //Update their visibility
        this.handleVisibility();
        this.refreshHotbar();

        if (!event.isCancelled() && event.getLocation() != null) {
            player.teleport(event.getLocation());
        }
    }

    /**
     * See if the profile's hotbar needs to be updated
     */
    public void checkForHotbarUpdate() {
        final Player player = getPlayer();

        if (player == null) return;

        if (isInLobby() && !kitEditor.isActive()) {
            boolean update = false;

            if (rematchData != null) {
                final Player target = Bukkit.getPlayer(rematchData.getTarget());

                if (System.currentTimeMillis() - rematchData.getTimestamp() >= 30_000) {
                    rematchData = null;
                    update = true;
                } else if (target == null || !target.isOnline()) {
                    rematchData = null;
                    update = true;
                } else {
                    Profile profile = Profile.getByUuid(target.getUniqueId());

                    if (!(profile.isInLobby() || profile.isInQueue())) {
                        rematchData = null;
                        update = true;
                    } else if (this.getRematchData() == null) {
                        rematchData = null;
                        update = true;
                    } else if (!rematchData.getKey().equals(this.getRematchData().getKey())) {
                        rematchData = null;
                        update = true;
                    } else if (rematchData.isReceive()) {
                        int requestSlot = player.getInventory().first(plugin.getHotbarManager().getHotbarItem(HotbarType.REMATCH_REQUEST).getItem());

                        if (requestSlot != -1) {
                            update = true;
                        }
                    }
                }
            }

            boolean activeEvent = plugin.getEventManager().getActiveEvent() != null && plugin.getEventManager().getActiveEvent().isWaiting();
            int eventSlot = player.getInventory().first(plugin.getHotbarManager().getHotbarItem(HotbarType.EVENT_JOIN).getItem());

            if (eventSlot == -1 && activeEvent) {
                update = true;
            } else if (eventSlot != -1 && !activeEvent) {
                update = true;
            }

            if (update) TaskUtil.run(this::refreshHotbar);
        }
    }


    public void reset() {
        PlayerUtil.reset(getPlayer());
    }

    /**
     * Update the profile's hotbar
     */
    public void refreshHotbar() {
        Player player = getPlayer();

        if (player != null) {
            PlayerUtil.reset(player);

            if (isInLobby() && !kitEditor.isActive()) {
                if (hasParty()) {
                    player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.PARTY, this));
                } else {
                    player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.LOBBY, this));
                }
            } else if (isInQueue()) {
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.QUEUE, this));
            } else if (isSpectating()) {
                TaskUtil.runLater(() -> PlayerUtil.spectator(player), 2L);
            } else if (isInEvent()) {
                if (getEvent().getEventPlayer(uuid).getState().equals(EventPlayerState.ELIMINATED)) {
                    TaskUtil.runLater(() -> PlayerUtil.spectator(player), 2L);
                }
                player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.EVENT, this));
            } else if (isInFight()) {
                if (!match.getTeamPlayer(player).isAlive()) {
                    player.getInventory().setContents(plugin.getHotbarManager().getLayout(HotbarLayout.MATCH_SPECTATE, this));
                }
            }
            player.updateInventory();
        }
    }

    /**
     * Get the profile's win/loose ratio
     *
     * @return Returns the WLR in {@link String} format
     */
    public String getWLR() {
        double totalWins = this.getTotalWins();
        double totalLosses = this.getTotalLost();

        double ratio = totalWins / Math.max(totalLosses, 1);
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(ratio);
    }

    /**
     * Handle and update the player's visibility
     *
     * @param player The player whose visibility is being updated
     * @param otherPlayer The viewer of the player
     */
    public void handleVisibility(Player player, Player otherPlayer) {
        if (player == null || otherPlayer == null) return;
        boolean hide = true;
        if (state == ProfileState.IN_LOBBY || state == ProfileState.IN_QUEUE) {
            if (this.getSettings().isShowPlayers()) {
                hide = false;
            }
            if (party != null && party.containsPlayer(otherPlayer)) {
                hide = false;
            }
            if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
                TaskUtil.runAsync(() -> plugin.getNameTagHandler().reloadPlayer(player, otherPlayer));
            }
        } else if (isInFight()) {
            TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);

            if (teamPlayer != null && teamPlayer.isAlive()) {
                hide = false;
            }
            if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
                TaskUtil.runAsync(() -> plugin.getNameTagHandler().reloadPlayer(player, otherPlayer));
            }
        } else if (isSpectating()) {
            if (event != null) {
                EventPlayer eventPlayer = event.getEventPlayer(otherPlayer.getUniqueId());
                if (eventPlayer != null && eventPlayer.getState() == EventPlayerState.WAITING) {
                    hide = false;
                }
            }  else {
                TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);
                if (teamPlayer != null && teamPlayer.isAlive()) {
                    hide = false;
                }
            }
            if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
                TaskUtil.runAsync(() -> plugin.getNameTagHandler().reloadPlayer(player, otherPlayer));
            }
        } else if (isInEvent()) {
            if (event != null) {
                if (!event.getSpectators().contains(otherPlayer.getUniqueId())) {
                    EventPlayer eventPlayer = event.getEventPlayer(otherPlayer.getUniqueId());
                    if (eventPlayer != null && eventPlayer.getState() == EventPlayerState.WAITING) {
                        hide = false;
                    }
                }
            }
            if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
                TaskUtil.runAsync(() -> plugin.getNameTagHandler().reloadPlayer(player, otherPlayer));
            }
        }

        if (hide) {
            TaskUtil.run(() -> player.hidePlayer(otherPlayer));
        } else {
            TaskUtil.run(() -> player.showPlayer(otherPlayer));
        }
    }

    /**
     * More simpler and direct method to access
     * and update profile's visibility
     */
    public void handleVisibility() {
        Player player = getPlayer();
        if (player != null) {
            TaskUtil.runAsync(() -> {
                for ( Player otherPlayer : Bukkit.getOnlinePlayers() ) {
                    this.handleVisibility(player, otherPlayer);
                }
            });
        }
    }

    /**
     * Apply the Enderpearl cooldown to the profile
     * and send it to LunarAPI if they are on Lunar
     *
     * @param cooldown {@link Cooldown}
     */
    public void setEnderpearlCooldown(Cooldown cooldown) {
        this.enderpearlCooldown = cooldown;

        final Player player = this.getPlayer();
        if (plugin.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) LunarClientAPICooldown.sendCooldown(player, "Enderpearl");
    }

    /**
     * Apply the Bow cooldown to the profile
     * and send it to LunarAPI if they are on Lunar
     *
     * @param cooldown {@link Cooldown}
     */
    public void setBowCooldown(Cooldown cooldown) {
        this.bowCooldown = cooldown;

        final Player player = this.getPlayer();
        if (plugin.getServer().getPluginManager().isPluginEnabled("LunarClient-API")) LunarClientAPICooldown.sendCooldown(player, "Bow");
    }

    public String getEloLeague() {
        return plugin.getDivisionsManager().getDivision(this);
    }

    public Integer getTotalWins() {
        return this.statisticsData.values().stream().mapToInt(StatisticsData::getWon).sum();
    }

    public Integer getTotalLost() {
        return this.statisticsData.values().stream().mapToInt(StatisticsData::getLost).sum();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isInLobby() {
        return state == ProfileState.IN_LOBBY;
    }

    public boolean isInQueue() {
        return state == ProfileState.IN_QUEUE && queue != null && queueProfile != null;
    }

    public boolean isInMatch() {
        return match != null;
    }

    public boolean isInFight() {
        return state == ProfileState.IN_FIGHT && match != null;
    }

    public boolean isSpectating() {
        return state == ProfileState.SPECTATING && (match != null || event != null);
    }

    public boolean isInEvent() {
        return event != null;
    }

    public boolean isInBrackets() {
        return isInEvent() && (event.getType().equals(EventType.BRACKETS_SOLO) || event.getType().equals(EventType.BRACKETS_TEAM));
    }

    public boolean isInTournament() {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            return Tournament.CURRENT_TOURNAMENT.isParticipating(getPlayer());
        }
        return false;
    }

    public boolean isInBrawl() {
        return state == ProfileState.IN_BRAWL && brawl != null;
    }
    
    public boolean isInSomeSortOfFight() {
        return (state == ProfileState.IN_FIGHT && match != null) || (state == ProfileState.IN_EVENT) || (state == ProfileState.IN_BRAWL);
    }

    public boolean isBusy() {
        return isInQueue() || isInFight() || isInEvent() || isSpectating() || isInTournament() || isInBrawl();
    }

    public boolean hasClan() {
        return clan != null;
    }

    public boolean hasParty() {
        return party != null;
    }
}

package me.drizzy.practice.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.clan.ClanInvite;
import me.drizzy.practice.duel.DuelProcedure;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.essentials.event.SpawnTeleportEvent;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.brackets.player.BracketsPlayer;
import me.drizzy.practice.events.types.brackets.player.BracketsPlayerState;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.gulag.player.GulagPlayer;
import me.drizzy.practice.events.types.gulag.player.GulagPlayerState;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.lms.player.LMSPlayer;
import me.drizzy.practice.events.types.lms.player.LMSPlayerState;
import me.drizzy.practice.events.types.oitc.OITC;
import me.drizzy.practice.events.types.oitc.player.OITCPlayerState;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.parkour.player.ParkourPlayer;
import me.drizzy.practice.events.types.parkour.player.ParkourPlayerState;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayer;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayerState;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.events.types.sumo.player.SumoPlayer;
import me.drizzy.practice.events.types.sumo.player.SumoPlayerState;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.hotbar.HotbarLayout;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.kiteditor.KitEditor;
import me.drizzy.practice.leaderboards.LeaderboardsAdapter;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueProfile;
import me.drizzy.practice.settings.meta.SettingsMeta;
import me.drizzy.practice.statistics.StatisticsData;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.InventoryUtil;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.other.NameTags;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.other.TaskUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = "all")
@Getter
@Setter
public class Profile {

    @Getter private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter private static MongoCollection<Document> collection = Array.getInstance().getMongoDatabase().getCollection("profiles");
    public static Executor mongoThread = Array.getInstance().getMongoThread();


    @Getter public static final List<Player> playerList = new ArrayList<>();
    @Getter private static final List<LeaderboardsAdapter> globalEloLeaderboards = new ArrayList<>();
    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    private final Map<Kit, StatisticsData> statisticsData = new LinkedHashMap<>();
    private final List<ClanInvite> clanInviteList = new ArrayList<>();
    private final List<Location> plates = new ArrayList<>();

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
     * Events
     */
    private Sumo sumo;
    private Brackets brackets;
    private LMS lms;
    private Parkour parkour;
    private Gulag gulag;
    private OITC OITC;
    private Spleef spleef;

    /*
     * Objects
     */
    private ProfileState state;
    private Party party;
    private Match match;
    private Queue queue;
    private Clan clan;

    /*
     * Fight Meta
     */
    private QueueProfile queueProfile;
    private DuelProcedure duelProcedure;
    private ProfileRematchData rematchData;

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
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = new Profile(player.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {
                player.kickPlayer(CC.RED + "The server is loading...");
                continue;
            }

            profiles.put(player.getUniqueId(), profile);
        }

        // Save every minute to prevent data loss
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.getProfiles().values().forEach(Profile::save);
                Profile.getProfiles().values().forEach(Profile::load);
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 36000L, 36000L);

        // Reload global elo leaderboards
        new BukkitRunnable() {
            @Override
            public void run() {
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
                loadGlobalLeaderboards();
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 600L, 600L);

        // Refresh players' hotbars every 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.checkForHotbarUpdate();
                }
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 40L, 40L);
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
            mongoThread.execute(() -> {
            for ( Document document : Profile.getCollection().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName((String) document.get("name"));
                leaderboardsAdapter.setElo((Integer) document.get("globalElo"));
                globalEloLeaderboards.removeIf(adapter -> adapter.getName().equalsIgnoreCase(leaderboardsAdapter.getName()));
                getGlobalEloLeaderboards().add(leaderboardsAdapter);
            }
        });
    }

    /**
     * Get a profile's rank color from the Core Hook
     * in ChatColor format
     *
     * @return {@link ChatColor}
     */
    public ChatColor getColor() {
        if (Array.getInstance().getEssentials().getNametagMeta().getDefaultColor().equalsIgnoreCase("<rank_color>")) {
            return Array.getInstance().getRankManager().getRankColor(this.getPlayer());
        } else {
            return ChatColor.valueOf(Array.getInstance().getEssentials().getNametagMeta().getDefaultColor());
        }
    }

    /**
     * Does the profile have a clan
     * or is he a part of the clan
     *
     * @return {@link Boolean}
     */
    public boolean hasClan() {
        if (clan == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Load the profile from the mongo database
     */
    public void load() {
        mongoThread.execute(() -> {
        try {

            Document document=collection.find(Filters.eq("uuid", uuid.toString())).first();

            if (document == null) {
                this.save();
                return;
            }

            this.globalElo = document.getInteger("globalElo");

            if (document.getString("clan") != null) {
                this.clan = Clan.getByName(document.getString("clan"));
            }

            Document options = (Document) document.get("settings");

            this.settings.setShowScoreboard(options.getBoolean("showScoreboard"));
            this.settings.setAllowSpectators(options.getBoolean("allowSpectators"));
            this.settings.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
            this.settings.setUsingPingFactor(options.getBoolean("usingPingFactor"));
            this.settings.setLightning(options.getBoolean("toggleLightning"));
            this.settings.setPingScoreboard(options.getBoolean("pingScoreboard"));
            this.settings.setCpsScoreboard(options.getBoolean("cpsScoreboard") == null ? false : options.getBoolean("cpsScoreboard"));
            this.settings.setAllowTournamentMessages(options.getBoolean("allowTournamentMessages"));
            this.settings.setVanillaTab(options.getBoolean("usingVanillaTab"));

            Document kitStatistics=(Document) document.get("kitStatistics");

            for ( String key : kitStatistics.keySet() ) {
                Document kitDocument=(Document) kitStatistics.get(key);
                Kit kit=Kit.getByName(key);

                if (kit != null) {
                    StatisticsData statisticsData=new StatisticsData();
                    if (kitDocument.getInteger("elo") != null) {
                        statisticsData.setElo(kitDocument.getInteger("elo"));
                    } else {
                        kitDocument.put("elo", 0);
                    }
                    if (kitDocument.getInteger("won") != null) {
                        statisticsData.setWon(kitDocument.getInteger("won"));
                    } else {
                        kitDocument.put("won", 0);
                    }
                    if (kitDocument.getInteger("lost") != null) {
                        statisticsData.setLost(kitDocument.getInteger("lost"));
                    } else {
                        kitDocument.put("lost", 0);
                    }
                    this.statisticsData.put(kit, statisticsData);
                }
            }

            Document kitsDocument=(Document) document.get("loadouts");

            for ( String key : kitsDocument.keySet() ) {
                Kit kit=Kit.getByName(key);

                if (kit != null) {
                    JsonArray kitsArray=new JsonParser().parse(kitsDocument.getString(key)).getAsJsonArray();
                    KitInventory[] loadouts=new KitInventory[4];

                    for ( JsonElement kitElement : kitsArray ) {
                        JsonObject kitObject=kitElement.getAsJsonObject();

                        KitInventory loadout=new KitInventory(kitObject.get("name").getAsString());
                        loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                        loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                        loadouts[kitObject.get("index").getAsInt()]=loadout;
                    }

                    statisticsData.get(kit).setLoadouts(loadouts);
                }
            }
        } catch(Exception e) {
            this.save();
        }
        });
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

            if (clan != null) {
                document.put("clan", clan.getName());
            }

            Document optionsDocument = new Document();
            optionsDocument.put("showScoreboard", settings.isShowScoreboard());
            optionsDocument.put("allowSpectators", settings.isAllowSpectators());
            optionsDocument.put("receiveDuelRequests", settings.isReceiveDuelRequests());
            optionsDocument.put("usingPingFactor", settings.isUsingPingFactor());
            optionsDocument.put("toggleLightning", settings.isLightning());
            optionsDocument.put("pingScoreboard", settings.isPingScoreboard());
            optionsDocument.put("allowTournamentMessages", settings.isAllowTournamentMessages());
            optionsDocument.put("usingVanillaTab", settings.isVanillaTab());
            optionsDocument.put("cpsScoreboard", settings.isCpsScoreboard());

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
    }

    /**
     * Update the profile's elo in the mongo
     */
    public void updateElo() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();
        Document kitStatistics = (Document) document.get("kitStatistics");

        for (Kit kit : Kit.getKits()) {
            Document kitDocument = (Document) kitStatistics.get(kit.getName());
            this.getStatisticsData().get(kit).setElo(kitDocument.getInteger("elo"));
        }
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
        } else {
            return false;
        }
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
        } else {
            return true;
        }
    }

    /**
     * Execute join tasks for the profile
     */
    public void handleJoin() {
        Player player = getPlayer();

        for ( Player players : getPlayerList() ) {
            if (players.getName().equalsIgnoreCase(player.getName())) {
                getPlayerList().remove(players);
            }
        }
        Profile.getPlayerList().add(player);
        for ( Profile other : Profile.getProfiles().values() ) {
            other.handleVisibility();
        }

        Array.getInstance().getTaskThread().execute(() -> {
            if (!ArrayCache.getPlayerCache().containsKey(player.getName())) {
                ArrayCache.getPlayerCache().put(player.getName(), this.getUuid());
            }
            if (!ArrayCache.getNameCache().containsKey(player.getUniqueId())) {
                ArrayCache.getNameCache().put(this.getUuid(), player.getName());
            }
            this.setName(player.getName());
            this.refreshHotbar();
        });

        this.teleportToSpawn();

        //Visibility Bug Fix
        new BukkitRunnable() {
            @Override
            public void run() {
                handleVisibility();
            }
        }.runTaskLater(Array.getInstance(), 5L);
    }

    /**
     * Execute leave tasks for the profile
     */
    public void handleLeave() {
        Array.getInstance().getTaskThread().execute(() -> {
            //Remove from the Tab PlayerList
            Profile.getPlayerList().remove(this.getPlayer());

            //If they are in a match, remove them
            if (this.getMatch() != null) {
                this.getMatch().handleDeath(this.getPlayer(), null, true);
            }

            //If they are in a queue, remove them
            if (this.isInQueue()) {
                this.getQueue().removePlayer(this.getQueueProfile());
            }
            //Save their Profile
            this.save();

            //If there is a rematch request pending for them, remove it
            if (this.getRematchData() != null) {
                Player target = Array.getInstance().getServer().getPlayer(this.getRematchData().getTarget());
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
     * Teleport the profile to spawn and update
     * their nametag color
     */
    public void teleportToSpawn() {
        SpawnTeleportEvent event = new SpawnTeleportEvent(getPlayer(), Array.getInstance().getEssentials().getSpawn());
        event.call();

        //Update their visibility
        this.handleVisibility();

        if (!event.isCancelled() && event.getLocation() != null) {
            getPlayer().teleport(event.getLocation());
        }

        //Handle their nametag and update it accordingly
        for ( Player otherPlayer : Bukkit.getOnlinePlayers() ) {
            if (Array.getInstance().getEssentials().getNametagMeta().isEnabled()) {
                if (party == null) {
                    NameTags.color(getPlayer(), otherPlayer, getColor(), false);
                } else {
                    NameTags.color(getPlayer(), otherPlayer, Array.getInstance().getEssentials().nametagMeta.getPartyColor(), false);
                }
                Profile profile = Profile.getByPlayer(otherPlayer);

                if (profile.getParty() == null) {
                    NameTags.color(otherPlayer, getPlayer(), profile.getColor(), false);
                } else {
                    NameTags.color(otherPlayer, getPlayer(), Array.getInstance().getEssentials().nametagMeta.getPartyColor(), false);
                }
            } else {
                NameTags.reset(getPlayer(), otherPlayer);
                NameTags.reset(otherPlayer, getPlayer());
            }
        }
    }

    /**
     * See if the profile's hotbar needs to be updated
     */
    public void checkForHotbarUpdate() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        if (isInLobby() && !kitEditor.isActive()) {
            boolean update = false;

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());

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
                        int requestSlot = player.getInventory().first(Hotbar.getItems().get(HotbarType.REMATCH_REQUEST));

                        if (requestSlot != -1) {
                            update = true;
                        }
                    }
                }
            }
                    boolean activeEvent = (Array.getInstance().getSumoManager().getActiveSumo() != null && Array.getInstance().getSumoManager().getActiveSumo().isWaiting())
                            || (Array.getInstance().getBracketsManager().getActiveBrackets() != null && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting())
                            || (Array.getInstance().getLMSManager().getActiveLMS() != null && Array.getInstance().getLMSManager().getActiveLMS().isWaiting())
                            || (Array.getInstance().getParkourManager().getActiveParkour() != null && Array.getInstance().getParkourManager().getActiveParkour().isWaiting())
                            || (Array.getInstance().getGulagManager().getActiveGulag() != null && Array.getInstance().getGulagManager().getActiveGulag().isWaiting())
                            || (Array.getInstance().getOITCManager().getActiveOITC() != null && Array.getInstance().getOITCManager().getActiveOITC().isWaiting())
                            || (Array.getInstance().getSpleefManager().getActiveSpleef() != null && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting());
                    int eventSlot=player.getInventory().first(Hotbar.getItems().get(HotbarType.EVENT_JOIN));

                    if (eventSlot == -1 && activeEvent) {
                        update = true;
                    } else if (eventSlot != -1 && !activeEvent) {
                        update = true;
                    }


            if (update) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        refreshHotbar();
                    }
                }.runTask(Array.getInstance());
            }
        }
    }

    /**
     * Update the profile's hotbar
     */
    public void refreshHotbar() {
        Player player = getPlayer();

        if (player != null) {
            PlayerUtil.reset(player, false);

            if (isInLobby()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LOBBY, this));
            } else if (isInQueue()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.QUEUE, this));
            } else if (isSpectating()) {
                PlayerUtil.spectator(player);
                TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this)), 2L);
            } else if (isInSumo()) {
                if (getSumo().getEventPlayer(player).getState().equals(SumoPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this));
            } else if (isInBrackets()) {
                if (getBrackets().getEventPlayer(player).getState().equals(BracketsPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.BRACKETS_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.BRACKETS_SPECTATE, this));
            } else if (isInLMS()) {
                if (getLms().getEventPlayer(player).getState().equals(LMSPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LMS_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LMS_SPECTATE, this));
            } else if (isInParkour()) {
                if (getParkour().getEventPlayer(player).getState().equals(ParkourPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.PARKOUR_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.PARKOUR_SPECTATE, this));
            } else if (isInSpleef()) {
                if (getSpleef().getEventPlayer(player).getState().equals(SpleefPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this));
            } else if (isInOITC()) {
                if (getOITC().getEventPlayer(player).getState().equals(OITCPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.OITC_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.OITC_SPECTATE, this));
            } else if(isInGulag()) {
                if (getGulag().getEventPlayer(player).getState().equals(GulagPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                    TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.GULAG_SPECTATE, this)), 2L);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.GULAG_SPECTATE, this));
            } else if (isInFight()) {
                if (!match.getTeamPlayer(player).isAlive()) {
                    player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
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
                hide=false;
            }
            if (party != null && party.containsPlayer(otherPlayer)) {
                hide = false;
            }
        } else if (isInFight()) {
            TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);

            if (teamPlayer != null && teamPlayer.isAlive()) {
                hide = false;
            }
        } else if (isSpectating()) {
            if (sumo != null) {
                SumoPlayer sumoPlayer = sumo.getEventPlayer(otherPlayer);
                if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                    hide = false;
                }
            } else if (brackets != null) {
                BracketsPlayer bracketsPlayer = brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (gulag != null) {
                GulagPlayer gulagPlayer= gulag.getEventPlayer(otherPlayer);
                if (gulagPlayer != null && gulagPlayer.getState() == GulagPlayerState.WAITING) {
                    hide = false;
                }
            } else if (lms != null) {
                LMSPlayer LMSPlayer = lms.getEventPlayer(otherPlayer);
                if (LMSPlayer != null && LMSPlayer.getState() == LMSPlayerState.WAITING) {
                    hide = false;
                }
            } else if (parkour != null) {
                ParkourPlayer parkourPlayer = parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide = false;
                }
            } else if (spleef != null) {
                SpleefPlayer spleefPlayer = spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide = false;
                }
            } else {
                TeamPlayer teamPlayer = match.getTeamPlayer(otherPlayer);
                if (teamPlayer != null && teamPlayer.isAlive()) {
                    hide = false;
                }
            }
        } else if (isInEvent()) {
            if (sumo != null) {
                if (!sumo.getSpectators().contains(otherPlayer.getUniqueId())) {
                    SumoPlayer sumoPlayer = sumo.getEventPlayer(otherPlayer);
                    if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                        hide = false;
                    }
                }
            } else if (brackets != null) {
                BracketsPlayer bracketsPlayer = brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (gulag != null) {
                GulagPlayer gulagPlayer= gulag.getEventPlayer(otherPlayer);
                if (gulagPlayer != null && gulagPlayer.getState() == GulagPlayerState.WAITING) {
                    hide = false;
                }
            } else if (lms != null) {
                LMSPlayer LMSPlayer = lms.getEventPlayer(otherPlayer);
                if (LMSPlayer != null && LMSPlayer.getState() == LMSPlayerState.WAITING) {
                    hide = false;
                }
            } else if (parkour != null) {
                ParkourPlayer parkourPlayer = parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide = false;
                }
            } else if (spleef != null) {
                SpleefPlayer spleefPlayer = spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide = false;
                }
            }
        }

        if (hide) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.hidePlayer(otherPlayer);
                }
            }.runTask(Array.getInstance());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.showPlayer(otherPlayer);
                }
            }.runTask(Array.getInstance());
        }
    }

    /**
     * More simpler and direct method to access
     * and update profile's visibility
     */
    public void handleVisibility() {
        Player player = getPlayer();
        if (player != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        handleVisibility(player, otherPlayer);
                    }
                }
            }.runTaskAsynchronously(Array.getInstance());
        }
    }

    /**
     * Set the profile's knockback profile to
     * the specified knockback profile
     *
     * @param player The player whose knockback is being changed
     * @param kb The knockback profile name in {@link String} format
     */
    public static void setKb(Player player, String kb) {
       TaskUtil.runAsync(() -> Array.getInstance().getNMSManager().getKnockbackType().applyKnockback(player, kb));
    }

    /**
     * Apply the Enderpearl cooldown to the profile
     * and send it to LunarAPI if they are on Lunar
     *
     * @param cooldown {@link Cooldown}
     */
    public void setEnderpearlCooldown(Cooldown cooldown) {
        this.enderpearlCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                LunarClientAPICooldown.registerCooldown(new LCCooldown("EnderPearl", Integer.parseInt(cooldown.getTimeLeft()), TimeUnit.MILLISECONDS, Material.ENDER_PEARL));
                LunarClientAPICooldown.sendCooldown(player, "Enderpearl");
            }
        } catch (Exception e) {
            Array.logger("&cCould not send LC-Cooldown!");
        }
    }

    /**
     * Apply the Bow cooldown to the profile
     * and send it to LunarAPI if they are on Lunar
     *
     * @param cooldown {@link Cooldown}
     */
    public void setBowCooldown(Cooldown cooldown) {
        this.bowCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                LunarClientAPICooldown.registerCooldown(new LCCooldown("Bow", Integer.parseInt(cooldown.getTimeLeft()), TimeUnit.MILLISECONDS, Material.BOW));
                LunarClientAPICooldown.sendCooldown(player, "Bow");
            }
        } catch (Exception e) {
            Array.logger("&cCould not send LC-Cooldown!");
        }
    }

    public String getEloLeague() {
        return Array.getInstance().getDivisionsManager().getDivision(this);
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
        return state == ProfileState.SPECTATING && (
                match != null || sumo != null ||
                        brackets != null || lms != null ||
                        parkour != null || gulag !=null ||
                        OITC !=null || spleef != null);
    }

    public boolean isInEvent() {
        return state == ProfileState.IN_EVENT;
    }

    public boolean isInTournament() {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            return Tournament.CURRENT_TOURNAMENT.isParticipating(getPlayer());
        } else {
            return false;
        }
    }

    public boolean isInSumo() {
        return state == ProfileState.IN_EVENT && sumo != null;
    }

    public boolean isInBrackets() {
        return state == ProfileState.IN_EVENT && brackets != null;
    }

    public boolean isInLMS() {
        return state == ProfileState.IN_EVENT && lms != null;
    }

    public boolean isInParkour() {
        return state == ProfileState.IN_EVENT && parkour != null;
    }

    public boolean isInSpleef() {
        return state == ProfileState.IN_EVENT && spleef != null;
    }

    public boolean isInGulag() {
        return state == ProfileState.IN_EVENT && gulag !=null;
    }

    public boolean isInOITC() {
        return state == ProfileState.IN_EVENT && OITC != null;
    }

    public boolean isInSomeSortOfFight() {
        return (state == ProfileState.IN_FIGHT && match != null) || (state == ProfileState.IN_EVENT);
    }

    public boolean isBusy() {
        return isInQueue() || isInFight() || isInEvent() || isSpectating() || isInTournament();
    }
}

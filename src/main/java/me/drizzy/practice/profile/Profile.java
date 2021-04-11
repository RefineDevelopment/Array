package me.drizzy.practice.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.duel.DuelProcedure;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.enums.HotbarType;
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
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueProfile;
import me.drizzy.practice.settings.meta.SettingsMeta;
import me.drizzy.practice.statistics.LeaderboardsAdapter;
import me.drizzy.practice.statistics.StatisticsData;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.TaskUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.nametag.NameTags;
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
public class Profile {

    @Getter private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter static final List<Player> playerList = new ArrayList<>();
    @Getter private static final List<LeaderboardsAdapter> globalEloLeaderboards = new ArrayList<>();
    @Getter private static MongoCollection<Document> collection;
    @Setter String name;
    @Setter int globalElo = 1000;
    @Setter int bridgeRounds = 0;
    @Setter int sumoRounds = 0;
    @Setter private ProfileState state;
    @Setter private Party party;
    @Setter private Match match;
    @Setter private Sumo sumo;
    @Setter private Brackets brackets;
    @Setter private LMS lms;
    @Setter private Parkour parkour;
    @Setter private Gulag gulag;
    @Setter private OITC OITC;
    @Setter private Spleef spleef;
    @Setter private Queue queue;
    @Setter private QueueProfile queueProfile;
    @Setter private String hcfClass = "HCFDIAMOND";
    @Setter private DuelProcedure duelProcedure;
    @Setter private ProfileRematchData rematchData;
    @Setter private Player lastMessager;
    @Setter private boolean followMode = false;
    @Setter private boolean visibility = false;
    @Setter private Player following;
    @Setter private long lastRunVisibility = 0L;
    @Setter private List<Player> follower = new ArrayList<>();
    @Setter private Player spectating;
    private final UUID uuid;
    private Cooldown enderpearlCooldown = new Cooldown(0);
    private Cooldown bowCooldown = new Cooldown(0);
    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    private final SettingsMeta settings = new SettingsMeta();
    private final KitEditor kitEditor = new KitEditor();
    private final Map<Kit, StatisticsData> statisticsData= new LinkedHashMap<>();
    private final List<Location> plates = new ArrayList<>();
    public static Executor mongoThread = Array.getInstance().getMongoThread();

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.IN_LOBBY;

        for (Kit kit : Kit.getKits()) {
            this.statisticsData.put(kit, new StatisticsData());
        }
        this.calculateGlobalElo();
    }

    public static void preload() {
        collection = Array.getInstance().getMongoDatabase().getCollection("profiles");

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
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.save();
                }
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 36000L, 36000L);

        // Load all players from database
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.getProfiles().values().forEach(Profile::load);
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 600L, 600L);

        // Reload global elo leaderboards
        new BukkitRunnable() {
            @Override
            public void run() {
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

    public static Profile getByUuid(UUID uuid) {
        Profile profile = profiles.get(uuid);

        if (profile == null) {
            profile = new Profile(uuid);
        }

        return profile;
    }

    public static Profile getByUuid(Player player) {
        Profile profile = profiles.get(player.getUniqueId());

        if (profile == null) {
            profile = new Profile(player.getUniqueId());
        }

        return profile;
    }



    public static void loadGlobalLeaderboards() {
        if (!getGlobalEloLeaderboards().isEmpty()) getGlobalEloLeaderboards().clear();
            mongoThread.execute(() -> {
            for ( Document document : Profile.getCollection().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter=new LeaderboardsAdapter();
                leaderboardsAdapter.setName((String) document.get("name"));
                leaderboardsAdapter.setElo((Integer) document.get("globalElo"));
                getGlobalEloLeaderboards().add(leaderboardsAdapter);
            }
        });
    }

    public void load() {
        mongoThread.execute(() -> {
            Document document=collection.find(Filters.eq("uuid", uuid.toString())).first();

            if (document == null) {
                this.save();
                return;
            }

            this.globalElo=document.getInteger("globalElo");

            Document options=(Document) document.get("settings");

            this.settings.setShowScoreboard(options.getBoolean("showScoreboard"));
            this.settings.setAllowSpectators(options.getBoolean("allowSpectators"));
            this.settings.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
            this.settings.setUsingPingFactor(options.getBoolean("usingPingFactor"));
            this.settings.setLightning(options.getBoolean("toggleLightning"));
            this.settings.setPingScoreboard(options.getBoolean("pingScoreboard"));
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
        });
    }

    public void save() {
        mongoThread.execute(() -> {
            Document document=new Document();
            document.put("uuid", uuid.toString());
            document.put("name", Bukkit.getOfflinePlayer(uuid).getName());
            document.put("globalElo", globalElo);

            Document optionsDocument=new Document();
            optionsDocument.put("showScoreboard", settings.isShowScoreboard());
            optionsDocument.put("allowSpectators", settings.isAllowSpectators());
            optionsDocument.put("receiveDuelRequests", settings.isReceiveDuelRequests());
            optionsDocument.put("usingPingFactor", settings.isUsingPingFactor());
            optionsDocument.put("toggleLightning", settings.isLightning());
            optionsDocument.put("pingScoreboard", settings.isPingScoreboard());
            optionsDocument.put("allowTournamentMessages", settings.isAllowTournamentMessages());
            optionsDocument.put("usingVanillaTab", settings.isVanillaTab());
            document.put("settings", optionsDocument);

            Document kitStatisticsDocument=new Document();

            for ( Map.Entry<Kit, StatisticsData> entry : statisticsData.entrySet() ) {
                Document kitDocument=new Document();
                kitDocument.put("elo", entry.getValue().getElo());
                kitDocument.put("won", entry.getValue().getWon());
                kitDocument.put("lost", entry.getValue().getLost());
                kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
            }
            document.put("kitStatistics", kitStatisticsDocument);

            Document kitsDocument=new Document();

            for ( Map.Entry<Kit, StatisticsData> entry : statisticsData.entrySet() ) {
                JsonArray kitsArray=new JsonArray();

                for ( int i=0; i < 4; i++ ) {
                    KitInventory loadout=entry.getValue().getLoadout(i);

                    if (loadout != null) {
                        JsonObject kitObject=new JsonObject();
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

    public void updateElo() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();
        Document kitStatistics = (Document) document.get("kitStatistics");

        for (Kit kit : Kit.getKits()) {
            Document kitDocument = (Document) kitStatistics.get(kit.getName());
            this.getStatisticsData().get(kit).setElo(kitDocument.getInteger("elo"));
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

    public void handleJoin() {
        Profile.getPlayerList().add(this.getPlayer());
        for ( Profile other : Profile.getProfiles().values() ) {
            other.handleVisibility();
        }

        Array.getInstance().getTaskThread().execute(() -> {
            if (!ArrayCache.getPlayerCache().containsKey(this.getPlayer().getName())) {
                ArrayCache.getPlayerCache().put(this.getPlayer().getName(), this.getUuid());
            }
            try {
                this.load();
            } catch (Exception e) {
                e.printStackTrace();
                this.getPlayer().kickPlayer(CC.AQUA + "Failed to load your profile, Please contact an Administrator!");
                return;
            }
            Profile.getProfiles().put(this.getPlayer().getUniqueId(), this);
            this.setName(this.getPlayer().getName());
            this.refreshHotbar();
        });

        Essentials.teleportToSpawn(this.getPlayer());

        //Visibility Bug Fix :)
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.this.handleVisibility();
            }
        }.runTaskLater(Array.getInstance(), 5L);

        for ( Player ps : Bukkit.getOnlinePlayers() ) {
            NameTags.color(this.getPlayer(), ps, ChatColor.GREEN, false);
            if (!Profile.getByUuid(ps).isBusy(ps) && !Profile.getByUuid(ps).isInSomeSortOfFight()) {
                if (Profile.getByUuid(ps).getState() == ProfileState.IN_LOBBY || Profile.getByUuid(ps).getState() == ProfileState.IN_QUEUE)
                    NameTags.color(ps, getPlayer(), ChatColor.GREEN, false);
            }
        }
    }

    public void handleLeave() {
        Array.getInstance().getTaskThread().execute(() -> {
            Profile.getPlayerList().remove(this.getPlayer());
            if (this.getMatch() != null) {
                if (this.getMatch().isSoloMatch() || this.getMatch().isSumoMatch() || this.getMatch().isTheBridgeMatch()) {
                    this.getMatch().handleDeath(this.getPlayer(), null, true);
                } else {
                    this.getMatch().handleDeath(this.getPlayer(), null, true);
                }
            }
            if (this.isInQueue()) {
                this.getQueue().removePlayer(this.getQueueProfile());
            }
            this.save();
            if (this.getRematchData() != null) {
                Player target = Array.getInstance().getServer().getPlayer(this.getRematchData().getTarget());
                if (target != null && target.isOnline()) {
                    Profile.getByUuid(target.getUniqueId()).checkForHotbarUpdate();
                }
            }
            if (this.getParty() !=null && Tournament.CURRENT_TOURNAMENT !=null && Tournament.CURRENT_TOURNAMENT.isParticipating(this.getPlayer())) {
                Tournament.CURRENT_TOURNAMENT.leave(this.getParty());
            }
        });
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
        return state == ProfileState.SPECTATE_MATCH && (
                match != null || sumo != null ||
                brackets != null || lms != null ||
                parkour != null || gulag !=null ||
                OITC !=null || spleef != null);
    }

    public boolean isInEvent() {
        return state == ProfileState.IN_EVENT;
    }

    public boolean isInTournament(Player player) {
        if (Tournament.CURRENT_TOURNAMENT != null) {
            return Tournament.CURRENT_TOURNAMENT.isParticipating(player);
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

    public boolean isBusy(Player player) {
        return isInQueue() || isInFight() || isInEvent() || isSpectating() || isInTournament(player) || isFollowMode();
    }

    public void checkForHotbarUpdate() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        if (isInLobby() && !kitEditor.isActive()) {
            boolean update = false;

            if (this.rematchData != null) {
                final Player target=Bukkit.getPlayer(this.rematchData.getTarget());
                if (System.currentTimeMillis() - this.rematchData.getTimestamp() >= 30000L) {
                    this.rematchData=null;
                    update=true;
                } else if (target == null || !target.isOnline()) {
                    this.rematchData=null;
                    update=true;
                } else {
                    final Profile profile=getByUuid(target.getUniqueId());
                    if (!profile.isInLobby() && !profile.isInQueue()) {
                        this.rematchData=null;
                        update=true;
                    } else if (this.getRematchData() == null) {
                        this.rematchData=null;
                        update=true;
                    } else if (!this.rematchData.getKey().equals(this.getRematchData().getKey())) {
                        this.rematchData=null;
                        update=true;
                    } else if (this.rematchData.isReceive()) {
                        int requestSlot=player.getInventory().first(Hotbar.getItems().get(HotbarType.REMATCH_ACCEPT));

                        if (requestSlot != -1) {
                            update=true;
                        }
                    }
                }

                {
                    boolean activeEvent=(Array.getInstance().getSumoManager().getActiveSumo() != null && Array.getInstance().getSumoManager().getActiveSumo().isWaiting())
                            || (Array.getInstance().getBracketsManager().getActiveBrackets() != null && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting())
                            || (Array.getInstance().getLMSManager().getActiveLMS() != null && Array.getInstance().getLMSManager().getActiveLMS().isWaiting())
                            || (Array.getInstance().getParkourManager().getActiveParkour() != null && Array.getInstance().getParkourManager().getActiveParkour().isWaiting())
                            || (Array.getInstance().getGulagManager().getActiveGulag() != null && Array.getInstance().getGulagManager().getActiveGulag().isWaiting())
                            || (Array.getInstance().getOITCManager().getActiveOITC() != null && Array.getInstance().getOITCManager().getActiveOITC().isWaiting())
                            || (Array.getInstance().getSpleefManager().getActiveSpleef() != null && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting());
                    int eventSlot=player.getInventory().first(Hotbar.getItems().get(HotbarType.EVENT_JOIN));

                    if (eventSlot == -1 && activeEvent) {
                        update=true;
                    } else if (eventSlot != -1 && !activeEvent) {
                        update=true;
                    }
                }
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
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this));
            } else if (isInBrackets()) {
                if (getBrackets().getEventPlayer(player).getState().equals(BracketsPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.BRACKETS_SPECTATE, this));
            } else if (isInLMS()) {
                if (getLms().getEventPlayer(player).getState().equals(LMSPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LMS_SPECTATE, this));
            } else if (isInParkour()) {
                if (getParkour().getEventPlayer(player).getState().equals(ParkourPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.PARKOUR_SPECTATE, this));
            } else if (isInSpleef()) {
                if (getSpleef().getEventPlayer(player).getState().equals(SpleefPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this));
            } else if (isInOITC()) {
                if (getOITC().getEventPlayer(player).getState().equals(OITCPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.OITC_SPECTATE, this));
            } else if(isInGulag()) {
                if (getGulag().getEventPlayer(player).getState().equals(GulagPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
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

    public String getWLR() {
        double totalWins = this.getTotalWins();
        double totalLosses = this.getTotalLost();

        double ratio = totalWins / Math.max(totalLosses, 1);
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(ratio);
    }

    public void handleVisibility(Player player, Player otherPlayer) {
        if (player == null || otherPlayer == null) return;
        boolean hide = true;
        if (state == ProfileState.IN_LOBBY || state == ProfileState.IN_QUEUE) {
            if (this.getSettings().isShowPlayers()) {
                hide=false;
            }
            if (party != null && party.containsPlayer(otherPlayer)) {
                hide = false;
                NameTags.color(player, otherPlayer, ChatColor.BLUE, false);
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

    public static void setKb(Player player, String kb) {
       TaskUtil.runAsync(() -> Array.getInstance().getKnockbackManager().getKnockbackType().applyKnockback(player, kb));
    }

    public void setEnderpearlCooldown(Cooldown cooldown) {
        this.enderpearlCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                LunarClientAPICooldown.registerCooldown(new LCCooldown("EnderPearl", Integer.parseInt(cooldown.getTimeLeft()), TimeUnit.MILLISECONDS, Material.ENDER_PEARL));
                LunarClientAPICooldown.sendCooldown(player, "Enderpearl");
            }
        } catch (Exception e) {
            Array.logger("Could not send LC-Cooldown!");
        }
    }

    public void setBowCooldown(Cooldown cooldown) {
        this.bowCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                LunarClientAPICooldown.registerCooldown(new LCCooldown("Bow", Integer.parseInt(cooldown.getTimeLeft()), TimeUnit.MILLISECONDS, Material.BOW));
                LunarClientAPICooldown.sendCooldown(player, "Bow");
            }
        } catch (Exception e) {
            Array.logger("Could not send LC-Cooldown!");
        }


    }

}

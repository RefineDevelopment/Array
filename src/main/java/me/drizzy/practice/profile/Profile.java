package me.drizzy.practice.profile;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCCooldown;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.duel.DuelProcedure;
import me.drizzy.practice.duel.DuelRequest;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.brackets.player.BracketsPlayer;
import me.drizzy.practice.event.types.brackets.player.BracketsPlayerState;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.lms.player.LMSPlayer;
import me.drizzy.practice.event.types.lms.player.LMSPlayerState;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.parkour.player.ParkourPlayer;
import me.drizzy.practice.event.types.parkour.player.ParkourPlayerState;
import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.event.types.skywars.player.SkyWarsPlayer;
import me.drizzy.practice.event.types.skywars.player.SkyWarsPlayerState;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.spleef.player.SpleefPlayer;
import me.drizzy.practice.event.types.spleef.player.SpleefPlayerState;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.event.types.sumo.player.SumoPlayer;
import me.drizzy.practice.event.types.sumo.player.SumoPlayerState;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitLeaderboards;
import me.drizzy.practice.kit.KitLoadout;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarItem;
import me.drizzy.practice.profile.hotbar.HotbarLayout;
import me.drizzy.practice.profile.meta.ProfileKitEditor;
import me.drizzy.practice.profile.meta.ProfileMatchHistory;
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueProfile;
import me.drizzy.practice.settings.meta.ProfileOptions;
import me.drizzy.practice.statistics.ProfileKitData;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.nametag.NameTags;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profile {
    @Getter
    private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter
    static final List<Player> playerList = new ArrayList<>();
    @Getter
    private static final List<KitLeaderboards> globalEloLeaderboards = new ArrayList<>();
    @Getter
    private static Map<Integer, String> eloLeagues = new HashMap<>();
    @Getter
    private static final Map<String, UUID> playerCache = new HashMap<>();
    @Getter
    private static MongoCollection<Document> allProfiles;
    private static MongoCollection<Document> collection;
    @Getter
    private final ProfileOptions settings = new ProfileOptions();
    @Getter
    private final ProfileKitEditor kitEditor = new ProfileKitEditor();
    @Getter
    private final Map<Kit, ProfileKitData> kitData = new LinkedHashMap<>();
    @Getter
    @Setter
    String name;
    @Getter
    @Setter
    int globalElo = 1000;
    @Getter
    @Setter
    int sumoRounds = 0;
    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private ProfileState state;
    @Getter
    @Setter
    private Party party;
    @Getter
    @Setter
    private Match match;
    @Getter
    @Setter
    private Sumo sumo;
    @Getter
    @Setter
    private Brackets brackets;
    @Getter
    @Setter
    private LMS lms;
    @Getter
    @Setter
    private Parkour parkour;
    @Getter
    @Setter
    private SkyWars skyWars;
    @Getter
    @Setter
    private Spleef spleef;
    @Getter
    @Setter
    private Queue queue;
    @Getter
    @Setter
    private QueueProfile queueProfile;
    @Getter
    @Setter
    private Cooldown enderpearlCooldown = new Cooldown(0);
    @Getter
    private final Map<UUID, DuelRequest> sentDuelRequests = new HashMap<>();
    @Getter
    @Setter
    private DuelProcedure duelProcedure;
    @Getter
    @Setter
    private ProfileRematchData rematchData;
    @Getter
    @Setter
    private Player lastMessager;
    @Getter
    @Setter
    private boolean silent = false;
    @Getter
    @Setter
    private boolean followMode = false;
    @Getter
    @Setter
    private boolean visibility = false;
    @Getter private final List<Location> plates = new ArrayList<>();
    @Getter
    @Setter
    private Player following;
    @Getter
    @Setter
    private long lastRunVisibility = 0L;
    @Getter
    @Setter
    private List<Player> follower = new ArrayList<>();
    @Getter
    @Setter
    private Player spectating;
    @Getter
    private List<ProfileMatchHistory> matchHistory = new ArrayList<>();

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.state = ProfileState.IN_LOBBY;

        for (Kit kit : Kit.getKits()) {
            this.kitData.put(kit, new ProfileKitData());
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
        getEloLeagues().put(1019, "Diamond 3");
        getEloLeagues().put(1018, "Diamond 3");
        getEloLeagues().put(1017, "Diamond 2");
        getEloLeagues().put(1016, "Diamond 2");
        getEloLeagues().put(1015, "Diamond 1");
        getEloLeagues().put(1014, "Diamond 1");
        getEloLeagues().put(1013, "Gold 3");
        getEloLeagues().put(1012, "Gold 3");
        getEloLeagues().put(1011, "Gold 2");
        getEloLeagues().put(1010, "Gold 2");
        getEloLeagues().put(1009, "Gold 1");
        getEloLeagues().put(1008, "Gold 1");
        getEloLeagues().put(1007, "Silver 4");
        getEloLeagues().put(1006, "Silver 4");
        getEloLeagues().put(1005, "Silver 3");
        getEloLeagues().put(1004, "Silver 3");
        getEloLeagues().put(1003, "Silver 2");
        getEloLeagues().put(1002, "Silver 2");
        getEloLeagues().put(1001, "Silver 1");
        getEloLeagues().put(1000, "Silver 1");
        getEloLeagues().put(999, "Bronze 5");
        getEloLeagues().put(998, "Bronze 5");
        getEloLeagues().put(997, "Bronze 4");
        getEloLeagues().put(996, "Bronze 4");
        getEloLeagues().put(995, "Bronze 3");
        getEloLeagues().put(994, "Bronze 3");
        getEloLeagues().put(993, "Bronze 2");
        getEloLeagues().put(992, "Bronze 2");
        getEloLeagues().put(991, "Bronze 1");
        getEloLeagues().put(990, "Bronze");
        eloLeagues = eloLeagues.entrySet().stream()
                .sorted(Map.Entry.<Integer, String>comparingByKey().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

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
        Profile.loadAllProfiles();
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.loadAllProfiles();
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 600L, 600L);

        new BukkitRunnable() {
            @Override
            public void run() {
            Bukkit.broadcastMessage(CC.translate("&8[&b&oWarning&8] &7Updating Leaderboards, this might cause some lag..."));
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 6000L, 6000L);

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
        }.runTaskTimerAsynchronously(Array.getInstance(), 60L, 60L);
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

    public static void loadAllProfiles() {
        allProfiles = Array.getInstance().getMongoDatabase().getCollection("profiles");
    }

    public static void loadGlobalLeaderboards() {
        if (!getGlobalEloLeaderboards().isEmpty()) getGlobalEloLeaderboards().clear();
        for (Document document : Profile.getAllProfiles().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>())) {
            KitLeaderboards kitLeaderboards = new KitLeaderboards();
            kitLeaderboards.setName((String) document.get("name"));
            kitLeaderboards.setElo((Integer) document.get("globalElo"));
            getGlobalEloLeaderboards().add(kitLeaderboards);
        }
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            this.save();
            return;
        }

        this.globalElo = document.getInteger("globalElo");

        Document options = (Document) document.get("settings");

        this.settings.setShowScoreboard(options.getBoolean("showScoreboard"));
        this.settings.setAllowSpectators(options.getBoolean("allowSpectators"));
        this.settings.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
        this.settings.setUsingPingFactor(options.getBoolean("usingPingFactor"));
        this.settings.setLightning(options.getBoolean("toggleLightning"));
        this.settings.setPingScoreboard(options.getBoolean("pingScoreboard"));
        this.settings.setAllowTournamentMessages(options.getBoolean("allowTournamentMessages"));
        this.settings.setVanillaTab(options.getBoolean("usingVanillaTab"));

        Document kitStatistics = (Document) document.get("kitStatistics");

        for (String key : kitStatistics.keySet()) {
            Document kitDocument = (Document) kitStatistics.get(key);
            Kit kit = Kit.getByName(key);

            if (kit != null) {
                ProfileKitData profileKitData = new ProfileKitData();
                profileKitData.setElo(kitDocument.getInteger("elo"));
                profileKitData.setWon(kitDocument.getInteger("unrankedWon"));
                profileKitData.setLost(kitDocument.getInteger("unrankedLost"));

                kitData.put(kit, profileKitData);
            }
        }

        Document kitsDocument = (Document) document.get("loadouts");

        for (String key : kitsDocument.keySet()) {
            Kit kit = Kit.getByName(key);

            if (kit != null) {
                JsonArray kitsArray = new JsonParser().parse(kitsDocument.getString(key)).getAsJsonArray();
                KitLoadout[] loadouts = new KitLoadout[4];

                for (JsonElement kitElement : kitsArray) {
                    JsonObject kitObject = kitElement.getAsJsonObject();

                    KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
                    loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                    loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                    loadouts[kitObject.get("index").getAsInt()] = loadout;
                }

                kitData.get(kit).setLoadouts(loadouts);
            }
        }

        this.matchHistory = Lists.newArrayList();

        if (document.containsKey("matchHistory")) {
            List<Document> matchHistoryDocument = (List<Document>) document.get("matchHistory");
            for (Document children : matchHistoryDocument) {
                try {
                    this.matchHistory.add(new ProfileMatchHistory(children));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", Bukkit.getOfflinePlayer(uuid).getName());
        document.put("globalElo", globalElo);

        Document optionsDocument = new Document();
        optionsDocument.put("showScoreboard", settings.isShowScoreboard());
        optionsDocument.put("allowSpectators", settings.isAllowSpectators());
        optionsDocument.put("receiveDuelRequests", settings.isReceiveDuelRequests());
        optionsDocument.put("usingPingFactor", settings.isUsingPingFactor());
        optionsDocument.put("toggleLightning", settings.isLightning());
        optionsDocument.put("pingScoreboard", settings.isPingScoreboard());
        optionsDocument.put("allowTournamentMessages", settings.isAllowTournamentMessages());
        optionsDocument.put("usingVanillaTab", settings.isVanillaTab());
        document.put("settings", optionsDocument);

        Document kitStatisticsDocument = new Document();

        for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
            Document kitDocument = new Document();
            kitDocument.put("elo", entry.getValue().getElo());
            kitDocument.put("unrankedWon", entry.getValue().getWon());
            kitDocument.put("unrankedLost", entry.getValue().getLost());
            kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
        }
        document.put("kitStatistics", kitStatisticsDocument);

        List<Document> matchHistoryDocument = Lists.newArrayList();
        for (ProfileMatchHistory history : matchHistory) {
            matchHistoryDocument.add(history.toDocument());
        }
        document.put("matchHistory", matchHistoryDocument);

        Document kitsDocument = new Document();

        for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
            JsonArray kitsArray = new JsonArray();

            for (int i = 0; i < 4; i++) {
                KitLoadout loadout = entry.getValue().getLoadout(i);

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
    }

    public void calculateGlobalElo() {
        int globalElo = 0;
        int kitCounter = 0;
        for (Kit kit : this.kitData.keySet()) {
            if (kit.getGameRules().isRanked()) {
                globalElo += this.kitData.get(kit).getElo();
                kitCounter++;
            }
        }
        this.globalElo = Math.round(globalElo / kitCounter);
    }

    public String getEloLeague() {
        String toReturn = "&8Bronze 1";
        for (Integer elo : getEloLeagues().keySet()) {
            if (this.globalElo >= elo) {
                toReturn = getEloLeagues().get(elo);
                break;
            }
        }
        if (this.globalElo >= 1020) toReturn = "&b&lPurge";
        return toReturn;
    }

    public Integer getTotalWins() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getWon).sum();
    }

    public Integer getTotalLost() {
        return this.kitData.values().stream().mapToInt(ProfileKitData::getLost).sum();
    }

    public void addMatchHistory(ProfileMatchHistory profileMatchHistory) {
        while (matchHistory.size() > 54) {
            matchHistory.remove(0);
        }
        while (matchHistory.size() > 53) {
            matchHistory.remove(0);
        }
        matchHistory.add(profileMatchHistory);
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
                parkour != null || skyWars != null ||
                spleef != null);
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

    public boolean isInSkyWars() {
        return state == ProfileState.IN_EVENT && skyWars != null;
    }

    public boolean isInSpleef() {
        return state == ProfileState.IN_EVENT && spleef != null;
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
                    }// else if (this.rematchData.isReceive()) {
                        ///int requestSlot = player.getInventory().first(Hotbar.getItems().get(HotbarItem.REMATCH_ACCEPT));

                        /*if (requestSlot != -1) {
                            update = true;
                        }*/
                }
            }

            {
                boolean activeEvent = (Array.getInstance().getSumoManager().getActiveSumo() != null && Array.getInstance().getSumoManager().getActiveSumo().isWaiting())
                        || (Array.getInstance().getBracketsManager().getActiveBrackets() != null && Array.getInstance().getBracketsManager().getActiveBrackets().isWaiting())
                        || (Array.getInstance().getLMSManager().getActiveLMS() != null && Array.getInstance().getLMSManager().getActiveLMS().isWaiting())
                        || (Array.getInstance().getParkourManager().getActiveParkour() != null && Array.getInstance().getParkourManager().getActiveParkour().isWaiting())
                        || (Array.getInstance().getSkyWarsManager().getActiveSkyWars() != null && Array.getInstance().getSkyWarsManager().getActiveSkyWars().isWaiting())
                        || (Array.getInstance().getSpleefManager().getActiveSpleef() != null && Array.getInstance().getSpleefManager().getActiveSpleef().isWaiting());
                int eventSlot = player.getInventory().first(Hotbar.getItems().get(HotbarItem.EVENT_JOIN));

                if (eventSlot == -1 && activeEvent) {
                    update = true;
                } else if (eventSlot != -1 && !activeEvent) {
                    update = true;
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
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
            } else if (isInSumo()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this));
            } else if (isInBrackets()) {
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
            } else if (isInSkyWars()) {
                if (getSkyWars().getEventPlayer(player).getState().equals(SkyWarsPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SKYWARS_SPECTATE, this));
            } else if (isInSpleef()) {
                if (getSpleef().getEventPlayer(player).getState().equals(SpleefPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this));
            } else if (isInFight()) {
                if (!match.getTeamPlayer(player).isAlive()) {
                    player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
                }
            }

            player.updateInventory();
        }
    }

    public void handleVisibility(Player player, Player otherPlayer) {
        if (player == null || otherPlayer == null) return;
        boolean hide = true;
        if (state == ProfileState.IN_LOBBY || state == ProfileState.IN_QUEUE) {
            if (this.getSettings().isShowPlayers()) {
                hide=false;
            } else {
                hide=true;
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
            } else if (skyWars != null) {
                SkyWarsPlayer skyWarsPlayer = skyWars.getEventPlayer(otherPlayer);
                if (skyWarsPlayer != null && skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
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
            } else if (skyWars != null) {
                SkyWarsPlayer skyWarsPlayer = skyWars.getEventPlayer(otherPlayer);
                if (skyWarsPlayer != null && skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
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
        KnockbackProfile knockbackProfile =KnockbackModule.INSTANCE.profiles.get(kb);
        ((CraftPlayer)player).getHandle().setKnockback(knockbackProfile);
    }

    public void setEnderpearlCooldown(Cooldown cooldown) {
        this.enderpearlCooldown = cooldown;

        try {
            final Player player = this.getPlayer();
            if (player != null) {
                LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("EnderPearl", cooldown.getDuration(), TimeUnit.MILLISECONDS, Material.ENDER_PEARL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UUID getUUID(String name) {
        UUID uuid = null;
        if (Profile.getPlayerCache().containsKey(name)) {
            uuid = Profile.getPlayerCache().get(name);
        }
        return uuid;
    }
}

package me.array.ArrayPractice.profile;

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
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.duel.DuelProcedure;
import me.array.ArrayPractice.duel.DuelRequest;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayer;
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayerState;
import me.array.ArrayPractice.event.impl.lms.LMS;
import me.array.ArrayPractice.event.impl.lms.player.LMSPlayer;
import me.array.ArrayPractice.event.impl.lms.player.LMSPlayerState;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayerState;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayer;
import me.array.ArrayPractice.event.impl.skywars.player.SkyWarsPlayerState;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayer;
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayerState;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayer;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayerState;
import me.array.ArrayPractice.tournament.Tournament;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.profile.hotbar.HotbarLayout;
import me.array.ArrayPractice.profile.meta.ProfileKitData;
import me.array.ArrayPractice.profile.meta.ProfileKitEditor;
import me.array.ArrayPractice.profile.meta.ProfileMatchHistory;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import me.array.ArrayPractice.profile.meta.essentials.PackStatus;
import me.array.ArrayPractice.profile.meta.essentials.ProfileEssentials;
import me.array.ArrayPractice.profile.meta.option.ProfileOptions;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueProfile;
import me.array.ArrayPractice.util.InventoryUtil;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.nametag.NameTags;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Profile {
    @Getter
    private static final Map<UUID, Profile> profiles = new HashMap<>();
    @Getter
    private static final List<KitLeaderboards> globalEloLeaderboards = new ArrayList<>();
    @Getter
    private static Map<Integer, String> eloLeagues = new HashMap<>();
    @Getter
    private static MongoCollection<Document> allProfiles;
    private static MongoCollection<Document> collection;
    @Getter
    private final ProfileEssentials essentials = new ProfileEssentials();
    @Getter
    private final ProfileOptions options = new ProfileOptions();
    @Getter
    private final ProfileKitEditor kitEditor = new ProfileKitEditor();
    @Getter
    private final Map<Kit, ProfileKitData> kitData = new LinkedHashMap<>();
    @Getter
    @Setter
    private PackStatus packStatus = PackStatus.DIAMOND;
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
    private boolean socialSpy = false;
    @Getter
    @Setter
    private boolean silent = false;
    @Getter
    @Setter
    private boolean followMode = false;
    @Getter
    @Setter
    private boolean visibility = false;
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

    public static void init() {
        collection = Practice.getInstance().getMongoDatabase().getCollection("profiles");

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

        getEloLeagues().put(1019, "&8[&bDiamond &c3&8]");
        getEloLeagues().put(1018, "&8[&bDiamond &c3&8]");
        getEloLeagues().put(1017, "&8[&bDiamond &c2&8]");
        getEloLeagues().put(1016, "&8[&bDiamond &c2&8]");
        getEloLeagues().put(1015, "&8[&bDiamond &c1&8]");
        getEloLeagues().put(1014, "&8[&bDiamond &c1&8]");
        getEloLeagues().put(1013, "&8[&6Gold &e3&8]");
        getEloLeagues().put(1012, "&8[&6Gold &e3&8]");
        getEloLeagues().put(1011, "&8[&6Gold &e2&8]");
        getEloLeagues().put(1010, "&8[&6Gold &e2&8]");
        getEloLeagues().put(1009, "&8[&6Gold &e1&8]");
        getEloLeagues().put(1008, "&8[&6Gold &e1&8]");
        getEloLeagues().put(1007, "&8[&7Silver 4&8]");
        getEloLeagues().put(1006, "&8[&7Silver 4&8]");
        getEloLeagues().put(1005, "&8[&7Silver 3&8]");
        getEloLeagues().put(1004, "&8[&7Silver 3&8]");
        getEloLeagues().put(1003, "&8[&7Silver 2&8]");
        getEloLeagues().put(1002, "&8[&7Silver 2&8]");
        getEloLeagues().put(1001, "&8[&7Silver 1&8]");
        getEloLeagues().put(1000, "&8[&7Silver 1&8]");
        getEloLeagues().put(999, "&7[&8Bronze 5&7]");
        getEloLeagues().put(998, "&7[&8Bronze 5&7]");
        getEloLeagues().put(997, "&7[&8Bronze 4&7]");
        getEloLeagues().put(996, "&7[&8Bronze 4&7]");
        getEloLeagues().put(995, "&7[&8Bronze 3&7]");
        getEloLeagues().put(994, "&7[&8Bronze 3&7]");
        getEloLeagues().put(993, "&7[&8Bronze 2&7]");
        getEloLeagues().put(992, "&7[&8Bronze 2&7]");
        getEloLeagues().put(991, "&7[&8Bronze 1&7]");
        getEloLeagues().put(990, "&7[&8Bronze &7]");
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
        }.runTaskTimerAsynchronously(Practice.getInstance(), 36000L, 36000L);

        // Load all players from database
        Profile.loadAllProfiles();
        new BukkitRunnable() {
            @Override
            public void run() {
                Profile.loadAllProfiles();
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 600L, 600L);

        new BukkitRunnable() {
            @Override
            public void run() {
            Bukkit.broadcastMessage(CC.translate("&8[&b&oWarning&8] &7Updating Leaderboards, this might cause some lag..."));
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 6000L, 6000L);

        // Reload global elo leaderboards
        new BukkitRunnable() {
            @Override
            public void run() {
                loadGlobalLeaderboards();
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 600L, 600L);

        // Refresh players' hotbars every 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.checkForHotbarUpdate();
                }
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 60L, 60L);
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
        allProfiles = Practice.getInstance().getMongoDatabase().getCollection("profiles");
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

        Document essentials = (Document) document.get("essentials");

        if (essentials == null) {
            Document essentialsDocument = new Document();
            essentialsDocument.put("nick", null);
            document.put("essentials", essentialsDocument);
        } else {
            this.essentials.setNick(essentials.getString("nick"));
        }

        Document options = (Document) document.get("options");

        this.options.setShowScoreboard(options.getBoolean("showScoreboard"));
        this.options.setAllowSpectators(options.getBoolean("allowSpectators"));
        this.options.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
        this.options.setUsingPingFactor(options.getBoolean("usingPingFactor"));
        this.options.setLightning(options.getBoolean("toggleLightning"));

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

        Document essentialsDocument = new Document();
        essentialsDocument.put("nick", essentials.getNick());
        document.put("essentials", essentialsDocument);

        Document optionsDocument = new Document();
        optionsDocument.put("showScoreboard", options.isShowScoreboard());
        optionsDocument.put("allowSpectators", options.isAllowSpectators());
        optionsDocument.put("receiveDuelRequests", options.isReceiveDuelRequests());
        optionsDocument.put("usingPingFactor", options.isUsingPingFactor());
        optionsDocument.put("toggleLightning", options.isLightning());
        document.put("options", optionsDocument);

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
        if (this.globalElo >= 1020) toReturn = "&b&lResolve";
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
                match != null ||
                        sumo != null ||
                        brackets != null ||
                        lms != null ||
                        parkour != null ||
                        skyWars != null ||
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
                    } else if (this.rematchData.isReceive()) {
                        final int requestSlot=player.getInventory().first(Hotbar.getItems().get(HotbarItem.REMATCH_REQUEST));
                        if (requestSlot != -1) {
                            update=true;
                        }
                    }
                }
            }

            {
                boolean activeEvent = (Practice.getInstance().getSumoManager().getActiveSumo() != null && Practice.getInstance().getSumoManager().getActiveSumo().isWaiting())
                        || (Practice.getInstance().getBracketsManager().getActiveBrackets() != null && Practice.getInstance().getBracketsManager().getActiveBrackets().isWaiting())
                        || (Practice.getInstance().getLMSManager().getActiveLMS() != null && Practice.getInstance().getLMSManager().getActiveLMS().isWaiting())
                        || (Practice.getInstance().getParkourManager().getActiveParkour() != null && Practice.getInstance().getParkourManager().getActiveParkour().isWaiting())
                        || (Practice.getInstance().getSkyWarsManager().getActiveSkyWars() != null && Practice.getInstance().getSkyWarsManager().getActiveSkyWars().isWaiting())
                        || (Practice.getInstance().getSpleefManager().getActiveSpleef() != null && Practice.getInstance().getSpleefManager().getActiveSpleef().isWaiting());
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
                }.runTask(Practice.getInstance());
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
        if (player == null || otherPlayer == null) {
            return;
        }
        boolean hide=true;
        if (this.state == ProfileState.IN_LOBBY || this.state == ProfileState.IN_QUEUE) {
            if (this.party != null && this.party.containsPlayer(otherPlayer)) {
                hide=false;
                NameTags.color(player, otherPlayer, ChatColor.GREEN, false);
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
            } else if (skyWars != null) {
                SkyWarsPlayer skyWarsPlayer = skyWars.getEventPlayer(otherPlayer);
                if (skyWarsPlayer != null && skyWarsPlayer.getState() == SkyWarsPlayerState.WAITING) {
                    hide = false;
                }
            } else if (spleef != null) {
                SpleefPlayer spleefPlayer=spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide=false;
                }
            }
        }

        if (hide) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.hidePlayer(otherPlayer);
                }
            }.runTask(Practice.getInstance());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.showPlayer(otherPlayer);
                }
            }.runTask(Practice.getInstance());
        }
    }

    public void handleVisibility() {
        final Player player=this.getPlayer();
        if (player != null) {
            new BukkitRunnable() {
                public void run() {
                    for ( final Player otherPlayer : Bukkit.getOnlinePlayers() ) {
                        Profile.this.handleVisibility(player, otherPlayer);
                    }
                }
            }.runTaskAsynchronously(Practice.getInstance());
        }
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
}

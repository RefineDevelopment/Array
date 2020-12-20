package me.array.ArrayPractice.profile;

import com.mongodb.client.model.Sorts;

import java.util.ArrayList;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.duel.DuelProcedure;
import me.array.ArrayPractice.duel.DuelRequest;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLeaderboards;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.tournament.TournamentManager;
import me.array.ArrayPractice.util.InventoryUtil;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import com.mongodb.client.model.ReplaceOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayer;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayer;
import me.array.ArrayPractice.event.impl.lms.player.FFAPlayer;
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayer;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayer;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayerState;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayerState;
import me.array.ArrayPractice.util.nametag.NameTags;
import org.bukkit.ChatColor;
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayerState;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayerState;
import me.array.ArrayPractice.event.impl.lms.player.FFAPlayerState;
import me.array.ArrayPractice.profile.hotbar.HotbarLayout;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.queue.QueueProfile;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.profile.meta.ProfileKitData;
import me.array.ArrayPractice.profile.meta.ProfileKitEditor;
import me.array.ArrayPractice.profile.meta.option.ProfileOptions;
import me.array.ArrayPractice.profile.meta.essentials.ProfileEssentials;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import rip.verse.jupiter.knockback.KnockbackModule;
import rip.verse.jupiter.knockback.KnockbackProfile;

import java.util.List;
import java.util.UUID;
import java.util.Map;

public class Profile {
    private static Map<UUID, Profile> profiles;
    private static List<KitLeaderboards> globalEloLeaderboards;
    private static MongoCollection<Document> allProfiles;
    private static MongoCollection<Document> collection;
    private final UUID uuid;
    String name;
    int globalElo;
    int globalWins;
    int globalLosses;
    private ProfileState state;
    private final ProfileEssentials essentials;
    private final ProfileOptions options;
    private final ProfileKitEditor kitEditor;
    private final Map<Kit, ProfileKitData> kitData;
    private Party party;
    private Match match;
    private Sumo sumo;
    private Brackets brackets;
    private FFA ffa;
    private Parkour parkour;
    private Spleef spleef;
    private Queue queue;
    private QueueProfile queueProfile;
    private Cooldown enderpearlCooldown;
    private final Map<UUID, DuelRequest> sentDuelRequests;
    private DuelProcedure duelProcedure;
    private ProfileRematchData rematchData;

    public Profile(final UUID uuid) {
        this.globalElo=1000;
        this.globalWins=0;
        this.globalLosses=0;
        this.essentials=new ProfileEssentials();
        this.options=new ProfileOptions();
        this.kitEditor=new ProfileKitEditor();
        this.kitData=new HashMap<>();
        this.enderpearlCooldown=new Cooldown(0L);
        this.sentDuelRequests=new HashMap<>();
        this.uuid=uuid;
        this.state=ProfileState.IN_LOBBY;
        for ( final Kit kit : Kit.getKits() ) {
            this.kitData.put(kit, new ProfileKitData());
        }
        this.calculateGlobalElo();
        this.calculateGlobalWins();
        this.calculateGlobalLosses();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void calculateGlobalElo() {
        int globalElo=0;
        int kitCounter=0;
        for ( final Kit kit : this.kitData.keySet() ) {
            if (kit.getGameRules().isRanked()) {
                globalElo+=this.kitData.get(kit).getElo();
                ++kitCounter;
            }
        }
        this.globalElo=Math.round((float) (globalElo / kitCounter));
    }

    public void calculateGlobalWins() {
        int globalWins = 0;
        for ( final Kit kit : this.kitData.keySet() ) {
            int wins = this.kitData.get(kit).getWon();
            this.globalWins = globalWins + wins;
        }
    }

    public void calculateGlobalLosses() {
        int globalLosses=0;
        for ( final Kit kit : this.kitData.keySet() ) {
            int lost = this.kitData.get(kit).getLost();
            this.globalLosses = globalLosses + lost;
        }
    }

    public boolean canSendDuelRequest(final Player player) {
        if (!this.sentDuelRequests.containsKey(player.getUniqueId())) {
            return true;
        }
        final DuelRequest request=this.sentDuelRequests.get(player.getUniqueId());
        if (request.isExpired()) {
            this.sentDuelRequests.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public boolean isPendingDuelRequest(final Player player) {
        if (!this.sentDuelRequests.containsKey(player.getUniqueId())) {
            return false;
        }
        final DuelRequest request=this.sentDuelRequests.get(player.getUniqueId());
        if (request.isExpired()) {
            this.sentDuelRequests.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public boolean isInLobby() {
        return this.state == ProfileState.IN_LOBBY;
    }

    public boolean isInQueue() {
        return this.state == ProfileState.IN_QUEUE && this.queue != null && this.queueProfile != null;
    }

    public boolean isInMatch() {
        return this.match != null;
    }

    public boolean isInFight() {
        return this.state == ProfileState.IN_FIGHT && this.match != null;
    }

    public boolean isSpectating() {
        return this.state == ProfileState.SPECTATE_MATCH && (this.match != null || this.sumo != null || this.brackets != null || this.ffa != null || this.parkour != null || this.spleef != null);
    }

    public boolean isInEvent() {
        return this.state == ProfileState.IN_EVENT;
    }

    public boolean isInTournament(final Player player) {
        return TournamentManager.CURRENT_TOURNAMENT != null && TournamentManager.CURRENT_TOURNAMENT.isParticipating(player);
    }

    public boolean isInSumo() {
        return this.state == ProfileState.IN_EVENT && this.sumo != null;
    }

    public boolean isInBrackets() {
        return this.state == ProfileState.IN_EVENT && this.brackets != null;
    }

    public boolean isInFfa() {
        return this.state == ProfileState.IN_EVENT && this.ffa != null;
    }

    public boolean isInParkour() {
        return this.state == ProfileState.IN_EVENT && this.parkour != null;
    }

    public boolean isInSpleef() {
        return this.state == ProfileState.IN_EVENT && this.spleef != null;
    }

    public boolean isInSomeSortOfFight() {
        return (this.state == ProfileState.IN_FIGHT && this.match != null) || this.state == ProfileState.IN_EVENT;
    }

    public boolean isBusy(final Player player) {
        return this.isInQueue() || this.isInFight() || this.isInEvent() || this.isSpectating() || this.isInTournament(player);
    }

    public void checkForHotbarUpdate() {
        final Player player=this.getPlayer();
        if (player == null) {
            return;
        }
        if (this.isInLobby() && !this.kitEditor.isActive()) {
            boolean update=false;
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
            final boolean activeEvent=(Array.get().getSumoManager().getActiveSumo() != null && Array.get().getSumoManager().getActiveSumo().isWaiting()) || (Array.get().getBracketsManager().getActiveBrackets() != null && Array.get().getBracketsManager().getActiveBrackets().isWaiting()) || (Array.get().getFfaManager().getActiveFFA() != null && Array.get().getFfaManager().getActiveFFA().isWaiting()) || (Array.get().getParkourManager().getActiveParkour() != null && Array.get().getParkourManager().getActiveParkour().isWaiting()) || (Array.get().getSpleefManager().getActiveSpleef() != null && Array.get().getSpleefManager().getActiveSpleef().isWaiting());
            final int eventSlot=player.getInventory().first(Hotbar.getItems().get(HotbarItem.EVENT_JOIN));
            if (eventSlot == -1 && activeEvent) {
                update=true;
            } else if (eventSlot != -1 && !activeEvent) {
                update=true;
            }
            if (update) {
                new BukkitRunnable() {
                    public void run() {
                        Profile.this.refreshHotbar();
                    }
                }.runTask(Array.get());
            }
        }
    }

    public void refreshHotbar() {
        final Player player=this.getPlayer();
        if (player != null) {
            PlayerUtil.reset(player, false);
            if (this.isInLobby()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.LOBBY, this));
            } else if (this.isInQueue()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.QUEUE, this));
            } else if (this.isSpectating()) {
                PlayerUtil.spectator(player);
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
            } else if (this.isInSumo()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SUMO_SPECTATE, this));
            } else if (this.isInBrackets()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.BRACKETS_SPECTATE, this));
            } else if (this.isInFfa()) {
                if (this.getFfa().getEventPlayer(player).getState().equals(FFAPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.FFA_SPECTATE, this));
            } else if (this.isInParkour()) {
                if (this.getParkour().getEventPlayer(player).getState().equals(ParkourPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.PARKOUR_SPECTATE, this));
            } else if (this.isInSpleef()) {
                if (this.getSpleef().getEventPlayer(player).getState().equals(SpleefPlayerState.ELIMINATED)) {
                    PlayerUtil.spectator(player);
                }
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.SPLEEF_SPECTATE, this));
            } else if (this.isInFight() && !this.match.getTeamPlayer(player).isAlive()) {
                player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, this));
            }
            player.updateInventory();
        }
    }

    public void handleVisibility(final Player player, final Player otherPlayer) {
        if (player == null || otherPlayer == null) {
            return;
        }
        boolean hide=true;
        if (this.state == ProfileState.IN_LOBBY || this.state == ProfileState.IN_QUEUE) {
            if (this.party != null && this.party.containsPlayer(otherPlayer)) {
                hide=false;
                NameTags.color(player, otherPlayer, ChatColor.BLUE, false);
            }
        } else if (this.isInFight()) {
            final TeamPlayer teamPlayer=this.match.getTeamPlayer(otherPlayer);
            if (teamPlayer != null && teamPlayer.isAlive()) {
                hide=false;
            }
        } else if (this.isSpectating()) {
            if (this.sumo != null) {
                final SumoPlayer sumoPlayer=this.sumo.getEventPlayer(otherPlayer);
                if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.brackets != null) {
                final BracketsPlayer bracketsPlayer=this.brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.ffa != null) {
                final FFAPlayer ffaPlayer=this.ffa.getEventPlayer(otherPlayer);
                if (ffaPlayer != null && ffaPlayer.getState() == FFAPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.parkour != null) {
                final ParkourPlayer parkourPlayer=this.parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.spleef != null) {
                final SpleefPlayer spleefPlayer=this.spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide=false;
                }
            } else {
                final TeamPlayer teamPlayer=this.match.getTeamPlayer(otherPlayer);
                if (teamPlayer != null && teamPlayer.isAlive()) {
                    hide=false;
                }
            }
        } else if (this.isInEvent()) {
            if (this.sumo != null) {
                if (!this.sumo.getSpectators().contains(otherPlayer.getUniqueId())) {
                    final SumoPlayer sumoPlayer=this.sumo.getEventPlayer(otherPlayer);
                    if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayerState.WAITING) {
                        hide=false;
                    }
                }
            } else if (this.brackets != null) {
                final BracketsPlayer bracketsPlayer=this.brackets.getEventPlayer(otherPlayer);
                if (bracketsPlayer != null && bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.ffa != null) {
                final FFAPlayer ffaPlayer=this.ffa.getEventPlayer(otherPlayer);
                if (ffaPlayer != null && ffaPlayer.getState() == FFAPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.parkour != null) {
                final ParkourPlayer parkourPlayer=this.parkour.getEventPlayer(otherPlayer);
                if (parkourPlayer != null && parkourPlayer.getState() == ParkourPlayerState.WAITING) {
                    hide=false;
                }
            } else if (this.spleef != null) {
                final SpleefPlayer spleefPlayer=this.spleef.getEventPlayer(otherPlayer);
                if (spleefPlayer != null && spleefPlayer.getState() == SpleefPlayerState.WAITING) {
                    hide=false;
                }
            }
        }
        if (hide) {
            new BukkitRunnable() {
                public void run() {
                    player.hidePlayer(otherPlayer);
                }
            }.runTask(Array.get());
        } else {
            new BukkitRunnable() {
                public void run() {
                    player.showPlayer(otherPlayer);
                }
            }.runTask(Array.get());
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
            }.runTaskAsynchronously(Array.get());
        }
    }

    public void load() {
        final Document document=Profile.collection.find(Filters.eq("uuid", this.uuid.toString())).first();
        if (document == null) {
            this.save();
            return;
        }

        this.globalElo=document.getInteger("globalElo");
        this.globalLosses=document.getInteger("globalLosses");
        this.globalWins=document.getInteger("globalWins");
        final Document essentials=(Document) document.get("essentials");
        if (essentials == null) {
            final Document essentialsDocument=new Document();
            essentialsDocument.put("nick", null);
            document.put("essentials", essentialsDocument);
        } else {
            this.essentials.setNick(essentials.getString("nick"));
        }


        final Document options=(Document) document.get("options");
        this.options.setShowScoreboard(options.getBoolean("showScoreboard"));
        this.options.setAllowSpectators(options.getBoolean("allowSpectators"));
        this.options.setReceiveDuelRequests(options.getBoolean("receiveDuelRequests"));
        final Document kitStatistics=(Document) document.get("kitStatistics");


        for ( final String key : kitStatistics.keySet() ) {
            final Document kitDocument=(Document) kitStatistics.get(key);
            final Kit kit=Kit.getByName(key);
            if (kit != null) {
                final ProfileKitData profileKitData=new ProfileKitData();
                profileKitData.setElo(kitDocument.getInteger("elo"));
                if (kitDocument.getInteger("won") != null || kitDocument.getInteger("lost") != null) {
                    profileKitData.setWon(kitDocument.getInteger("won"));
                    profileKitData.setLost(kitDocument.getInteger("lost"));
                } else {
                    for ( final Map.Entry<Kit, ProfileKitData> entry : this.kitData.entrySet()) {
                        kitDocument.put("won", entry.getValue().getWon());
                        kitDocument.put("lost", entry.getValue().getLost());
                    }
                    this.kitData.put(kit, profileKitData);
                }
            }
        }

        final Document kitsDocument=(Document) document.get("loadouts");
        for ( final String key2 : kitsDocument.keySet() ) {
            final Kit kit=Kit.getByName(key2);
            if (kit != null) {
                final JsonArray kitsArray = new JsonParser().parse(kitsDocument.getString(key2)).getAsJsonArray();
                final KitLoadout[] loadouts = new KitLoadout[4];
                for ( final JsonElement kitElement : kitsArray ) {
                    final JsonObject kitObject = kitElement.getAsJsonObject();
                    final KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
                    loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
                    loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));
                    loadouts[kitObject.get("index").getAsInt()]=loadout;
                }
                this.kitData.get(kit).setLoadouts(loadouts);
            }
        }
    }

    public void save() {
        final Document document=new Document();
        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);
        document.put("globalElo", this.globalElo);
        document.put("globalWins", this.globalWins);
        document.put("globalLosses", this.globalLosses);
        final Document essentialsDocument=new Document();
        essentialsDocument.put("nick", this.essentials.getNick());
        document.put("essentials", essentialsDocument);
        final Document optionsDocument=new Document();
        optionsDocument.put("showScoreboard", this.options.isShowScoreboard());
        optionsDocument.put("allowSpectators", this.options.isAllowSpectators());
        optionsDocument.put("receiveDuelRequests", this.options.isReceiveDuelRequests());
        document.put("options", optionsDocument);
        final Document kitStatisticsDocument=new Document();
        for ( final Map.Entry<Kit, ProfileKitData> entry : this.kitData.entrySet() ) {
            final Document kitDocument = new Document();
            kitDocument.put("elo", entry.getValue().getElo());
            kitDocument.put("won", entry.getValue().getWon());
            kitDocument.put("lost", entry.getValue().getLost());
            kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
        }
        document.put("kitStatistics", kitStatisticsDocument);
        final Document kitsDocument=new Document();
        for ( final Map.Entry<Kit, ProfileKitData> entry2 : this.kitData.entrySet() ) {
            final JsonArray kitsArray=new JsonArray();
            for ( int i=0; i < 4; ++i ) {
                final KitLoadout loadout=entry2.getValue().getLoadout(i);
                if (loadout != null) {
                    final JsonObject kitObject=new JsonObject();
                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", loadout.getCustomName());
                    kitObject.addProperty("armor", InventoryUtil.serializeInventory(loadout.getArmor()));
                    kitObject.addProperty("contents", InventoryUtil.serializeInventory(loadout.getContents()));
                    kitsArray.add(kitObject);
                }
            }
            kitsDocument.put(entry2.getKey().getName(), kitsArray.toString());
        }
        document.put("loadouts", kitsDocument);
        Profile.collection.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
    }

    public static void init() {
        Profile.collection=Array.get().getMongoDatabase().getCollection("profiles");
        for ( final Player player : Bukkit.getOnlinePlayers() ) {
            final Profile profile = new Profile(player.getUniqueId());
            try {
                profile.load();
            } catch (Exception e) {
                player.kickPlayer(CC.RED + "The server is loading...");
                continue;
            }
            Profile.profiles.put(player.getUniqueId(), profile);
        }
        new BukkitRunnable() {
            public void run() {
                for ( final Profile profile : Profile.getProfiles().values() ) {
                    profile.save();
                }
            }
        }.runTaskTimerAsynchronously(Array.get(), 10L * 40L * 5L, 10L * 40L * 5L);
        loadAllProfiles();
        new BukkitRunnable() {
            public void run() {
                Profile.loadAllProfiles();
                Kit.getKits().forEach(Kit::updateKitLeaderboards);
            }
        }.runTaskTimerAsynchronously(Array.get(), 30L * 60L * 5L, 30L * 60L * 5L);
        new BukkitRunnable() {
            public void run() {
                Profile.loadGlobalLeaderboards();
                Bukkit.broadcastMessage(CC.translate("&c&lWarning &7Updating Leaderboards, this might cause some lag!"));
            }
        }.runTaskTimerAsynchronously(Array.get(), 30L * 60L * 5L, 30L * 60L * 5L);
        new BukkitRunnable() {
            public void run() {
                for ( final Profile profile : Profile.getProfiles().values() ) {
                    profile.checkForHotbarUpdate();
                }
            }
        }.runTaskTimerAsynchronously(Array.get(), 60L, 60L);
    }

    public static Profile getByUuid(final UUID uuid) {
        Profile profile=Profile.profiles.get(uuid);
        if (profile == null) {
            profile=new Profile(uuid);
        }
        return profile;
    }

    public static void loadAllProfiles() {
        Profile.allProfiles=Array.get().getMongoDatabase().getCollection("profiles");
    }

    public static void loadGlobalLeaderboards() {
        if (!getGlobalEloLeaderboards().isEmpty()) {
            getGlobalEloLeaderboards().clear();
        }
        for ( final Document document : getAllProfiles().find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>())) {
            final KitLeaderboards kitLeaderboards=new KitLeaderboards();
            kitLeaderboards.setName((String) document.get("name"));
            kitLeaderboards.setElo((int) document.get("globalElo"));
            getGlobalEloLeaderboards().add(kitLeaderboards);
        }
    }

    public static Map<UUID, Profile> getProfiles() {
        return Profile.profiles;
    }

    public static List<KitLeaderboards> getGlobalEloLeaderboards() {
        return Profile.globalEloLeaderboards;
    }

    public static MongoCollection<Document> getAllProfiles() {
        return Profile.allProfiles;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name=name;
    }

    public void setKnockback(final String kb) {
        KnockbackProfile knockback=KnockbackModule.INSTANCE.profiles.get(kb);
        ((CraftPlayer) getPlayer()).getHandle().setKnockback(knockback);
    }

    public int getGlobalElo() {
        return this.globalElo;
    }

    public int getGlobalWins() {
        return this.globalWins;
    }

    public void setGlobalWins(final int globalWins) {
        this.globalWins = globalWins;
    }

    public int getGlobalLosses() {
        return this.globalLosses;
    }

    public void setGlobalLosses(final int globalLosses) {
        this.globalLosses = globalLosses;
    }

    public ProfileState getState() {
        return this.state;
    }

    public void setState(final ProfileState state) {
        this.state=state;
    }

    public ProfileEssentials getEssentials() {
        return this.essentials;
    }

    public ProfileOptions getOptions() {
        return this.options;
    }

    public ProfileKitEditor getKitEditor() {
        return this.kitEditor;
    }

    public Map<Kit, ProfileKitData> getKitData() {
        return this.kitData;
    }

    public Party getParty() {
        return this.party;
    }

    public void setParty(final Party party) {
        this.party=party;
    }

    public Match getMatch() {
        return this.match;
    }

    public void setMatch(final Match match) {
        this.match=match;
    }

    public Sumo getSumo() {
        return this.sumo;
    }

    public void setSumo(final Sumo sumo) {
        this.sumo=sumo;
    }

    public Brackets getBrackets() {
        return this.brackets;
    }

    public void setBrackets(final Brackets brackets) {
        this.brackets=brackets;
    }

    public FFA getFfa() {
        return this.ffa;
    }

    public void setFfa(final FFA ffa) {
        this.ffa=ffa;
    }

    public Parkour getParkour() {
        return this.parkour;
    }

    public void setParkour(final Parkour parkour) {
        this.parkour=parkour;
    }

    public Spleef getSpleef() {
        return this.spleef;
    }

    public void setSpleef(final Spleef spleef) {
        this.spleef=spleef;
    }

    public Queue getQueue() {
        return this.queue;
    }

    public void setQueue(final Queue queue) {
        this.queue=queue;
    }

    public QueueProfile getQueueProfile() {
        return this.queueProfile;
    }

    public void setQueueProfile(final QueueProfile queueProfile) {
        this.queueProfile=queueProfile;
    }

    public Cooldown getEnderpearlCooldown() {
        return this.enderpearlCooldown;
    }

    public void setEnderpearlCooldown(final Cooldown enderpearlCooldown) {
        this.enderpearlCooldown=enderpearlCooldown;
    }

    public Map<UUID, DuelRequest> getSentDuelRequests() {
        return this.sentDuelRequests;
    }

    public DuelProcedure getDuelProcedure() {
        return this.duelProcedure;
    }

    public void setDuelProcedure(final DuelProcedure duelProcedure) {
        this.duelProcedure=duelProcedure;
    }

    public ProfileRematchData getRematchData() {
        return this.rematchData;
    }

    public void setRematchData(final ProfileRematchData rematchData) {
        this.rematchData=rematchData;
    }

    static {
        Profile.profiles=new HashMap<>();
        Profile.globalEloLeaderboards=new ArrayList<>();
    }
}

package me.drizzy.practice.clan;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.clan.meta.ClanStatisticsData;
import me.drizzy.practice.enums.ClanProfileType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.leaderboards.LeaderboardsAdapter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.other.DateUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author Drizzy
 * Created at 4/8/2021
 */
@Getter
@Setter
@SuppressWarnings("all")
public class Clan {

    @Getter public static final List<Clan> clans = new ArrayList<>();
    @Getter public static final MongoCollection<Document> collection = Array.getInstance().getMongoDatabase().getCollection("Clans");
    @Getter private static final List<LeaderboardsAdapter> clanLeaderboards = new ArrayList<>();

    public static final Executor thread = Array.getInstance().getMongoThread();
    private final Map<Kit, ClanStatisticsData> statisticsData = new LinkedHashMap<>();
    private final List<ClanProfile> allMembers = new ArrayList<>();
    private final List<ClanProfile> members = new ArrayList<>();
    private final List<ClanProfile> captains = new ArrayList<>();

    private final String name;
    private final UUID uuid;
    private ClanProfile leader;
    private String description;
    private String password;
    private int globalElo = 1000;
    private long dateCreated;

    /**
     * The main Clan Constructor
     *
     * @param name The name of the clan
     * @param leader The UUID of the leader of the clan
     */
    public Clan(String name, UUID leader, UUID uuid) {
        this.name = name;
        this.leader = new ClanProfile(leader, this, ClanProfileType.LEADER);
        Profile.getByUuid(leader).setClan(this);
        this.uuid = uuid;
        this.dateCreated = System.currentTimeMillis();

        for(Kit kit : Kit.getKits()) {
            if (kit.getGameRules().isClan()) {
                this.statisticsData.put(kit, new ClanStatisticsData());
            }
        }
        this.calculateGlobalElo();
    }

    /**
     * Broadcast message to all members of the clan
     *
     * @param message The message to broadcast
     */
    public void broadcast(String message) {
        members.stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer).filter(Objects::nonNull)
        .forEach(player -> player.sendMessage(CC.translate(message)));
    }

    /**
     * Get a clan by its name
     *
     * @param name The name of the Clan
     * @return {@link Clan}
     */
    public static Clan getByName(String name) {
        return clans.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }


    /**
     * This method is called on Start
     * We use this to load our clans and setup Tasks
     */
    public static void preLoad() {
        new BukkitRunnable() {
            public void run() {
                Profile.getProfiles().values().forEach(clan -> clan.getClanInviteList().removeIf(ClanInvite::hasExpired));
            }
        }.runTaskTimerAsynchronously(Array.getInstance(), 100L, 100L);
        load();
    }

    /**
     * Load all our Clans
     */
    public static void load() {
        thread.execute(() -> {
        for ( Object o : collection.find()) {
            Document document = (Document) o;

            Clan clan = new Clan(document.getString("name"), (UUID) document.get("leader"), (UUID) document.get("uuid"));

            clan.setGlobalElo(document.getInteger("globalElo"));

            ClanProfile leader = new ClanProfile((UUID) document.get("leader"), clan, ClanProfileType.LEADER);
            clan.setLeader(leader);
            clan.getAllMembers().add(leader);

            List<String> strings = new ArrayList<>(document.getList("members", String.class));
            List<UUID> uuids =  strings.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids ) {
                ClanProfile member = new ClanProfile(uuid, clan, ClanProfileType.MEMBER);
                clan.getMembers().add(member);
                clan.getAllMembers().add(member);
            }

            List<String> strings1 = new ArrayList<>(document.getList("captains", String.class));
            List<UUID> uuids1 =  strings1.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids1 ) {
                ClanProfile member = new ClanProfile(uuid, clan, ClanProfileType.CAPTAIN);
                clan.getCaptains().add(member);
                clan.getAllMembers().add(member);
            }

            Document kitStatistics=(Document) document.get("kitStatistics");

            for ( String key : kitStatistics.keySet() ) {
                Document kitDocument=(Document) kitStatistics.get(key);
                Kit kit = Kit.getByName(key);

                if (kit != null) {
                    ClanStatisticsData statisticsData = new ClanStatisticsData();
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
                    clan.statisticsData.put(kit, statisticsData);
                }
            }

            clan.setDescription(document.getString("description"));
            clan.setDateCreated(document.getLong("date-created"));

            if (document.containsKey("description")) clan.setDescription(document.getString("description"));
            if (document.containsKey("password")) clan.setPassword(document.getString("password"));

            clans.add(clan);
        }
        });
    }

    /**
     * Load an Individual Clan
     */
    public void loadClan() {
        thread.execute(() -> {
            Document document=(Document) collection.find().filter(Filters.eq("uuid", uuid.toString())).first();

            this.setGlobalElo(document.getInteger("globalElo"));

            ClanProfile leader = new ClanProfile((UUID) document.get("leader"), this, ClanProfileType.LEADER);
            this.setLeader(leader);
            this.getAllMembers().add(leader);

            List<String> strings = new ArrayList<>(document.getList("members", String.class));
            List<UUID> uuids = strings.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids ) {
                ClanProfile member=new ClanProfile(uuid, this, ClanProfileType.MEMBER);
                this.getMembers().add(member);
                this.getAllMembers().add(member);
            }

            List<String> strings1 = new ArrayList<>(document.getList("captains", String.class));
            List<UUID> uuids1 = strings1.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids1 ) {
                ClanProfile member = new ClanProfile(uuid, this, ClanProfileType.CAPTAIN);
                this.getCaptains().add(member);
                this.getAllMembers().add(member);
            }

            Document kitStatistics = (Document) document.get("kitStatistics");

            for ( String key : kitStatistics.keySet() ) {
                Document kitDocument = (Document) kitStatistics.get(key);
                Kit kit=Kit.getByName(key);

                if (kit != null) {
                    ClanStatisticsData statisticsData=new ClanStatisticsData();
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

            this.setDescription(document.getString("description"));
            this.setDateCreated(document.getLong("date-created"));

            if (document.containsKey("description")) this.setDescription(document.getString("description"));
            if (document.containsKey("password")) this.setPassword(document.getString("password"));

            clans.add(this);
        });
    }

    /**
     * Save our all Clans
     */
    public static void save() {
        thread.execute(() -> {
        Clan.getClans().forEach(clan -> {
            Document document = new Document();
            document.put("name", clan.getName());
            document.put("uuid", clan.getUuid().toString());
            document.put("globalElo", clan.globalElo);
            document.put("leader", clan.getLeader());
            document.put("members", clan.getMembers());
            document.put("captains", clan.getCaptains());
            document.put("description", clan.getDescription());
            document.put("date-created", clan.getDateCreated());

            Document kitStatisticsDocument = new Document();

            for ( Map.Entry<Kit, ClanStatisticsData> entry : clan.getStatisticsData().entrySet() ) {
                Document kitDocument = new Document();
                kitDocument.put("elo", entry.getValue().getElo());
                kitDocument.put("won", entry.getValue().getWon());
                kitDocument.put("lost", entry.getValue().getLost());
                kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
            }
            document.put("kitStatistics", kitStatisticsDocument);

            if (clan.getDescription() != null) document.put("description", clan.getDescription());
            if (clan.getPassword() != null) document.put("password", clan.getPassword());

            collection.replaceOne(Filters.eq("uuid", clan.getUuid().toString()), document, new ReplaceOptions().upsert(true));
        });
        });
    }


    /**
     * Save an Individual Clan
     */
    public void saveClan() {
        thread.execute(() -> {
            Document clanDocument = new Document();
            clanDocument.put("name", this.getName());
            clanDocument.put("uuid", this.getUuid().toString());
            clanDocument.put("globalElo", this.globalElo);
            clanDocument.put("leader", this.getLeader());
            clanDocument.put("members", this.getMembers());
            clanDocument.put("captains", this.getCaptains());
            clanDocument.put("description", this.getDescription());
            clanDocument.put("date-created", this.getDateCreated());

            Document kitStatisticsDocument = new Document();

            for ( Map.Entry<Kit, ClanStatisticsData> entry : this.getStatisticsData().entrySet() ) {
                Document kitDocument = new Document();
                kitDocument.put("elo", entry.getValue().getElo());
                kitDocument.put("won", entry.getValue().getWon());
                kitDocument.put("lost", entry.getValue().getLost());
                kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
            }
            clanDocument.put("kitStatistics", kitStatisticsDocument);

            if (this.getDescription() != null) clanDocument.put("description", this.getDescription());
            if (this.getPassword() != null) clanDocument.put("password", this.getPassword());

            collection.replaceOne(Filters.eq("uuid", uuid.toString()), clanDocument, new ReplaceOptions().upsert(true));
        });
    }

    public void promote(final ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
        member.setClanProfileType(ClanProfileType.CAPTAIN);
        this.members.remove(member);
        this.captains.add(member);
        this.saveClan();
        this.broadcast(CC.translate("&8[&cClan&8] &c" + player.getName() + " &7has been promoted to &c&lCaptain&7!"));
    }

    //TODO: Complete this
    public void leader(final ClanProfile member) {

    }

    //TODO: Complete this
    public void demote(final ClanProfile member) {

    }


    /**
     * Invite a player to the clan
     *
     * @param target The player being invited
     */
    public void invite(final Player target) {
        Profile profile = Profile.getByPlayer(target);
        profile.getClanInviteList().add(new ClanInvite(this));
        OfflinePlayer leader = Bukkit.getOfflinePlayer(this.getLeader().getUuid());

        List<String> invite = new ArrayList<>();
        invite.add(CC.translate("&8[&cClan&8] &7You have been invited to join &c" + leader.getName() + "'s &7clan!"));
        invite.add(CC.translate("&8[&cClan&8] &a(Click here to Join)"));

        for ( String string : invite ) {
            Clickable message = new Clickable(string, "Click to Join", "/clan join " + leader.getName());
            message.sendToPlayer(target);
        }

    }

    /**
     * Make the player invited, join the clan
     *
     * @param joiner The player joining the clan
     */
    public void join(final Player joiner, final ClanInvite clanInvite) {
        Profile profile = Profile.getByPlayer(joiner);

        if (!profile.getClanInviteList().contains(clanInvite)) {
            joiner.sendMessage(CC.translate("&7You are not invited to this clan or your invite expired!"));
            return;
        }

        Array.getInstance().getTaskThread().execute(() -> {
            profile.setClan(this);
            this.getMembers().add(new ClanProfile(joiner.getUniqueId(), this, ClanProfileType.MEMBER));

            if (clanInvite != null) profile.getClanInviteList().remove(clanInvite);
            this.save();
            this.broadcast(CC.RED + joiner.getDisplayName() + CC.GRAY + " has joined the clan!");
            profile.save();
            profile.refreshHotbar();
        });
    }

    /**
     * Make the player leave his clan
     *
     * @param leaver The player leaving the clan
     */
    public void leave(final Player leaver) {
        Profile profile = Profile.getByPlayer(leaver);

        Array.getInstance().getTaskThread().execute(() -> {
            ClanProfile clanProfile = getByUUID(leaver.getUniqueId());
            switch (clanProfile.getClanProfileType()) {
                case MEMBER:
                    this.getMembers().remove(clanProfile);
                    this.getAllMembers().remove(getByUUID(leaver.getUniqueId()));
                    profile.setClan(null);
                    profile.save();
                    profile.refreshHotbar();
                    this.saveClan();
                    this.broadcast(CC.RED + leaver.getDisplayName() + CC.GRAY + " has left the clan!");
                    break;
                case CAPTAIN:
                    this.getCaptains().remove(clanProfile);
                    this.getAllMembers().remove(getByUUID(leaver.getUniqueId()));
                    profile.setClan(null);
                    profile.save();
                    profile.refreshHotbar();
                    this.saveClan();
                    this.broadcast(CC.RED + leaver.getDisplayName() + CC.GRAY + " has left the clan!");
                    break;
                case LEADER:
                    throw new IllegalArgumentException("Leader can not leave the clan!");
            }
        });
    }

    /**
     * Get the Data Created in a Formatted String
     *
     * @return {@link String}
     */
    public String getDateCreated() {
        return DateUtil.getFormattedDate(this.dateCreated);
    }

    /**
     * Get the Clan's Total Wins
     *
     * @return {@link Integer}
     */
    public Integer getTotalWins() {
        return this.statisticsData.values().stream().mapToInt(ClanStatisticsData::getWon).sum();
    }

    /**
     * Get the Clan's Total Losses
     *
     * @return {@link Integer}
     */
    public Integer getTotalLost() {
        return this.statisticsData.values().stream().mapToInt(ClanStatisticsData::getLost).sum();
    }

    /**
     * Recalculate the Clan's Global ELO
     */
    public void calculateGlobalElo() {
        int globalElo = 0;
        int kitCounter = 0;
        for (Kit kit : this.statisticsData.keySet()) {
            if (kit.getGameRules().isClan()) {
                globalElo += this.statisticsData.get(kit).getElo();
                kitCounter++;
            }
        }
        this.globalElo = Math.round(globalElo / kitCounter);
    }


    /**
     * Get a Clan Member by their UUID
     *
     * @param uuid Clan Member's UUID
     * @return {@link ClanProfile}
     */
    public static ClanProfile getByUUID(UUID uuid) {
        for ( Clan clan : Clan.getClans() ) {
            for ( ClanProfile member : clan.getMembers() ) {
                if (member.getUuid().equals(uuid)) {
                    return member;
                }
            }
            for ( ClanProfile captains : clan.getCaptains() ) {
                if (captains.getUuid().equals(uuid)) {
                    return captains;
                }
            }
        }
        return null;
    }


    /**
     * Load our Clan Leaderboards
     */
    public static void loadClanLeaderboards() {
        if (!getClanLeaderboards().isEmpty()) getClanLeaderboards().clear();
        thread.execute(() -> {
            for ( Document document : collection.find().sort(Sorts.descending("globalElo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter=new LeaderboardsAdapter();
                leaderboardsAdapter.setName((String) document.get("name"));
                leaderboardsAdapter.setElo((Integer) document.get("globalElo"));
                getClanLeaderboards().add(leaderboardsAdapter);
            }
        });
    }

    /**
     * Send clan information to the player
     *
     * @param player The Player recieving the information
     * @param clan The Clan whose information is being sent
     */
    public void describeClan(Player player) {
        Clan clan = this;
        clan.getMembers().sort(Comparator.comparing(cm -> cm.getClanProfileType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        clan.getMembers().forEach(cm -> playerNames.add((clan.getLeader().getUuid().equals(cm.getUuid()) ? CC.DARK_GREEN + "***" : clan.getCaptains().contains(cm.getUuid()) ? CC.DARK_GREEN + "*" : "") + colorName(cm.getUuid())));

        player.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------");
        player.sendMessage(CC.BLUE + CC.BOLD + clan.getName() + CC.GRAY + " [" + clan.getMembers().size() + "/20]");
        player.sendMessage(CC.YELLOW + "Description: " + CC.PINK + clan.getDescription());
        player.sendMessage(CC.YELLOW + "Members: " + Strings.join(playerNames, CC.GRAY + ", "));
        player.sendMessage(CC.YELLOW + "Elo: " + CC.BLUE + (clan.getGlobalElo() == 0 ? "[N/A]" : clan.getGlobalElo()));
        player.sendMessage(CC.YELLOW + "Date Created: " + CC.GREEN + clan.getDateCreated());
        player.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------");
    }

    /**
     * Get a player's formatted name for the list
     *
     * @param uuid The UUID of the player
     * @return {@link String}
     */
    private String colorName(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.isOnline() ? CC.GREEN + offlinePlayer.getName() : CC.RED + offlinePlayer.getName();
    }


    /**
     * Get an Invite using the player and Clan name
     *
     * @param player The player whose invite we are getting
     * @param name Clan name
     * @return {@link ClanInvite}
     */
    public ClanInvite getInvite(final String name, final Player player) {
        Clan clan = Clan.getByName(name);
        Profile profile = Profile.getByPlayer(player);
        for ( ClanInvite invite : profile.getClanInviteList())
            if (invite.getClan().equals(clan)) {
                if (invite.hasExpired()) {
                    return null;
                }
                return invite;
            }
        return null;
    }

}

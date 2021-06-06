package me.drizzy.practice.clan;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.clan.meta.ClanInvite;
import me.drizzy.practice.clan.meta.ClanProfile;
import me.drizzy.practice.leaderboards.LeaderboardsAdapter;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.other.DateUtil;
import me.drizzy.practice.util.other.TaskUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/8/2021
 * Project: Array
 */

@Getter
@Setter
@SuppressWarnings("all")
public class Clan {

    @Getter public static final List<Clan> clans = new ArrayList<>();
    @Getter public static final MongoCollection<Document> collection = Array.getInstance().getMongoDatabase().getCollection("clans");
    @Getter private static final List<LeaderboardsAdapter> clanLeaderboards = new ArrayList<>();

    public static final Executor thread = Array.getInstance().getMongoThread();
    private final List<ClanProfile> members = new ArrayList<>();
    private final List<ClanProfile> captains = new ArrayList<>();
    private final List<UUID> bannedPlayers = new ArrayList<>();

    private final String name;
    private final UUID uuid;

    private ClanProfile leader;
    private String description;
    private String password;
    private String dateCreated;

    private int elo = 1000;
    private int wins = 0;
    private int losses = 0;
    private int winStreak = 0;
    private int highestWinStreak = 0;

    /**
     * The main Clan Constructor
     *
     * @param name The name of the clan
     * @param leader The UUID of the leader of the clan
     */
    public Clan(String name, UUID leader, UUID uuid) {
        this.name = name;
        this.leader = new ClanProfile(leader, this, ClanProfileType.LEADER);
        this.uuid = uuid;
        this.dateCreated = DateUtil.getFormattedDate(System.currentTimeMillis());
        Profile.getByUuid(leader).setClan(this);

        clans.add(this);
        this.save();
    }

    /**
     * Broadcast message to all members of the clan
     *
     * @param message The message to broadcast
     */
    public void broadcast(String message) {
        getAllMembers().stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer).filter(Objects::nonNull)
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
     * Get a clan by its leader
     *
     * @param player The leader of the Clan
     * @return {@link Clan}
     */
    public static Clan getByLeader(UUID player) {
        return clans.stream().filter(c -> c.getLeader().getUuid().equals(player)).findFirst().orElse(null);
    }


    /**
     * This method is called on Start
     * We use this to load our clans and setup Tasks
     */
    public static void  preload() {
        Array.logger("&7Loading Clans!");
        //First of all Load up the Leaderboards
        updateClanLeaderboards();

        //Register a Task that will clear the expired Invites
        TaskUtil.runTimerAsync(() -> {
            Profile.getProfiles().values().forEach(clan -> clan.getClanInviteList().removeIf(ClanInvite::hasExpired));
        }, 100L, 100L);

        //Then register a Task that will update the leaderboards frequently
        TaskUtil.runTimerAsync(Clan::updateClanLeaderboards, 600L, 600L);
        fetchClans();
        Array.logger("&aLoaded Clans!");
    }

    /**
     * Load all our Clans
     */
    public static void fetchClans() {
        thread.execute(() -> {
            if (collection.find() == null) {
                return;
            }
            for ( Object o : collection.find() ) {
                Document document = (Document) o;
                Clan clan = new Clan(document.getString("name"), UUID.fromString(document.getString("leader")), UUID.fromString(document.getString("_id")));
                clan.load();
            }
        });
    }

    /**
     * Delete the clan from the mongo
     */
    public void deleteClan() {
        collection.deleteOne(Filters.eq("_id", uuid.toString()));
    }

    /**
     * Load an Individual Clan
     */
    public void load() {
        thread.execute(() -> {
            Document document=(Document) collection.find().filter(Filters.eq("_id", uuid.toString())).first();

            if (document == null) {
                this.save();
                return;
            }
            this.setElo(document.getInteger("elo"));
            this.setWins(document.getInteger("wins"));
            this.setLosses(document.getInteger("losses"));
            this.setWinStreak(document.getInteger("winstreak"));
            this.setHighestWinStreak(document.getInteger("highestwinstreak"));

            ClanProfile leader = new ClanProfile(UUID.fromString(document.getString("leader")), this, ClanProfileType.LEADER);
            this.setLeader(leader);

            //Listen I know this is straight up trash code but just bare with me while I learn mongo XD
            List<String> strings = new ArrayList<>(document.getList("members", String.class));
            List<UUID> uuids = strings.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids ) {
                ClanProfile member = new ClanProfile(uuid, this, ClanProfileType.MEMBER);
                Profile profile = Profile.getByUuid(uuid);
                profile.setClanProfile(member);
                this.getMembers().add(member);
            }

            //Same here, trash mongo code XD
            List<String> strings1 = new ArrayList<>(document.getList("captains", String.class));
            List<UUID> uuids1 = strings1.stream().map(UUID::fromString).collect(Collectors.toList());

            for ( UUID uuid : uuids1 ) {
                ClanProfile member = new ClanProfile(uuid, this, ClanProfileType.CAPTAIN);
                Profile profile = Profile.getByUuid(uuid);
                profile.setClanProfile(member);
                this.getCaptains().add(member);
            }

            this.setDescription(document.getString("description"));
            this.setDateCreated(document.getString("date-created"));

            if (document.containsKey("description")) this.setDescription(document.getString("description"));
            if (document.containsKey("password")) this.setPassword(document.getString("password"));

            clans.add(this);
        });
    }

    /**
     * Save an Individual Clan
     */
    public void save() {
        thread.execute(() -> {
            Document clanDocument = new Document();
            clanDocument.put("name", this.getName());
            clanDocument.put("_id", this.getUuid().toString());

            clanDocument.put("leader", this.getLeader().getUuid().toString());
            clanDocument.put("members", this.getMembers().stream().map(ClanProfile::getUuid).collect(Collectors.toList()));
            clanDocument.put("captains", this.getCaptains().stream().map(ClanProfile::getUuid).collect(Collectors.toList()));

            clanDocument.put("description", this.getDescription());
            clanDocument.put("date-created", this.getDateCreated());

            clanDocument.put("elo", this.getElo());
            clanDocument.put("wins", this.getWins());
            clanDocument.put("losses", this.getLosses());
            clanDocument.put("winstreak", this.getWinStreak());
            clanDocument.put("highestwinstreak", this.getHighestWinStreak());

            if (this.getDescription() != null) clanDocument.put("description", this.getDescription());
            if (this.getPassword() != null) clanDocument.put("password", this.getPassword());

            collection.replaceOne(Filters.eq("_id", uuid.toString()), clanDocument, new ReplaceOptions().upsert(true));
        });
    }

    /**
     * Promote a Clan Profile to Captain
     *
     * @param member The {@link ClanProfile} being promoted
     */
    public void promote(final ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
        member.setClanProfileType(ClanProfileType.CAPTAIN);
        this.members.remove(member);
        this.captains.add(member);
        this.save();
        this.broadcast(CC.translate("&8[&cClan&8] &c" + player.getName() + " &7has been promoted to &c&lCaptain&7!"));
    }

    /**
     * Promote a Clan Profile to Leader
     *
     * @param member The {@link ClanProfile} being promoted
     */
    public void leader(final ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
        OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(leader.getUuid());

        this.broadcast(CC.translate("&8[&cClan&8] &c" + player.getName() + " &7has been promoted to &c&lLeader&7!"));
        this.broadcast(CC.translate("&8[&cClan&8] &c" + leaderPlayer.getName() + " &7has been demoted to &c&lCaptain&7!"));

        member.setClanProfileType(ClanProfileType.LEADER);
        leader.setClanProfileType(ClanProfileType.CAPTAIN);

        this.members.remove(member);
        this.captains.remove(member);
        this.captains.add(leader);
        this.leader = member;

        this.save();
    }

    /**
     * Demote a Clan Profile to Member
     *
     * @param member The {@link ClanProfile} being demoted
     */
    public void demote(final ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());

        member.setClanProfileType(ClanProfileType.MEMBER);

        this.members.add(member);
        this.captains.remove(member);

        this.save();
        this.broadcast(CC.translate("&8[&cClan&8] &c" + player.getName() + " &7has been demoted to &c&lMember&7!"));

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

        ClanProfile clanProfile = new ClanProfile(joiner.getUniqueId(), this, ClanProfileType.MEMBER);

        this.getMembers().add(clanProfile);

        if (clanInvite != null) profile.getClanInviteList().remove(clanInvite);
        this.broadcast(CC.RED + joiner.getDisplayName() + CC.GRAY + " has joined the clan!");
        this.save();

        profile.setClan(this);
        profile.setClanProfile(clanProfile);
        profile.save();
        profile.refreshHotbar();
    }

    /**
     * Make the player leave his clan
     *
     * @param leaver The player leaving the clan
     */
    public void leave(final Player leaver) {
        Profile profile = Profile.getByPlayer(leaver);

        this.thread.execute(() -> {
            ClanProfile clanProfile = profile.getClanProfile();
            switch (clanProfile.getClanProfileType()) {
                case MEMBER:
                    this.getMembers().remove(clanProfile);
                    profile.setClan(null);
                    profile.setClanProfile(null);
                    profile.save();
                    profile.refreshHotbar();
                    this.save();
                    this.broadcast(CC.RED + leaver.getDisplayName() + CC.GRAY + " has left the clan!");
                    break;
                case CAPTAIN:
                    this.getCaptains().remove(clanProfile);
                    profile.setClan(null);
                    profile.setClanProfile(null);
                    profile.save();
                    profile.refreshHotbar();
                    this.save();
                    this.broadcast(CC.RED + leaver.getDisplayName() + CC.GRAY + " has left the clan!");
                    break;
                case LEADER:
                    throw new IllegalArgumentException("Leader can not leave the clan!");
            }
        });
    }

    /**
     * Kick a Player from the Clan
     *
     * @param player The player getting kicked from the Clan
     */
    public void kick(Player player) {


    }

    /**
     * Ban a Player from the Clan
     *
     * @param player The player getting banned from the Clan
     */
    public void ban(Player player) {


    }


    /**
     * Load our Clan Leaderboards
     */
    public static void updateClanLeaderboards() {
        if (!getClanLeaderboards().isEmpty()) getClanLeaderboards().clear();
        thread.execute(() -> {
            for ( Document document : collection.find().sort(Sorts.descending("elo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName((String) document.get("name"));
                leaderboardsAdapter.setElo((Integer) document.get("elo"));
                getClanLeaderboards().add(leaderboardsAdapter);
            }
        });
    }

    /**
     * Returns {@link List<ClanProfile>} of all
     * Members of a Clan
     */
    public List<ClanProfile> getAllMembers() {
        List<ClanProfile> list = new ArrayList<>();

        list.addAll(members);
        list.addAll(captains);
        list.add(leader);

        return list;
    }

    public List<Player> getOnlineMembers() {
        return getAllMembers().stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Send clan information to the player
     *
     * @param player The Player recieving the information
     * @param clan The Clan whose information is being sent
     */
    public void information(Player player) {
        this.getAllMembers().sort(Comparator.comparing(cm -> cm.getClanProfileType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        this.getAllMembers().forEach(cm -> playerNames.add((this.getLeader().getUuid().equals(cm.getUuid()) ? CC.DARK_GREEN + "***" : this.getCaptains().contains(cm.getUuid()) ? CC.DARK_GREEN + "*" : "") + colorName(cm.getUuid())));

        Locale.CLAN_INFO.toList().forEach(line -> {
            line = line
                    .replace("<clan_description>", this.getDescription())
                    .replace("<clan_elo>", String.valueOf(this.getElo() == 0 ? "[N/A]" : this.getElo()))
                    .replace("<clan_created>", this.getDateCreated())
                    .replace("<clan_members>", Strings.join(playerNames, CC.GRAY + ", "))
                    .replace("<clan_winstreak>", String.valueOf(this.getWinStreak()))
                    .replace("<clan_highest_winstreak>", String.valueOf(this.getHighestWinStreak()))
                    .replace("<clan_members_size>", String.valueOf(this.getAllMembers().size()));

            player.sendMessage(line);
        });
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
     * Get an Invite using the player
     *
     * @param player The player whose invite we are getting
     * @return {@link ClanInvite}
     */
    public ClanInvite getInvite(Player player) {
        Profile profile = Profile.getByPlayer(player);
        for ( ClanInvite invite : profile.getClanInviteList())
            if (invite.getClan().equals(this)) {
                if (invite.hasExpired()) {
                    return null;
                }
                return invite;
            }
        return null;
    }

}

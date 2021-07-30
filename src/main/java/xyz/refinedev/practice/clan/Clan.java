package xyz.refinedev.practice.clan;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.DateUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
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

    private final List<ClanProfile> members;
    private final List<ClanProfile> captains;
    private final List<UUID> bannedPlayers;

    private final String name;
    private final UUID uuid;

    private ClanProfile leader;
    private String description = "This is the default Description, use /clan setdesc <text> to setup the description for your clan.";
    private String password;
    private String dateCreated;

    private int maxMembers;

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
        this.uuid = uuid;
        this.members = new ArrayList<>();
        this.captains = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
        this.leader = new ClanProfile(leader, this, ClanProfileType.LEADER);
        this.dateCreated = DateUtil.getFormattedDate(System.currentTimeMillis());

        this.maxMembers  = 25;

        Profile profile = Profile.getByUuid(leader);
        if (profile != null) {
            profile.setClan(this);
        }

        this.clans.add(this);
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
     * We use this to init our clans and setup Tasks
     */
    public static void  preload() {
        //First of all Load up the Leaderboards
        Clan.updateClanLeaderboards();
        //Register a Task that will clear the expired Invites
        TaskUtil.runTimerAsync(() -> {
            Profile.getProfiles().values().forEach(clan -> clan.getClanInviteList().removeIf(ClanInvite::hasExpired));
        }, 100L, 100L);

        //Then register a Task that will update the leaderboards frequently
        TaskUtil.runTimerAsync(Clan::updateClanLeaderboards, 600L, 600L);
        //Fetch clans directly from mongo
        Clan.fetchClans();
    }

    /**
     * Load all our Clans
     */
    public static void fetchClans() {
        thread.execute(() -> {
            for ( Document document : collection.find() ) {

                if (document == null) return;

                UUID uuid =  UUID.fromString(document.getString("_id"));
                UUID leader = UUID.fromString(document.getString("leader"));
                String name = document.getString("name");

                if (uuid == null || name == null || leader == null) continue;

                Clan clan = new Clan(name, leader, uuid);
                clan.load();
            }
        });
    }

    /**
     * Delete the clan from the mongo
     */
    public void deleteClan() {
        //Reset the Profile's clans and shit
        this.getAllMembers().forEach(member -> {
            Profile playerProfile = Profile.getByUuid(member.getUuid());

            playerProfile.setClan(null);
            playerProfile.setClanProfile(null);

            if (playerProfile.getPlayer().isOnline()) {
                if (playerProfile.isInLobby()) {
                    playerProfile.refreshHotbar();
                }
                playerProfile.getPlayer().sendMessage(Locale.CLAN_DISBANDED.toString());
            }
        });

        //Then clear it up from the Main Data
        this.clans.remove(this);
        this.members.clear();
        this.captains.clear();

        collection.deleteOne(Filters.eq("_id", uuid.toString()));
    }

    /**
     * Load an Individual Clan
     */
    public void load() {
        thread.execute(() -> {
            Document document = (Document) collection.find().filter(Filters.eq("_id", uuid.toString())).first();

            if (document == null) {
                this.save();
                return;
            }

            this.setElo(document.getInteger("elo"));
            this.setWins(document.getInteger("wins"));
            this.setLosses(document.getInteger("losses"));
            this.setWinStreak(document.getInteger("winstreak"));
            this.setHighestWinStreak(document.getInteger("highestwinstreak"));

            if (!document.getList("members", String.class).isEmpty()) {
                for ( String memberUUID : document.getList("members", String.class) ) {
                    UUID uuid = Array.GSON.fromJson(memberUUID, UUID.class);
                    ClanProfile member = new ClanProfile(uuid, this, ClanProfileType.MEMBER);

                    Profile profile = Profile.getByUuid(uuid);
                    profile.setClanProfile(member);

                    this.getMembers().add(member);
                }
            }

            if (!document.getList("captains", String.class).isEmpty()) {
                for ( String captainUUID : document.getList("captains", String.class) ) {
                    UUID uuid = Array.GSON.fromJson(captainUUID, UUID.class);
                    ClanProfile captain = new ClanProfile(uuid, this, ClanProfileType.CAPTAIN);

                    Profile profile = Profile.getByUuid(uuid);
                    profile.setClanProfile(captain);

                    this.getCaptains().add(captain);
                }
            }

            this.bannedPlayers.addAll(document.getList("banned", String.class).stream().map(UUID::fromString).collect(Collectors.toList()));

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
            List<String> members = new ArrayList<>();
            List<String> captains = new ArrayList<>();
            List<String> banned = new ArrayList<>();

            if (!this.members.isEmpty()) this.members.forEach(member -> members.add(Array.GSON.toJson(member)));
            if (!this.captains.isEmpty()) this.captains.forEach(captain -> captains.add(Array.GSON.toJson(captain)));
            if (!this.bannedPlayers.isEmpty()) this.bannedPlayers.forEach(player -> banned.add(Array.GSON.toJson(player)));

            Document clanDocument = new Document();
            clanDocument.put("name", this.getName());
            clanDocument.put("_id", this.getUuid().toString());

            clanDocument.put("leader", this.getLeader().getUuid().toString());
            clanDocument.put("members", members);
            clanDocument.put("captains", captains);
            clanDocument.put("banned", banned);

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
        member.setType(ClanProfileType.CAPTAIN);
        this.members.remove(member);
        this.captains.add(member);
        this.save();
        this.broadcast(Locale.CLAN_PROMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Captain"));
    }

    /**
     * Promote a Clan Profile to Leader
     *
     * @param member The {@link ClanProfile} being promoted
     */
    public void leader(final ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
        OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(leader.getUuid());

        this.broadcast(Locale.CLAN_PROMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Leader"));
        this.broadcast(Locale.CLAN_DEMOTE.toString().replace("<player>", leaderPlayer.getName()).replace("<role>", "Captain"));

        member.setType(ClanProfileType.LEADER);
        leader.setType(ClanProfileType.CAPTAIN);

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

        member.setType(ClanProfileType.MEMBER);

        this.members.add(member);
        this.captains.remove(member);

        this.save();
        this.broadcast(Locale.CLAN_DEMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Member"));

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

        if (this.getAllMembers().size() >= this.maxMembers) {
            joiner.sendMessage(CC.translate("&7This clan has exceeded the max member limit, you are unable to join currently!"));
            return;
        }

        ClanProfile clanProfile = new ClanProfile(joiner.getUniqueId(), this, ClanProfileType.MEMBER);

        this.getMembers().add(clanProfile);

        if (clanInvite != null) profile.getClanInviteList().remove(clanInvite);
        this.broadcast(Locale.CLAN_JOIN.toString().replace("<player>", joiner.getName()));
        this.save();

        profile.setClan(this);
        profile.setClanProfile(clanProfile);
        profile.refreshHotbar();
        profile.save();
    }

    /**
     * Make the player leave his clan
     *
     * @param leaver The player leaving the clan
     */
    public void leave(final Player leaver) {
        Profile profile = Profile.getByPlayer(leaver);
        ClanProfile clanProfile = profile.getClanProfile();

        if (clanProfile.getType() == ClanProfileType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        if (clanProfile.getType() == ClanProfileType.CAPTAIN) this.captains.remove(clanProfile);
        else this.members.remove(clanProfile);
        profile.setClan(null);
        profile.setClanProfile(null);
        profile.save();
        profile.refreshHotbar();
        this.save();
        this.broadcast(Locale.CLAN_LEFT.toString().replace("<player>", leaver.getName()));
    }

    /**
     * Kick a Player from the Clan
     *
     * @param uuid The uuid getting kicked from the Clan
     */
    public void kick(UUID uuid) {
        Profile profile = Profile.getByUuid(uuid);
        ClanProfile clanProfile = profile.getClanProfile();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (clanProfile.getType() == ClanProfileType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        if (clanProfile.getType() == ClanProfileType.CAPTAIN) this.captains.remove(clanProfile);
        else this.members.remove(clanProfile);
        profile.setClan(null);
        profile.setClanProfile(null);
        profile.save();
        if (player.isOnline()) profile.refreshHotbar();
        this.save();
        this.broadcast(Locale.CLAN_KICKED.toString().replace("<player>", player.getName()));
    }

    /**
     * Ban a Player from the Clan
     *
     * @param uuid The uuid getting banned from the Clan
     */
    public void ban(UUID uuid) {
        Profile profile = Profile.getByUuid(uuid);
        ClanProfile clanProfile = profile.getClanProfile();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (clanProfile.getType() == ClanProfileType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        if (clanProfile.getType() == ClanProfileType.CAPTAIN) this.captains.remove(clanProfile);
        else this.members.remove(clanProfile);
        profile.setClan(null);
        profile.setClanProfile(null);
        profile.save();
        if (player.isOnline()) profile.refreshHotbar();
        this.save();
        this.broadcast(Locale.CLAN_BANNED.toString().replace("<player>", player.getName()));
        this.bannedPlayers.add(uuid);
    }


    /**
     * Load our Clan Leaderboards
     */
    public static void updateClanLeaderboards() {
        if (!getClanLeaderboards().isEmpty()) getClanLeaderboards().clear();
        thread.execute(() -> {
            for ( Document document : collection.find().sort(Sorts.descending("elo")).limit(10).into(new ArrayList<>()) ) {
                LeaderboardsAdapter leaderboardsAdapter = new LeaderboardsAdapter();
                leaderboardsAdapter.setName(document.getString("name"));
                leaderboardsAdapter.setElo(document.getInteger("elo"));
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
        return getAllMembers().stream().map(ClanProfile::getUuid).map(Bukkit::getPlayer)
               .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Send clan information to the player
     *
     * @param player The Player recieving the information
     * @param clan The Clan whose information is being sent
     */
    public void information(Player player) {
        this.getAllMembers().sort(Comparator.comparing(cm -> cm.getType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        this.getAllMembers().forEach(cm -> playerNames.add((this.getLeader().getUuid().equals(cm.getUuid()) ? CC.RED + "***" : this.getCaptains().contains(cm.getUuid()) ? CC.RED + "*" : "") + colorName(cm.getUuid())));

        Locale.CLAN_INFO.toList().forEach(line -> {
            line = line
                    .replace("<clan_name>", this.getName())
                    .replace("<clan_description>", this.getDescription())
                    .replace("<clan_elo>", String.valueOf(this.getElo() == 0 ? "[N/A]" : this.getElo()))
                    .replace("<clan_created>", this.getDateCreated())
                    .replace("<clan_members_limit>", String.valueOf(this.maxMembers))
                    .replace("<clan_members>", Strings.join(playerNames, CC.GRAY + ", "))
                    .replace("<clan_winstreak>", String.valueOf(this.getWinStreak()))
                    .replace("<clan_leader>", Profile.getByUuid(this.getLeader().getUuid()).getName())
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

    public boolean isLeader(UUID uuid) {
        return this.getLeader().getUuid().equals(uuid);
    }

    public boolean isCaptain(UUID uuid) {
        return this.getCaptains().stream().map(ClanProfile::getUuid).anyMatch(unique -> unique.equals(uuid));
    }
}

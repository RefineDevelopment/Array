package xyz.refinedev.practice.managers;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.ClanRoleType;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/12/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class ClanManager {

    private final Array plugin;
    private final MongoCollection<Document> collection;

    //Clan UUID -> Clan
    private final Map<UUID, Clan> clans = new HashMap<>();
    //Player UUID -> ClanProfile
    private final Map<UUID, ClanProfile> profileMap = new HashMap<>();

    public void init() {
        plugin.submitToThread(() -> {
            for ( Document document : collection.find() ) {

                if (document == null) return;
                if (document.getString("clan") == null) continue; 
                
                Clan clan = Array.GSON.fromJson(document.getString("clan"), Clan.class);
                this.setupMap(clan);
            }
        });
    }

    /**
     * Load the members into the hashmap
     *
     * @param clan {@link Clan} the clan being loaded in the map
     */
    public void setupMap(Clan clan) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            this.clans.put(clan.getUniqueId(), clan);

            for ( ClanProfile profile : clan.getAllMembers() ) {
                this.profileMap.put(profile.getUniqueId(), profile);
            }
        });
    }

    /**
     * Save an Individual Clan
     *
     * @param clan {@link Clan} the clan we need to save
     */
    public void save(Clan clan) {
        Document document = new Document("clan", Array.GSON.toJson(clan));
        plugin.submitToThread(() -> collection.replaceOne(Filters.eq("_id", clan.getUniqueId().toString()), document, new ReplaceOptions().upsert(true)));
    }

    /**
     * Delete the clan from existence
     */
    public void delete(Clan clan) {
        for ( ClanProfile member : clan.getAllMembers() ) {
            Player player = this.plugin.getServer().getPlayer(member.getUniqueId());

            Profile profile = this.plugin.getProfileManager().getProfileByUUID(member.getUniqueId());
            profile.setClan(null);

            if (player != null && player.isOnline()) {
                if (profile.isInLobby()) {
                    this.plugin.getProfileManager().refreshHotbar(profile);
                }
                player.sendMessage(Locale.CLAN_DISBANDED.toString());
            }
            this.plugin.getProfileManager().save(profile);
        }

        clan.getMembers().clear();
        clan.getCaptains().clear();

        this.clans.remove(clan.getUniqueId(), clan);
        this.plugin.submitToThread(() -> collection.deleteOne(Filters.eq("_id", clan.getUniqueId().toString())));
    }


    /**
     * Send clan information to the player
     *
     * @param clan   The clan we are utilizing
     * @param player The Player receiving the information
     */
    public void information(Clan clan, Player player) {
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(clan.getLeader().getUniqueId());
        clan.getAllMembers().sort(Comparator.comparing(cm -> cm.getType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        clan.getAllMembers().forEach(cm -> playerNames.add((
        clan.getLeader().getUniqueId().equals(cm.getUniqueId()) ? CC.RED + "**" : // If its a leader then two stars
        clan.getCaptains().contains(cm) ? CC.RED + "*" : //If a captain then one star
        "") + colorName(cm.getUniqueId()))); // if just a member then no stars lel

        Locale.CLAN_INFO.toList().forEach(line -> {
            line = line
                    .replace("<clan_name>", clan.getName())
                    .replace("<clan_description>", clan.getDescription())
                    .replace("<clan_elo>", String.valueOf(clan.getElo() == 0 ? "[N/A]" : clan.getElo()))
                    .replace("<clan_created>", clan.getDateCreated())
                    .replace("<clan_members_limit>", String.valueOf(clan.getMaxMembers()))
                    .replace("<clan_members>", Strings.join(playerNames, CC.GRAY + ", "))
                    .replace("<clan_winstreak>", String.valueOf(clan.getWinStreak()))
                    .replace("<clan_leader>", offlinePlayer.getName())
                    .replace("<clan_highest_winstreak>", String.valueOf(clan.getHighestWinStreak()))
                    .replace("<clan_members_size>", String.valueOf(clan.getAllMembers().size()));

            player.sendMessage(line);
        });
    }

    /**
     * Get a player's formatted name for the list
     *
     * @param uuid The UUID of the player
     * @return     {@link String}
     */
    private String colorName(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.isOnline() ? CC.GREEN + offlinePlayer.getName() : CC.RED + offlinePlayer.getName();
    }

    /**
     * Promote a Clan Profile to Captain
     *
     * @param clan   The clan we are utilizing
     * @param member The {@link ClanProfile} being promoted
     */
    public void promote(Clan clan, ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUniqueId());

        member.setType(ClanRoleType.CAPTAIN);
        clan.getMembers().remove(member);
        clan.getCaptains().add(member);
        clan.broadcast(Locale.CLAN_PROMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Captain"));

        this.save(clan);
    }

    /**
     * Promote a Clan Profile to Leader
     *
     * @param clan   The clan we are utilizing
     * @param member The {@link ClanProfile} being promoted
     */
    public void leader(Clan clan, ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUniqueId());
        OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(clan.getLeader().getUniqueId());

        clan.broadcast(Locale.CLAN_PROMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Leader"));
        clan.broadcast(Locale.CLAN_DEMOTE.toString().replace("<player>", leaderPlayer.getName()).replace("<role>", "Captain"));

        //Change the roles
        member.setType(ClanRoleType.LEADER);
        clan.getLeader().setType(ClanRoleType.CAPTAIN);

        //Remove the new leader from being a member or captain
        clan.getMembers().remove(member);
        clan.getCaptains().remove(member);

        //Add the old leader as a captain
        clan.getCaptains().add(clan.getLeader());
        //Finally assign the new leader as a leader
        clan.setLeader(member);

        this.save(clan);
    }

    /**
     * Demote a Clan Profile to Member
     *
     * @param clan   The clan we are utilizing
     * @param member The {@link ClanProfile} being demoted
     */
    public void demote(Clan clan, ClanProfile member) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUniqueId());

        member.setType(ClanRoleType.MEMBER);

        clan.getMembers().add(member);
        clan.getCaptains().remove(member);

        this.save(clan);
        clan.broadcast(Locale.CLAN_DEMOTE.toString().replace("<player>", player.getName()).replace("<role>", "Member"));
    }

    /**
     * Invite a player to the clan
     *
     * @param clan   The clan we are utilizing
     * @param target The player being invited
     */
    public void invite(Clan clan, Player target) {
        OfflinePlayer leader = Bukkit.getOfflinePlayer(clan.getLeader().getUniqueId());
        ClanInvite invite = new ClanInvite(target.getUniqueId());
        clan.getInvites().put(target.getUniqueId(), invite);

        List<String> strings = new ArrayList<>();
        strings.add(Locale.CLAN_INVITED.toString().replace("<leader>", leader.getName()));
        strings.add(Locale.CLAN_CLICK_TO_JOIN.toString());

        for ( String string : strings ) {
            Clickable message = new Clickable(string, Locale.CLAN_INVITE_HOVER.toString(), "/clan join " + leader.getName());
            message.sendToPlayer(target);
        }
    }

    /**
     * Make the player invited, join the clan
     *
     * @param clan   The clan we are utilizing
     * @param joiner The player joining the clan
     */
    public void join(Clan clan, Player joiner, ClanInvite clanInvite) {
        Profile profile = this.plugin.getProfileManager().getProfileByPlayer(joiner);

        if (clanInvite != null && !clan.getInvites().containsKey(joiner.getUniqueId())) {
            joiner.sendMessage(CC.translate("&7You are not invited to this clan or your invite expired!"));
            return;
        }

        if (clan.getAllMembers().size() >= clan.getMaxMembers()) {
            joiner.sendMessage(CC.translate("&7This clan has exceeded the max member limit, you are unable to join currently!"));
            return;
        }

        // Create a ClanProfile and have it hooked up to the player and their profile
        ClanProfile clanProfile = new ClanProfile(joiner.getUniqueId(), clan, ClanRoleType.MEMBER);
        clan.getMembers().add(clanProfile);

        profile.setClan(clan.getUniqueId());
        if (clanInvite != null) clan.getInvites().remove(clanInvite);

        this.plugin.getProfileManager().refreshHotbar(profile);
        this.plugin.getProfileManager().save(profile);

        this.profileMap.put(joiner.getUniqueId(), clanProfile);

        clan.broadcast(Locale.CLAN_JOIN.toString().replace("<player>", joiner.getName()));
        this.save(clan);
    }

    /**
     * Make the player leave his clan
     *
     * @param clan   The clan we are utilizing
     * @param leaver The player leaving the clan
     */
    public void leave(Clan clan, Player leaver) {
        Preconditions.checkNotNull(leaver, "Player can not be null!");

        Profile profile = this.plugin.getProfileManager().getProfileByPlayer(leaver);
        ClanProfile clanProfile = this.profileMap.get(leaver.getUniqueId());

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);

        profile.setClan(null);

        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.profileMap.remove(leaver.getUniqueId(), clanProfile);

        this.save(clan);
        clan.broadcast(Locale.CLAN_LEFT.toString().replace("<player>", leaver.getName()));
    }

    /**
     * Kick a Player from the Clan
     *
     * @param clan The clan we are utilizing
     * @param uuid The uniqueId getting kicked from the Clan
     */
    public void kick(Clan clan, UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        Preconditions.checkNotNull(player, "Player can not be null!");

        Profile profile = this.plugin.getProfileManager().getProfileByUUID(uuid);
        ClanProfile clanProfile = this.profileMap.get(uuid);

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);

        profile.setClan(null);

        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.profileMap.remove(player.getUniqueId(), clanProfile);

        this.save(clan);
        clan.broadcast(Locale.CLAN_KICKED.toString().replace("<player>", player.getName()));
    }

    /**
     * Ban a Player from the Clan
     *
     * @param clan The clan we are utilizing
     * @param uuid The uniqueId getting banned from the Clan
     */
    public void ban(Clan clan, UUID uuid) {
        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(uuid);
        Preconditions.checkNotNull(player, "Player can not be null!");

        Profile profile = this.plugin.getProfileManager().getProfileByUUID(uuid);
        ClanProfile clanProfile = this.profileMap.get(uuid);

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);

        profile.setClan(null);

        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.profileMap.remove(player.getUniqueId(), clanProfile);

        clan.broadcast(Locale.CLAN_BANNED.toString().replace("<player>", player.getName()));
        clan.getBannedPlayers().add(uuid);

        this.save(clan);
    }

    /**
     * Get a clan by its name
     *
     * @param name The name of the Clan
     * @return     {@link Clan}
     */
    public Clan getByName(String name) {
        return clans.values()
                .stream()
                .filter(clan -> clan.getName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);
    }

    /**
     * Get a clan by its UUID
     *
     * @param uuid The UUID of the Clan
     * @return     {@link Clan}
     */
    public Clan getByUUID(UUID uuid) {
        if (uuid == null) return null;
        return clans.get(uuid);
    }

    /**
     * Get a ClanProfile by its associated UUID
     *
     * @param uuid The UUID of the Clan or Player
     * @return     {@link ClanProfile}
     */
    public ClanProfile getProfileByUUID(UUID uuid) {
        return profileMap.get(uuid);
    }

    /**
     * Get a clan by its leader
     *
     * @param player The leader of the Clan
     * @return       {@link Clan}
     */
    public Clan getByLeader(UUID player) {
        return clans.values()
                .stream()
                .filter(c -> c.getLeader().getUniqueId().equals(player))
                .findFirst().orElse(null);
    }

    /**
     * Get an Invite using the player
     *
     * @param clan   The clan we are checking
     * @param player The player whose invite we are getting
     * @return       {@link ClanInvite}
     */
    public ClanInvite getInvite(Clan clan, Player player) {
        return clan.getInvites().get(player.getUniqueId());
    }

    public boolean isInFight(Clan clan) {
        return clan.getAllMembers()
                .stream()
                .map(ClanProfile::getUniqueId)
                .map(this.plugin.getProfileManager()::getProfileByUUID)
                .filter(Objects::nonNull)
                .anyMatch(profile -> profile.isInFight() && profile.getMatch().getQueueType().equals(QueueType.CLAN));
    }
}

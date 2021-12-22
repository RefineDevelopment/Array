package xyz.refinedev.practice.managers;

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
import xyz.refinedev.practice.task.clan.ClanInviteExpireTask;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;

import java.util.*;
import java.util.stream.Collectors;

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

    //Player UUID -> Clan UUID
    private final Map<UUID, ClanProfile> profileMap = new HashMap<>();
    private final Map<UUID, UUID> clanMap = new HashMap<>();
    private final Map<UUID, Clan> clans = new HashMap<>();

    public void init() {
        new ClanInviteExpireTask(plugin).runTaskTimerAsynchronously(plugin, 100L, 100L);

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
                this.clanMap.put(profile.getUuid(), clan.getUniqueId());
                this.profileMap.put(profile.getUuid(), profile);
            }
        });
    }

    /**
     * Does the player with the given uuid have a clan
     *
     * @param uuid {@link UUID} the uuid of the player
     * @return {@link Boolean} returns true if a clan is present
     */
    public boolean hasClan(UUID uuid) {
        return this.clanMap.containsKey(uuid);
    }

    /**
     * Returns a player's clan using their uuid
     *
     * @param uuid {@link UUID} the uuid of the player
     * @return {@link Clan} returns the clan the of player
     */
    public Clan getByPlayer(UUID uuid) {
        UUID clan = this.clanMap.get(uuid);
        if (clan == null) return null;

        return clans.get(clan);
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
            Profile profile = plugin.getProfileManager().getByUUID(member.getUuid());

            if (profile.isInLobby()) {
                plugin.getProfileManager().refreshHotbar(profile);
            }

            if (profile.getPlayer() != null && profile.getPlayer().isOnline()) {
                profile.getPlayer().sendMessage(Locale.CLAN_DISBANDED.toString());
            }
        }

        clan.getMembers().clear();
        clan.getCaptains().clear();

        this.clans.remove(clan.getUniqueId(), clan);
        this.clanMap.values().removeIf(entry -> entry.equals(clan.getUniqueId()));
        this.plugin.submitToThread(() -> collection.deleteOne(Filters.eq("_id", clan.getUniqueId().toString())));
    }


    /**
     * Send clan information to the player
     *
     * @param clan   The clan we are utilizing
     * @param player The Player receiving the information
     */
    public void information(Clan clan, Player player) {
        clan.getAllMembers().sort(Comparator.comparing(cm -> cm.getType().getWeight()));

        List<String> playerNames = new ArrayList<>();
        clan.getAllMembers().forEach(cm -> playerNames.add((clan.getLeader().getUuid().equals(cm.getUuid()) ? CC.RED + "***" : clan.getCaptains().contains(cm) ? CC.RED + "*" : "") + colorName(cm.getUuid())));

        Locale.CLAN_INFO.toList().forEach(line -> {
            line = line
                    .replace("<clan_name>", clan.getName())
                    .replace("<clan_description>", clan.getDescription())
                    .replace("<clan_elo>", String.valueOf(clan.getElo() == 0 ? "[N/A]" : clan.getElo()))
                    .replace("<clan_created>", clan.getDateCreated())
                    .replace("<clan_members_limit>", String.valueOf(clan.getMaxMembers()))
                    .replace("<clan_members>", Strings.join(playerNames, CC.GRAY + ", "))
                    .replace("<clan_winstreak>", String.valueOf(clan.getWinStreak()))
                    .replace("<clan_leader>", plugin.getProfileManager().getByUUID(clan.getLeader().getUuid()).getName())
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
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());

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
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());
        OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(clan.getLeader().getUuid());

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
        OfflinePlayer player = Bukkit.getOfflinePlayer(member.getUuid());

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
        Profile profile = plugin.getProfileManager().getByPlayer(target);
        profile.getClanInviteList().add(new ClanInvite(clan));
        OfflinePlayer leader = Bukkit.getOfflinePlayer(clan.getLeader().getUuid());

        List<String> invite = new ArrayList<>();
        invite.add(Locale.CLAN_INVITED.toString().replace("<leader>", leader.getName()));
        invite.add(Locale.CLAN_CLICK_TO_JOIN.toString());

        for ( String string : invite ) {
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
        Profile profile = plugin.getProfileManager().getByPlayer(joiner);

        if (!profile.getClanInviteList().contains(clanInvite)) {
            joiner.sendMessage(CC.translate("&7You are not invited to this clan or your invite expired!"));
            return;
        }

        if (clan.getAllMembers().size() >= clan.getMaxMembers()) {
            joiner.sendMessage(CC.translate("&7This clan has exceeded the max member limit, you are unable to join currently!"));
            return;
        }

        ClanProfile clanProfile = new ClanProfile(joiner.getUniqueId(), clan, ClanRoleType.MEMBER);

        clan.getMembers().add(clanProfile);

        if (clanInvite != null) profile.getClanInviteList().remove(clanInvite);

        clan.broadcast(Locale.CLAN_JOIN.toString().replace("<player>", joiner.getName()));
        this.save(clan);

        this.clanMap.put(joiner.getUniqueId(), clan.getUniqueId());
        this.profileMap.put(joiner.getUniqueId(), clanProfile);
        this.plugin.getProfileManager().refreshHotbar(profile);
        this.plugin.getProfileManager().save(profile);
    }

    /**
     * Make the player leave his clan
     *
     * @param clan   The clan we are utilizing
     * @param leaver The player leaving the clan
     */
    public void leave(Clan clan, Player leaver) {
        Profile profile = plugin.getProfileManager().getByPlayer(leaver);
        ClanProfile clanProfile = this.profileMap.get(leaver.getUniqueId());

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);

        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.clanMap.remove(leaver.getUniqueId(), clan.getUniqueId());
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
        Profile profile = plugin.getProfileManager().getByUUID(uuid);
        ClanProfile clanProfile = this.profileMap.get(uuid);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);

        this.plugin.getProfileManager().save(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.clanMap.remove(uuid, clan.getUniqueId());
        this.profileMap.remove(uuid, clanProfile);

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
        Profile profile = plugin.getProfileManager().getByUUID(uuid);
        ClanProfile clanProfile = this.profileMap.get(uuid);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (clanProfile.getType() == ClanRoleType.LEADER) {
            throw new IllegalArgumentException("Leader can not leave the clan!");
        }

        clan.getCaptains().remove(clanProfile);
        clan.getMembers().remove(clanProfile);
        
        plugin.getProfileManager().save(profile);
        plugin.getProfileManager().refreshHotbar(profile);

        this.save(clan);
        clan.broadcast(Locale.CLAN_BANNED.toString().replace("<player>", player.getName()));
        clan.getBannedPlayers().add(uuid);
    }

    /**
     * Get a clan by its name
     *
     * @param name The name of the Clan
     * @return     {@link Clan}
     */
    public Clan getByName(String name) {
        return clans.values().stream().filter(clan -> clan.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    /**
     * Get a clan by its UUID
     *
     * @param uuid The UUID of the Clan
     * @return     {@link Clan}
     */
    public Clan getByUUID(UUID uuid) {
        return clans.get(uuid);
    }

    /**
     * Get a clan by its leader
     *
     * @param player The leader of the Clan
     * @return       {@link Clan}
     */
    public Clan getByLeader(UUID player) {
        return clans.values().stream().filter(c -> c.getLeader().getUuid().equals(player)).findFirst().orElse(null);
    }

    /**
     * Get an Invite using the player
     *
     * @param clan   The clan we are checking
     * @param player The player whose invite we are getting
     * @return       {@link ClanInvite}
     */
    public ClanInvite getInvite(Clan clan, Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        for ( ClanInvite invite : profile.getClanInviteList() )
            if (invite.getClan().equals(clan) && !invite.hasExpired()) {
                return invite;
            }
        return null;
    }
}

package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.PartyInvite;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.task.party.PartyPublicTask;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.PartyHelperUtil;

import java.util.*;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class PartyManager {

    private final Array plugin;
    private final Map<UUID, Party> parties = new HashMap<>();

    public void init() {
        PartyPublicTask partyPublicTask = new PartyPublicTask(plugin);
        partyPublicTask.runTaskTimer(plugin, 1000L, 1000L);
    }

    /**
     * Invite a specific player to a party
     *
     * @param player The player being in invited
     * @param party The party being utilized
     */
    public void invite(Player player, Party party) {
        party.getInvites().put(player.getUniqueId(), new PartyInvite(player.getUniqueId()));

        List<String> strings = new ArrayList<>();

        strings.add(Locale.PARTY_INVITED.toString().replace("<leader>", party.getLeader().getUsername()));
        strings.add(Locale.PARTY_CLICK_TO_JOIN.toString());

        for ( String string : strings ) {
            Clickable clickable = new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername());
            clickable.sendToPlayer(player);
        }

        party.broadcast(Locale.PARTY_PLAYER_INVITED.toString().replace("<invited>", player.getName()));
    }

    /**
     * Ban the targeted player from the party
     *
     * @param target The player being banned
     * @param party The party being utilized
     */
    public void ban(Player target, Party party) {
        party.getBanned().add(target.getUniqueId());
    }

    /**
     * Unban the targeted player form the party
     *
     * @param target The player being unbanned
     * @param party The party being utilized
     */
    public void unban(Player target, Party party) {
        party.broadcast(Locale.PARTY_UNBANNED.toString().replace("<target>", target.getName()));
        party.getBanned().remove(target.getUniqueId());
    }

    /**
     * Execute party join for the player
     *
     * @param player The player joining the party
     * @param party The party being utilized
     */
    public void join(Player player, Party party) {
        TeamPlayer teamPlayer = new TeamPlayer(player);

        Profile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
        profile.setParty(party.getUniqueId());

        party.getTeamPlayers().add(teamPlayer);
        party.getKits().put(player.getUniqueId(), PartyHelperUtil.getRandomClass());

        this.plugin.getNameTagHandler().reloadPlayer(player);
        this.plugin.getNameTagHandler().reloadOthersFor(player);

        party.getInvites().values().removeIf(invite -> invite.getUniqueId().equals(player.getUniqueId()));
        party.broadcast(Locale.PARTY_PLAYER_JOINED.toString().replace("<joiner>", player.getName()));

        for (Player partyMember : party.getPlayers()) {
            Profile partyProfile = this.plugin.getProfileManager().getProfile(partyMember.getUniqueId());

            this.plugin.getProfileManager().handleVisibility(partyProfile, player);
            this.plugin.getProfileManager().refreshHotbar(partyProfile);
        }

        if (this.isFighting(party.getUniqueId())) {
            Match match = this.getMatch(party.getUniqueId());
            this.plugin.getMatchManager().addSpectator(match, player, null);
        }
    }

    /**
     * Execute party leave tasks for the player leaving
     *
     * @param player The player leaving
     * @param party The party being utilized
     */
    public void leave(Player player, Party party) {
        Profile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
        profile.setParty(null);

        if (profile.isInMatch()) {
            Match match = profile.getMatch();
            this.plugin.getMatchManager().handleDeath(match, player, null, true);

            if (profile.isSpectating()) {
                this.plugin.getMatchManager().removeSpectator(match, player);
            }
        }

        party.getTeamPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        party.getPlayers().remove(player);
        party.getKits().remove(player.getUniqueId());

        for (Player teamPlayer : party.getPlayers()) {
            Profile teamProfile = this.plugin.getProfileManager().getProfile(teamPlayer.getUniqueId());

            this.plugin.getProfileManager().handleVisibility(teamProfile, player);
            this.plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        this.plugin.getProfileManager().handleVisibility(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.plugin.getNameTagHandler().reloadPlayer(player);
        this.plugin.getNameTagHandler().reloadOthersFor(player);

        party.broadcast(Locale.PARTY_PLAYER_LEFT.toString().replace("<leaver>", player.getName()));
    }

    /**
     * Execute party kick tasks for the player being kicked
     *
     * @param player The player being kicked
     * @param party The party being utilized
     */
    public void kick(Player player, Party party) {
        Profile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
        profile.setParty(null);

        if (profile.isInMatch()) {
            Match match = profile.getMatch();
            this.plugin.getMatchManager().handleDeath(match, player, null, true);

            if (profile.isSpectating()) {
                this.plugin.getMatchManager().removeSpectator(match, player);
            }
        }

        party.getTeamPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        party.getPlayers().remove(player);
        party.getKits().remove(player.getUniqueId());

        for (Player teamPlayer : party.getPlayers()) {
            Profile teamProfile = this.plugin.getProfileManager().getProfile(teamPlayer.getUniqueId());

            this.plugin.getProfileManager().handleVisibility(teamProfile, player);
            this.plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        this.plugin.getProfileManager().handleVisibility(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.plugin.getNameTagHandler().reloadPlayer(player);
        this.plugin.getNameTagHandler().reloadOthersFor(player);

        party.broadcast(Locale.PARTY_PLAYER_KICKED.toString().replace("<leaver>", player.getName()));
        player.sendMessage(Locale.PARTY_KICKED.toString());
    }

    /**
     * Make the targeted player, the leader of the party
     *
     * @param player The New Leader of the Party
     * @param party The party being utilized
     */
    public void leader(Player player, Party party) {
        Profile profile = this.plugin.getProfileManager().getProfile(party.getLeader().getUniqueId());
        Profile targetProfile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
        TeamPlayer teamPlayer =  party.getTeamPlayers().stream().filter(t -> t.getUniqueId().equals(player.getUniqueId())).findAny().orElse(new TeamPlayer(player));

        party.setLeader(teamPlayer);
        party.broadcast(Locale.PARTY_PROMOTED.toString().replace("<promoted>", player.getName()));

        this.plugin.getProfileManager().refreshHotbar(profile);
        this.plugin.getProfileManager().refreshHotbar(targetProfile);
    }

    /**
     * Execute tasks for disbanding the party
     *
     * @param party The party being disbanded
     */
    public void disband(Party party) {
        Profile leaderProfile = this.plugin.getProfileManager().getProfile(party.getLeader().getUniqueId());
        leaderProfile.getDuelRequests().values().removeIf(DuelRequest::isParty);

        for ( Player player : party.getPlayers() ) {
            Profile profile = this.plugin.getProfileManager().getProfile(player.getUniqueId());
            profile.setParty(null);

            if (this.isFighting(party.getUniqueId())) {
                this.plugin.getMatchManager().handleDeath(this.getMatch(party.getUniqueId()), player, null, true);
            }

            if (profile.isInLobby() || profile.isInQueue()) {
                this.plugin.getProfileManager().handleVisibility(profile);
                this.plugin.getProfileManager().refreshHotbar(profile);

                this.plugin.getNameTagHandler().reloadPlayer(player);
                this.plugin.getNameTagHandler().reloadOthersFor(player);
            }
        }
        party.broadcast(Locale.PARTY_DISBANDED.toString());
        party.setDisbanded(true);

        this.parties.remove(party.getUniqueId());
    }

    /**
     * Get a party by its {@link UUID}
     *
     * @param uuid {@link UUID} party uniqueId
     * @return {@link Party} party
     */
    public Party getPartyByUUID(UUID uuid) {
        return this.parties.get(uuid);
    }

    /**
     * Is the given party in a {@link Match}
     *
     * @param uuid {@link UUID} party uniqueId
     * @return {@link Boolean} returns true if party is in a match
     */
    public boolean isFighting(UUID uuid) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Party party = this.parties.get(uuid);
        return party.getPlayers().stream()
                .map(profileManager::getProfile)
                .anyMatch(profile -> profile.isInFight() || profile.isInTournament());
    }

    /**
     * Get a party's match by the party's uniqueId
     *
     * @param uuid {@link UUID} party uniqueId
     * @return {@link Match} party match
     */
    public Match getMatch(UUID uuid) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Party party = this.parties.get(uuid);
        return party.getPlayers()
                .stream()
                .map(profileManager::getProfile)
                .filter(profile -> profile.isInFight() || profile.isInTournament())
                .map(Profile::getMatch)
                .findAny()
                .orElse(null);
    }

}

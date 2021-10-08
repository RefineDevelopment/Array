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
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.task.PartyInviteExpireTask;
import xyz.refinedev.practice.task.PartyPublicTask;
import xyz.refinedev.practice.util.chat.Clickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private final List<Party> parties = new ArrayList<>();

    public void init() {
        new PartyInviteExpireTask(plugin).runTaskTimer(plugin, 100L, 100L);
        new PartyPublicTask(plugin).runTaskTimer(plugin, 1000L, 1000L);
    }

    /**
     * Invite a specific player to a party
     *
     * @param player The player being in invited
     * @param party The party being utilized
     */
    public void invite(Player player, Party party) {
        party.getInvites().add(new PartyInvite(player.getUniqueId()));

        List<String> strings = new ArrayList<>();

        strings.add(Locale.PARTY_INVITED.toString().replace("<leader>", party.getLeader().getUsername()));
        strings.add(Locale.PARTY_CLICK_TO_JOIN.toString());

        strings.forEach(string -> new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername()).sendToPlayer(player));

        party.broadcast(Locale.PARTY_PLAYER_INVITED.toString().replace("<invited>", player.getName()));
    }

    /**
     * Ban the targeted player from the party
     *
     * @param target The player being banned
     * @param party The party being utilized
     */
    public void ban(Player target, Party party) {
        party.getBanned().add(target);
    }

    /**
     * Unban the targeted player form the party
     *
     * @param target The player being unbanned
     * @param party The party being utilized
     */
    public void unban(Player target, Party party) {
        party.broadcast(Locale.PARTY_UNBANNED.toString().replace("<target>", target.getName()));
        party.getBanned().remove(target);
    }

    /**
     * Execute party join for the player
     *
     * @param player The player joining the party
     * @param party The party being utilized
     */
    public void join(Player player, Party party) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setParty(party);

        party.getKits().put(player.getUniqueId(), this.getRandomClass());

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);

        party.getInvites().removeIf(invite -> invite.getUuid().equals(player.getUniqueId()));
        party.getTeamPlayers().add(new TeamPlayer(player));
        party.broadcast(Locale.PARTY_PLAYER_JOINED.toString().replace("<joiner>", player.getName()));

        for (Player teamPlayer : party.getPlayers()) {
            Profile teamProfile = plugin.getProfileManager().getByUUID(teamPlayer.getUniqueId());

            plugin.getProfileManager().handleVisibility(teamProfile, player);
            plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        if (party.isFighting()) {
            Match match = party.getMatch();
            match.addSpectator(player, null);
        }
    }

    /**
     * Execute party leave tasks for the player leaving
     *
     * @param player The player leaving
     * @param party The party being utilized
     */
    public void leave(Player player, Party party) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setParty(null);

        party.getTeamPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        party.getPlayers().remove(player);
        party.getKits().remove(player.getUniqueId());


        party.broadcast(Locale.PARTY_PLAYER_LEFT.toString().replace("<leaver>", player.getName()));

        if (profile.isInFight()) {
            profile.getMatch().handleDeath(player, null, true);

            if (profile.isSpectating()) {
                profile.getMatch().removeSpectator(player);
            }
        }

        for (Player teamPlayer : party.getPlayers()) {
            Profile teamProfile = plugin.getProfileManager().getByUUID(teamPlayer.getUniqueId());

            plugin.getProfileManager().handleVisibility(teamProfile, player);
            plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        plugin.getProfileManager().handleVisibility(profile);
        plugin.getProfileManager().refreshHotbar(profile);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Execute party kick tasks for the player being kicked
     *
     * @param player The player being kicked
     * @param party The party being utilized
     */
    public void kick(Player player, Party party) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setParty(null);

        party.getTeamPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        party.getPlayers().remove(player);
        party.getKits().remove(player.getUniqueId());


        party.broadcast(Locale.PARTY_PLAYER_KICKED.toString().replace("<leaver>", player.getName()));

        if (profile.isInFight()) {
            profile.getMatch().handleDeath(player, null, true);

            if (profile.isSpectating()) {
                profile.getMatch().removeSpectator(player);
            }
        }

        for (Player teamPlayer : party.getPlayers()) {
            Profile teamProfile = plugin.getProfileManager().getByUUID(teamPlayer.getUniqueId());

            plugin.getProfileManager().handleVisibility(teamProfile, player);
            plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        plugin.getProfileManager().handleVisibility(profile);
        plugin.getProfileManager().refreshHotbar(profile);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);

        player.sendMessage(Locale.PARTY_KICKED.toString());
    }

    /**
     * Make the targeted player, the leader of the party
     *
     * @param player The New Leader of the Party
     * @param party The party being utilized
     */
    public void leader(Player player, Party party) {
        Profile profile = plugin.getProfileManager().getByUUID(party.getLeader().getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        TeamPlayer teamPlayer =  party.getTeamPlayers().stream().filter(t -> t.getUniqueId().equals(player.getUniqueId())).findAny().orElse(new TeamPlayer(player));

        party.setLeader(teamPlayer);
        party.broadcast(Locale.PARTY_PROMOTED.toString().replace("<promoted>", player.getName()));

       plugin.getProfileManager().refreshHotbar(profile);
       plugin.getProfileManager().refreshHotbar(targetProfile);
    }

    /**
     * Execute tasks for disbanding the party
     *
     * @param party The party being utilized
     */
    public void disband(Party party) {
        party.broadcast(Locale.PARTY_DISBANDED.toString());

        Profile leaderProfile = plugin.getProfileManager().getByUUID(party.getLeader().getUniqueId());
        leaderProfile.getSentDuelRequests().values().removeIf(DuelRequest::isParty);

        for ( Player player : party.getPlayers() ) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.setParty(null);

            if (profile.isInFight()) {
                profile.getMatch().handleDeath(player, null, true);
            }

            if (profile.isInLobby() || profile.isInQueue()) {
                plugin.getProfileManager().handleVisibility(profile);
                plugin.getProfileManager().refreshHotbar(profile);

                plugin.getNameTagHandler().reloadPlayer(player);
                plugin.getNameTagHandler().reloadOthersFor(player);
            }
        }
        party.setDisbanded(true);
        this.parties.remove(party);
    }

    public String getRandomClass() {
        List<String> classes = Arrays.asList(
                "Diamond",
                "Bard",
                "Archer",
                "Rogue"
        );
        Collections.shuffle(classes);
        return classes.get(0);
    }
}

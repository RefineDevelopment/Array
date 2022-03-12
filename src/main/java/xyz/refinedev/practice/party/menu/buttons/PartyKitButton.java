package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.managers.ArenaManager;
import xyz.refinedev.practice.managers.MatchManager;
import xyz.refinedev.practice.managers.PartyManager;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/13/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyKitButton extends Button {

    private final PartyEventType partyEventType;
    private final Kit kit;

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        return kit.getDisplayIcon();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param plugin {@link org.bukkit.plugin.Plugin} Array
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        MatchManager matchManager = plugin.getMatchManager();
        PartyManager partyManager = plugin.getPartyManager();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!profile.hasParty()) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        player.closeInventory();

        Party party = partyManager.getPartyByUUID(profile.getParty());
        if (party.getTeamPlayers().size() < 2) {
            player.sendMessage(Locale.PARTY_EVENT_NEED.toString());
            return;
        }

        Arena arena = arenaManager.getByKit(this.kit);
        if (arena == null) {
            player.sendMessage(Locale.ERROR_NO_ARENAS.toString());
            return;
        }
        arena.setActive(true);

        if (this.partyEventType == PartyEventType.PARTY_FFA) {
            Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));

            List<Player> players = new ArrayList<>(party.getPlayers());
            Match match = new FFAMatch(team, this.kit, arena);

            for (Player otherPlayer : players) {
                if (team.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                    continue;
                }
                team.getTeamPlayers().add(new TeamPlayer(otherPlayer));
            }
            matchManager.start(match);
            return;
        }

        Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
        Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

        List<Player> players = new ArrayList<>(party.getPlayers());

        //Doing a thorough shuffle, because some people complaining it keeps the same team
        Collections.shuffle(players);
        Collections.reverse(players);
        Collections.shuffle(players);
        Collections.reverse(players);

        Match match = matchManager.createTeamKitMatch(teamA, teamB, this.kit, arena);

        for (Player shuffledPlayer : players) {
            if (!teamA.getLeader().getUniqueId().equals(shuffledPlayer.getUniqueId())) {
                if (teamB.getLeader().getUniqueId().equals(shuffledPlayer.getUniqueId())) {
                    continue;
                }
                if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                    teamB.getTeamPlayers().add(new TeamPlayer(shuffledPlayer));
                } else {
                    teamA.getTeamPlayers().add(new TeamPlayer(shuffledPlayer));
                }
            }
        }
        matchManager.start(match);
    }
}

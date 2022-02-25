package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
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



    private final Array plugin = this.getPlugin();
    private final PartyEventType partyEventType;
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return kit.getDisplayIcon();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (!profile.hasParty()) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        player.closeInventory();

        Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());
        if (party.getTeamPlayers().size() < 2) {
            player.sendMessage(Locale.PARTY_EVENT_NEED.toString());
            return;
        }

        Arena arena = plugin.getArenaManager().getByKit(this.kit);
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
            plugin.getMatchManager().start(match);
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

        Match match = plugin.getMatchManager().createTeamKitMatch(teamA, teamB, this.kit, arena);

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
        plugin.getMatchManager().start(match);
    }
}

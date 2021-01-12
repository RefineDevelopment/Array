package me.array.ArrayPractice.tournament;

import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class TournamentLisener implements Listener {

    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player);
        if (profile.isInTournament(player)) {
            Profile test = Profile.getByUuid(Tournament.CURRENT_TOURNAMENT.getParticipants().get(1).getLeader().getPlayer());
            Match match =test.getMatch();
            if (match.spectators.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player);
        if (profile.isInTournament(player)) {
            Profile test = Profile.getByUuid(Tournament.CURRENT_TOURNAMENT.getParticipants().get(1).getLeader().getPlayer());
            Match match =test.getMatch();
            if (match.spectators.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
    public void onHunger(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByUuid(player);
        if (profile.isInTournament(player)) {
            Profile test = Profile.getByUuid(Tournament.CURRENT_TOURNAMENT.getParticipants().get(1).getLeader().getPlayer());
            Match match =test.getMatch();
            if (match.spectators.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}

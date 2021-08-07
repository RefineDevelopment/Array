package xyz.refinedev.practice.tournament;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

public class TournamentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        Tournament tournament = Tournament.getCurrentTournament();
        if (tournament != null) return;

        if (!profile.hasParty() && profile.isInTournament()) {
            tournament.leave(event.getPlayer());
        } else if (profile.hasParty() && profile.getParty().isInTournament()) {
            if (tournament.isFighting()) return;
            Party party = profile.getParty();
            tournament.leave(party);
        }
    }
}

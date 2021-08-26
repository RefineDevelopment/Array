package xyz.refinedev.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.chat.CC;

import java.util.List;
import java.util.stream.Collectors;

public class PartyListener implements Listener {

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party == null) return;

        List<Player> partyPlayers = party.getPlayers().stream().filter(p -> !p.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());

        if (partyPlayers.isEmpty()) {
            party.disband();
            return;
        }

        if (party.isLeader(player.getUniqueId())) {
            party.leader(player, partyPlayers.get(0));
            party.broadcast(CC.translate("&e" + party.getLeader().getUsername() + " has been randomly promoted to leader because the previous leader left."));
        }
        party.leave(player, false);

        if (Tournament.getCurrentTournament() == null || !Tournament.getCurrentTournament().isParticipating(player.getUniqueId())) return;

        Tournament.getCurrentTournament().leave(party);
    }
}

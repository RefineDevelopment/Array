package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.chat.CC;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartyListener implements Listener {

    private final Array plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Party party = profile.getParty();

        if (party == null) return;

        List<Player> partyPlayers = party.getPlayers().stream().filter(p -> !p.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());

        if (partyPlayers.isEmpty()) {
            plugin.getPartyManager().disband(party);
            return;
        }

        if (party.isLeader(player.getUniqueId())) {
            plugin.getPartyManager().leader(partyPlayers.get(0), party);
            party.broadcast(CC.translate("&b" + party.getLeader().getUsername() + " &ehas been randomly promoted to leader because the previous leader left."));
        }
        plugin.getPartyManager().leave(player, party);
    }
}

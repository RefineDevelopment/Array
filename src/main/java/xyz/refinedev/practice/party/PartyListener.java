package xyz.refinedev.practice.party;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.util.chat.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PartyListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPartyChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party != null) {
            if (chatMessage.startsWith("@") || profile.getSettings().isPartyChat()) {
                event.setCancelled(true);
                String chat = Locale.PARTY_CHAT_FORMAT.toString()
                        .replace("<player_displayname>", player.getDisplayName())
                        .replace("<player_name>", player.getName())
                        .replace("<message>", chatMessage.replace("@", ""));

                party.broadcast(chat);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party != null) {
            List<Player> partyPlayers = party.getPlayers().stream().filter(p -> !p.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());
            if (partyPlayers.isEmpty()) party.disband();
            if (party.isLeader(player.getUniqueId())) {
                party.leader(player, partyPlayers.get(0));
                party.broadcast(CC.translate("&e" + party.getLeader().getUsername() + " has been randomly promoted to leader because the previous leader left."));
            }
            party.leave(player, false);

            if (Tournament.getCurrentTournament() != null && Tournament.getCurrentTournament().isParticipating(player.getUniqueId())) {
                Tournament.getCurrentTournament().leave(party);
            }
        }
    }
}

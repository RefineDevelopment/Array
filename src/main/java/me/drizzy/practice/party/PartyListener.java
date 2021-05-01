package me.drizzy.practice.party;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
                String message = CC.translate("&8[&cParty&8] &c" + player.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.GREEN + chatMessage.replace("@", ""));
                party.broadcast(message);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        if (profile != null && profile.getParty() != null) {
            if (profile.getParty().isLeader(event.getPlayer().getUniqueId())) {
                profile.getParty().leader(event.getPlayer(), profile.getParty().getPlayers().get(0));
                profile.getParty().leave(event.getPlayer(), false);
            } else {
                profile.getParty().leave(event.getPlayer(), false);
            }
            if (profile.getParty() !=null && Tournament.CURRENT_TOURNAMENT !=null && Tournament.CURRENT_TOURNAMENT.isParticipating(event.getPlayer())) {
                Tournament.CURRENT_TOURNAMENT.leave(profile.getParty());
            }

        }
    }
}

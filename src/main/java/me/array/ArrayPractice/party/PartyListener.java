package me.array.ArrayPractice.party;

import me.array.ArrayPractice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class PartyListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onPartyChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party != null) {
            if (chatMessage.startsWith("@")) {
                event.setCancelled(true);
                String message = CC.translate("&7» " + Practice.getInstance().getCoreHook().getPlayerPrefix(player) + player.getName() + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + chatMessage.replace("@", ""));
                party.broadcast(message);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        if (profile != null && profile.getParty() != null) {
            if (profile.getParty().isLeader(event.getPlayer().getUniqueId())) {
                profile.getParty().disband();
            }
            else {
                profile.getParty().leave(event.getPlayer(), false);
            }
        }
    }
}

package me.array.ArrayPractice.party;

import me.array.ArrayPractice.Array;
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
    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party != null) {
            if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
                event.setCancelled(true);
                String message=CC.translate("&b&lPARTY &8» ") + Array.get().getChat().getPlayerPrefix(player) + ChatColor.WHITE + player.getName() + ChatColor.GRAY + ": " + ChatColor.AQUA + chatMessage.replaceFirst("!", "").replaceFirst("@", "");
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

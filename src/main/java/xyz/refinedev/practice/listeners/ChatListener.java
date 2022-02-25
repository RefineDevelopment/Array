package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final Array plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        
        Profile profile = plugin.getProfileManager().getProfile(player);
        if (!profile.getSettings().isClanChat() && !chatMessage.startsWith("$") && !chatMessage.startsWith(".")) return;
        if (!profile.hasClan()) return;
        
        Clan clan = plugin.getClanManager().getByUUID(profile.getClan());

        event.setCancelled(true);
        clan.broadcast(Locale.CLAN_CHAT_FORMAT.toString()
                .replace("<player_displayname>", player.getDisplayName())
                .replace("<player_name>", player.getName())
                .replace("<player_rankname>", plugin.getCoreHandler().getFullName(player))
                .replace("<message>", chatMessage.replace("$", "")));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPartyChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (!chatMessage.startsWith("@") && !profile.getSettings().isPartyChat()) return;
        if (!profile.hasParty()) return;
        
        Party party = plugin.getPartyManager().getPartyByUUID(profile.getParty());

        event.setCancelled(true);
        party.broadcast(Locale.PARTY_CHAT_FORMAT.toString()
                .replace("<player_displayname>", player.getDisplayName())
                .replace("<player_name>", player.getName())
                .replace("<player_rankname>", plugin.getCoreHandler().getFullName(player))
                .replace("<message>", chatMessage.replace("@", "")));
    }
}

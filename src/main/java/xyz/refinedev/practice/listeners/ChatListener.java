package xyz.refinedev.practice.listeners;

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

public class ChatListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        Profile profile = Profile.getByPlayer(player);
        Clan clan = profile.getClan();

        if (clan == null) return;
        if (!profile.getSettings().isClanChat() && !chatMessage.startsWith("$") && !chatMessage.startsWith(".")) return;

        event.setCancelled(true);
        clan.broadcast(Locale.CLAN_CHAT_FORMAT.toString()
                .replace("<player_displayname>", player.getDisplayName())
                .replace("<player_name>", player.getName())
                .replace("<player_rankname>", plugin.getRankManager().getCoreType().getCoreAdapter().getFullName(player))
                .replace("<message>", chatMessage.replace("$", "")));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPartyChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (!chatMessage.startsWith("@") && !profile.getSettings().isPartyChat()) return;
        if (party == null) return;

        event.setCancelled(true);
        party.broadcast(Locale.PARTY_CHAT_FORMAT.toString()
                .replace("<player_displayname>", player.getDisplayName())
                .replace("<player_name>", player.getName())
                .replace("<player_rankname>", plugin.getRankManager().getCoreType().getCoreAdapter().getFullName(player))
                .replace("<message>", chatMessage.replace("@", "")));
    }
}

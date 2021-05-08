package me.drizzy.practice.clan.listener;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ClanListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();

        Profile clansProfile = Profile.getByPlayer(player);

        if (chatMessage.startsWith(".") || chatMessage.startsWith("$") && clansProfile.getClan() != null) {
            event.setCancelled(true);
            String message = CC.translate("&8[&cClan&8] " + Array.getInstance().getRankManager().getFullName(player) + CC.GRAY + " » " + CC.WHITE + chatMessage.replace("$", "").replace(".", ""));
            clansProfile.getClan().broadcast(message);
        }

    }
}

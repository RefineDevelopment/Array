package xyz.refinedev.practice.clan.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.util.chat.CC;


public class ClanListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chatMessage = event.getMessage();

        Profile clansProfile = Profile.getByPlayer(player);

        if ((clansProfile.getSettings().isClanChat() || chatMessage.startsWith("$")) && clansProfile.hasClan()) {
            event.setCancelled(true);
            String message = CC.translate("&8[&cClan&8] " + Rank.getRankType().getFullName(player) + CC.GRAY + " » " + CC.WHITE + chatMessage.replace("$", "").replace(".", ""));
            clansProfile.getClan().broadcast(message);

        }

    }
}

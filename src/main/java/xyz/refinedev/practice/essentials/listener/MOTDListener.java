package xyz.refinedev.practice.essentials.listener;

import xyz.refinedev.practice.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.refinedev.practice.util.chat.CC;

public class MOTDListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Essentials.getMeta().isMotdEnabled()) {
            Player player = event.getPlayer();
            for (int i = 0; i < 300; i++) player.sendMessage(" ");
            for ( String string : Essentials.getMotd()) {
                player.sendMessage(CC.translate(string.replace("%splitter%", "┃").replace("|", "┃")));
            }
        }
    }
}

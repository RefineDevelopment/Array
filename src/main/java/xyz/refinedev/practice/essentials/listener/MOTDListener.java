package xyz.refinedev.practice.essentials.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;

public class MOTDListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getConfigHandler().isJOIN_MESSAGE_ENABLED()) {
            Player player = event.getPlayer();
            //Clear Chat
            for (int i = 0; i < 300; i++) player.sendMessage(" ");

            //Send the message
            for ( String string : plugin.getConfigHandler().getJOIN_MESSAGE()) {
                player.sendMessage(CC.translate(string.replace("%splitter%", "┃").replace("|", "┃")));
            }
        }
    }
}

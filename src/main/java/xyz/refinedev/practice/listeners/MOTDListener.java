package xyz.refinedev.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.refinedev.practice.Array;

public class MOTDListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getConfigHandler().isJOIN_MESSAGE_ENABLED()) {
            Player player = event.getPlayer();

            for (int i = 0; i < 300; i++) player.sendMessage(" ");

            plugin.getConfigHandler().getJOIN_MESSAGE().stream().map(string -> string.replace("%splitter%", "┃").replace("|", "┃")).forEach(player::sendMessage);
        }
    }
}

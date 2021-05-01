package me.blazingtide.pistol.listener;

import lombok.AllArgsConstructor;
import me.blazingtide.pistol.Pistol;
import me.blazingtide.pistol.board.PistolBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class PistolListener implements Listener {

    private final Pistol pistol;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

//        Bukkit.getScheduler().runTaskAsynchronously(pistol.getPlugin(), () -> {
//
//        });
        final PistolBoard board = new PistolBoard(pistol, player);

        board.init();
        pistol.getBoards().put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        pistol.getBoards().remove(player.getUniqueId());
    }

}

package me.drizzy.practice.match.task;

import lombok.SneakyThrows;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchState;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchBridgeStartTask extends BukkitRunnable {

    private final TheBridgeMatch match;
    private int ticks;

    public MatchBridgeStartTask(Match match) {
        this.match = (TheBridgeMatch) match;
    }

    @SneakyThrows
    @Override
    public void run() {
        int seconds=5 - ticks;
        if (match.isEnding()) {
            cancel();
            return;
        }

        if (seconds == 2) {
            match.getPlayers().forEach(PlayerUtil::allowMovement);
            match.setState(MatchState.FIGHTING);
            match.setStartTimestamp(System.currentTimeMillis());
            match.broadcastMessage(CC.GREEN + "The round has started!");
            match.broadcastSound(Sound.NOTE_BASS);
            for ( Player player : match.getPlayers() ) {
                player.getInventory().remove(Material.INK_SACK);
                player.updateInventory();
                for ( Player oPlayer : match.getPlayers() ) {
                    if (player.equals(oPlayer))
                        continue;
                    player.showPlayer(oPlayer);
                    oPlayer.showPlayer(player);
                }
            }
            cancel();
            return;
        }
        match.broadcastMessage(CC.AQUA + (seconds - 2) + "...");
        match.broadcastSound(Sound.NOTE_PLING);
        match.playFirework();
        ticks++;

    }

}

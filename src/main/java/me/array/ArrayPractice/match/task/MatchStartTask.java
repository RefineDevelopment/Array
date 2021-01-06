package me.array.ArrayPractice.match.task;

import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchState;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartTask extends BukkitRunnable {

    private final Match match;
    private int ticks;

    public MatchStartTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        int seconds = 3 - ticks;

        if (match.isEnding()) {
            cancel();
            return;
        }

        if (match.isHCFMatch() || match.isKoTHMatch()) {
            if (seconds == 0) {
                match.setState(MatchState.FIGHTING);
                match.setStartTimestamp(System.currentTimeMillis());
                match.broadcastMessage(CC.GREEN + "Match Started!");
                match.broadcastMessage("");
                match.broadcastMessage(CC.AQUA + CC.BOLD + "Reminder: " + CC.WHITE + "Butterfly clicking is " + CC.AQUA + "discouraged" + CC.WHITE + " and could result in a" + CC.AQUA + " ban." + CC.AQUA + " Use at own risk.");
                match.broadcastSound(Sound.LEVEL_UP);
                cancel();
                return;
            }

            match.broadcastMessage(CC.WHITE + "Starting in " + CC.AQUA + seconds + CC.WHITE +  "...");
            match.broadcastSound(Sound.NOTE_PLING);
        } else {
            if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isParkour()) {
                if (seconds == 0) {
                    match.getPlayers().forEach(PlayerUtil::allowMovement);
                    match.setState(MatchState.FIGHTING);
                    match.setStartTimestamp(System.currentTimeMillis());
                    match.broadcastMessage(CC.GREEN + "The round has started!");
                    match.broadcastSound(Sound.NOTE_BASS);
                    for (Player player : match.getPlayers()) {
                        player.getInventory().remove(Material.INK_SACK);
                        player.updateInventory();
                        for (Player oPlayer : match.getPlayers()) {
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
            } else {
                if (seconds == 0) {
                    match.setState(MatchState.FIGHTING);
                    match.setStartTimestamp(System.currentTimeMillis());
                    match.broadcastMessage(CC.GREEN + "Match Started!");
                    match.broadcastMessage("");
                    match.broadcastMessage(CC.AQUA + CC.BOLD + "Reminder: " + CC.WHITE + "Butterfly clicking is " + CC.AQUA + "discouraged" + CC.WHITE + " and could result in a" + CC.AQUA + " ban." + CC.AQUA + " Use at own risk.");
                    match.broadcastSound(Sound.LEVEL_UP);
                    match.getPlayers().forEach(player -> {
                        player.getInventory().remove(Material.INK_SACK);
                        player.updateInventory();
                    });
                    cancel();
                    return;
                }

                match.broadcastMessage(CC.WHITE + "Starting in " + CC.AQUA + seconds + CC.WHITE +  "...");
                match.broadcastSound(Sound.NOTE_PLING);
            }
        }

        ticks++;
    }

}

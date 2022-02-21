package xyz.refinedev.practice.task.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.timer.impl.BridgeArrowTimer;
import xyz.refinedev.practice.util.timer.impl.EnderpearlTimer;

import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/16/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchCooldownTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        int seconds;

        EnderpearlTimer enderpearlTimer = this.plugin.getTimerHandler().getTimer(EnderpearlTimer.class);
        for ( UUID uuid : enderpearlTimer.getCooldowns().keySet()) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player == null) continue;

            long time = enderpearlTimer.getRemaining(player);
            seconds = (int) Math.round((double) time / 1000.0);

            player.setLevel(seconds);
            player.setExp((float)time / 15000.0f);
        }
        BridgeArrowTimer bridgeArrowTimer = this.plugin.getTimerHandler().getTimer(BridgeArrowTimer.class);
        for (UUID uuid : bridgeArrowTimer.getCooldowns().keySet()) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player == null) continue;

            long time = bridgeArrowTimer.getRemaining(player);
            seconds = (int) Math.round((double)time / 1000.0);

            player.setLevel(seconds);
            player.setExp((float) time / 4000.0f);
        }
    }
}

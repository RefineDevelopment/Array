package me.drizzy.practice.events.types.sumo.task;

import lombok.RequiredArgsConstructor;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.events.types.sumo.SumoState;
import me.drizzy.practice.profile.Profile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/15/2021
 * Project: 1.0 Array
 */

@RequiredArgsConstructor
public class SumoWaterTask extends BukkitRunnable {

    private final Sumo sumo;

    @Override
    public void run() {
        if (sumo == null || !sumo.isFighting() || sumo.getRemainingPlayers().size() <= 1) {
            return;
        }

        for ( Player player : sumo.getRemainingPlayers()) {
            if (player == null || !Profile.getByUuid(player.getUniqueId()).isInSumo()) {
                return;
            }

            Block body = player.getLocation().getBlock();
            Block head = body.getRelative(BlockFace.UP);
            if (body.getType() == Material.WATER || body.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                if (sumo.getState() == SumoState.ROUND_FIGHTING) {
                    sumo.handleDeath(player);
                    return;
                }
            }
        }
    }
}

package xyz.refinedev.practice.event.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 7/22/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventWaterTask extends BukkitRunnable {

    private final Array plugin;
    private final Event event;

    @Override
    public void run() {
        if (event == null || event.getRemainingPlayers().isEmpty() || event.getRemainingPlayers().size() <= 1) {
            return;
        }

        for ( Player player : event.getRemainingPlayers()) {
            if (player == null || !plugin.getProfileManager().getByUUID(player.getUniqueId()).isInEvent()) {
                return;
            }

            Block body = player.getLocation().getBlock();
            Block head = body.getRelative(BlockFace.UP);
            if (body.getType() == Material.WATER || body.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                if ((event.isSumoSolo() || event.isSumoTeam()) && event.getState() == EventState.ROUND_FIGHTING) {
                    event.handleDeath(player);
                    return;
                }
                if (event.isParkour()) {
                    //.teleport(match.getTeamPlayers(player).getParkourCheckpoint());
                }
            }
        }
    }
}
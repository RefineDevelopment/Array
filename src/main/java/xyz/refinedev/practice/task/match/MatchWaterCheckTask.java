package xyz.refinedev.practice.task.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;

@RequiredArgsConstructor
public class MatchWaterCheckTask extends BukkitRunnable {

    private final Array plugin;
    private final Match match;

    @Override
    public void run() {
        if (match == null || match.getAlivePlayers().isEmpty() || match.getAlivePlayers().size() <= 1) {
            this.cancel();
            return;
        }

        for (Player player : match.getAlivePlayers()) {
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            if (teamPlayer == null) continue;

            if (match.isEnding()) {
                this.cancel();
                return;
            }

            Block body = player.getLocation().getBlock();
            Block head = body.getRelative(BlockFace.UP);

            if (body.getType() == Material.WATER || body.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                if (match.getKit().getGameRules().isWaterKill() || match.getKit().getGameRules().isSumo()) {
                    teamPlayer.setAlive(false);
                    match.handleDeath(player, null, false);
                }
                if (match.getKit().getGameRules().isParkour()) {
                    player.teleport(match.getTeamPlayer(player).getParkourCheckpoint());
                }
            }
        }
    }
}

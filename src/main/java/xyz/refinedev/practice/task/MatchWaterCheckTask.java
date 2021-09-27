package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MatchWaterCheckTask extends BukkitRunnable {

    private final Match match;
    private final List<UUID> caught = new ArrayList<>();

    @Override
    public void run() {
        if (match == null || match.getAlivePlayers().isEmpty() || match.getAlivePlayers().size() <= 1) {
            caught.clear();
            this.cancel();
            return;
        }

        for (Player player : match.getAlivePlayers()) {
            if (player == null) continue;
            Profile profile = plugin.getProfileManager().getByPlayer(player);
            if (!profile.isInFight()) continue;
            if (this.caught.contains(player.getUniqueId())) continue;

            if (match.isEnding()) {
                caught.clear();
                this.cancel();
                return;
            }

            Block body = player.getLocation().getBlock();
            Block head = body.getRelative(BlockFace.UP);

            if (body.getType() == Material.WATER || body.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                if (match.getKit().getGameRules().isWaterKill() || match.getKit().getGameRules().isSumo()) {
                    this.caught.add(player.getUniqueId());
                    match.handleDeath(player, null, false);
                    continue;
                }
                if (match.getKit().getGameRules().isParkour()) {
                    this.caught.add(player.getUniqueId());
                    player.teleport(match.getTeamPlayer(player).getParkourCheckpoint());
                }
            }
        }
    }
}

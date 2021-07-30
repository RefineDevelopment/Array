package xyz.refinedev.practice.match.task;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.match.types.kit.BridgeMatch;
import xyz.refinedev.practice.util.other.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MatchBridgePlayerTask extends BukkitRunnable {

    BridgeMatch bridgeMatch;
    Player player;

    @Override
    public void run() {
        if(player == null) {
            cancel();
        }
        player.teleport(bridgeMatch.getTeamPlayer(player).getPlayerSpawn());
        bridgeMatch.setupPlayer(player);
        bridgeMatch.getCatcher().remove(player);
        PlayerUtil.allowMovement(player);
        PlayerUtil.allowMovement(player);
    }
}

package xyz.refinedev.practice.match.task;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.match.types.TheBridgeMatch;
import xyz.refinedev.practice.util.other.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class BridgePlayerTask extends BukkitRunnable {

    TheBridgeMatch bridgeMatch;
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

package me.drizzy.practice.match.task;

import lombok.AllArgsConstructor;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.util.PlayerUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BridgePlayerTask implements Runnable{

    TheBridgeMatch bridgeMatch;
    Player player;

    @Override
    public void run() {
        player.teleport(bridgeMatch.getTeamPlayer(player).getPlayerSpawn());
        bridgeMatch.setupPlayer(player);
        bridgeMatch.getCatcher().remove(player);
        PlayerUtil.allowMovement(player);
    }
}

package me.drizzy.practice.match.task;

import lombok.AllArgsConstructor;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class BedwarsPlayerTask implements Runnable{

    Match bridgeMatch;
    Player player;

    @Override
    public void run() {
        player.teleport(bridgeMatch.getTeamPlayer(player).getPlayerSpawn());
        bridgeMatch.setupPlayer(player);
        bridgeMatch.getCatcher().remove(player);
        PlayerUtil.allowMovement(player);
        PlayerUtil.allowMovement(player);
    }
}

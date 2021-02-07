package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars tp", permission = "practice.skywars.tp")
public class SkyWarsTpCommand {

    public void execute(Player player) {
        player.teleport(LocationUtil.deserialize(Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().get(0)));
        player.sendMessage(CC.GREEN + "Teleported to skywars's spawn location.");
    }

}

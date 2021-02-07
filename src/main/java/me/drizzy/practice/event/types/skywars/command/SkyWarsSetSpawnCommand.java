package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars setspawn", permission = "practice.skywars.setspawn")
public class SkyWarsSetSpawnCommand {

    public void execute(Player player) {
        Array.getInstance().getSkyWarsManager().getSkyWarsSpectators().add(LocationUtil.serialize(player.getLocation()));

        player.sendMessage(CC.GREEN + "Added skywars's spawn location.");

        Array.getInstance().getSkyWarsManager().save();
    }

}

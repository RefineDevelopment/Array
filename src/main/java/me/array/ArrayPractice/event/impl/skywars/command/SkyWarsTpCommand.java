package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.LocationUtil;
import org.bukkit.entity.Player;

@CommandMeta(label = "skywars tp", permission = "practice.skywars.tp")
public class SkyWarsTpCommand {

    public void execute(Player player) {
        player.teleport(LocationUtil.deserialize(Practice.getInstance().getSkyWarsManager().getSkyWarsSpectators().get(0)));
        player.sendMessage(CC.GREEN + "Teleported to skywars's spawn location.");
    }

}

package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "OITC tp", permission = "array.oitc")
public class OITCTpCommand {

    public void execute(Player player) {
        player.teleport(Array.getInstance().getOITCManager().getOITCSpectator());
        player.sendMessage(CC.GREEN + "Teleported to OITC's spawn location.");
    }

}

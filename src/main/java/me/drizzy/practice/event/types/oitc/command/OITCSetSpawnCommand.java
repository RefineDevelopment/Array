package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "OITC setspawn", permission = "array.oitc")
public class OITCSetSpawnCommand {

    public void execute(Player player) {
        Array.getInstance().getOITCManager().setOITCSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated OITC's spawn location.");

        Array.getInstance().getOITCManager().save();
    }

}

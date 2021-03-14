package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms tp", permission = "practice.lms")
public class LMSTpCommand {

    public void execute(Player player) {
        player.teleport(Array.getInstance().getLMSManager().getLmsSpectator());
        player.sendMessage(CC.GREEN + "Teleported to lms's spawn location.");
    }

}

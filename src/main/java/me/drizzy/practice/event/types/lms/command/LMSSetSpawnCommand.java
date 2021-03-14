package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms setspawn", permission = "practice.lms")
public class LMSSetSpawnCommand {

    public void execute(Player player) {
        Array.getInstance().getLMSManager().setLmsSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated lms's spawn location.");

        Array.getInstance().getLMSManager().save();
    }

}

package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms tp", permission = "practice.lms")
public class LMSTpCommand {

    public void execute(Player player) {
        player.teleport(Practice.get().getLMSManager().getLmsSpectator());
        player.sendMessage(CC.GREEN + "Teleported to lms's spawn location.");
    }

}

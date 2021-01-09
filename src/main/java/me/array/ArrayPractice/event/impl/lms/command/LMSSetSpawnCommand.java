package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms setspawn", permission = "practice.lms")
public class LMSSetSpawnCommand {

    public void execute(Player player) {
        Practice.getInstance().getLMSManager().setLmsSpectator(player.getLocation());

        player.sendMessage(CC.GREEN + "Updated lms's spawn location.");

        Practice.getInstance().getLMSManager().save();
    }

}

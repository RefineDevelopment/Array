package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "lms setspawn", permission = "array.dev")
public class LMSSetSpawnCommand {

    public void execute(Player player) {
        Array.getInstance().getLMSManager().setLmsSpawn(player.getLocation());
        player.sendMessage(CC.translate("&7Updated &cLMS's &7spawn location."));
        Array.getInstance().getLMSManager().save();
    }

}

package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "oitc cancel", permission = "array.oitc")
public class OITCCancelCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getOITCManager().getActiveOITC() == null) {
            sender.sendMessage(CC.RED + "There isn't an active OITC event.");
            return;
        }

        Array.getInstance().getOITCManager().getActiveOITC().end();
    }

}

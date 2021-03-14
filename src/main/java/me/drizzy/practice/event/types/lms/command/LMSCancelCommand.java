package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cancel", permission = "practice.lms")
public class LMSCancelCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getLMSManager().getActiveLMS() == null) {
            sender.sendMessage(CC.RED + "There isn't an active LMS event.");
            return;
        }

        Array.getInstance().getLMSManager().getActiveLMS().end();
    }

}

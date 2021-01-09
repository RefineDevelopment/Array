package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cancel", permission = "practice.lms")
public class LMSCancelCommand {

    public void execute(CommandSender sender) {
        if (Practice.getInstance().getLMSManager().getActiveLMS() == null) {
            sender.sendMessage(CC.RED + "There isn't an active LMS event.");
            return;
        }

        Practice.getInstance().getLMSManager().getActiveLMS().end();
    }

}

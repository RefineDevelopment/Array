package me.array.ArrayPractice.event.impl.lms.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cooldown", permission = "practice.lms")
public class LMSCooldownCommand {

    public void execute(CommandSender sender) {
        if (Practice.get().getLMSManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a LMS Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the LMS Event cooldown.");

        Practice.get().getLMSManager().setCooldown(new Cooldown(0));
    }

}

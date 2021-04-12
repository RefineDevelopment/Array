package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cooldown", permission = "array.staff")
public class LMSCooldownCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getLMSManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a LMS Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the LMS Event cooldown.");

        Array.getInstance().getLMSManager().setCooldown(new Cooldown(0));
    }

}

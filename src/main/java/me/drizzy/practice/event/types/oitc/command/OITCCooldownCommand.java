package me.drizzy.practice.event.types.oitc.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "OITC cooldown", permission = "array.oitc")
public class OITCCooldownCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getOITCManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a OITC Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "Successfully reset the OITC Event cooldown.");

        Array.getInstance().getOITCManager().setCooldown(new Cooldown(0));
    }

}

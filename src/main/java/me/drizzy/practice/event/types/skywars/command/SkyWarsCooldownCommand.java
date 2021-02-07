package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.Cooldown;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cooldown", permission = "practice.skywars.cooldown")
public class SkyWarsCooldownCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getSkyWarsManager().getCooldown().hasExpired()) {
            sender.sendMessage(CC.RED + "There isn't a SkyWars Event cooldown.");
            return;
        }

        sender.sendMessage(CC.GREEN + "You reset the SkyWars Event cooldown.");

        Array.getInstance().getSkyWarsManager().setCooldown(new Cooldown(0));
    }

}

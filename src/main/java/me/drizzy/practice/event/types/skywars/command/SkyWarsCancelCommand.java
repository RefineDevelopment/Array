package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.CC;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "skywars cancel", permission = "practice.skywars.cancel")
public class SkyWarsCancelCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getSkyWarsManager().getActiveSkyWars() == null) {
            sender.sendMessage(CC.RED + "There isn't an active SkyWars event.");
            return;
        }

        Array.getInstance().getSkyWarsManager().getActiveSkyWars().end();
    }

}

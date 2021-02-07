package me.drizzy.practice.array;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label={"version", "ver", "spigot"}, permission="")
public class VerCommand {
    public void execute(CommandSender commandSender) {
        commandSender.sendMessage(CC.translate("&8&m--------------------------------------"));
        commandSender.sendMessage(CC.translate("&7This server is running &b&lRaveSpigot"));
        commandSender.sendMessage(CC.translate("&7Made by Drizzy on version 1.2."));
        commandSender.sendMessage("");
        commandSender.sendMessage(CC.translate("&7Protocol Version: &b1.7x-1.12x"));
        commandSender.sendMessage(CC.translate("&7Knockback: &bAdvanced/Simple mode"));
        commandSender.sendMessage(CC.translate("&8&m--------------------------------------"));
    }
}

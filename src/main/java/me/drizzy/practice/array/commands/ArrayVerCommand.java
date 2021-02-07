package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.CC;

@CommandMeta(label={"array ver", "array version"})
public class ArrayVerCommand {
    public void execute(Player p) {
        p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        p.sendMessage(CC.translate("&7This server is running &bArray [Resolve Build]"));
        p.sendMessage(CC.translate("&7Array is made By Drizzy, Nick and Zentil"));
        p.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}

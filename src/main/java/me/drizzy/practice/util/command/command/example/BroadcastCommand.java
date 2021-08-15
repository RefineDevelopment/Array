package me.drizzy.practice.util.command.command.example;

import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "broadcast" })
public class BroadcastCommand
{
    public void execute(final CommandSender sender, final String message) {
        Bukkit.broadcastMessage(ChatColor.AQUA + message);
    }
}

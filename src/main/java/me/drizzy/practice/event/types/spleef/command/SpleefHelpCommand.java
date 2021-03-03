package me.drizzy.practice.event.types.spleef.command;

import me.drizzy.practice.util.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "practice.spleefhelp")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSLPEEF &8- &8&o(&7&o&7Information on how to use slpeef commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/slpeef cancel &8- &8&o(&7&o&7Cancel current Spleef Event"));
        player.sendMessage(Color.translate("&b/slpeef cooldown &8- &8&o(&7&o&7Reset the Spleef Event cooldown"));
        player.sendMessage(Color.translate("&b/slpeef host &8- &8&o(&7&o&7Host a Spleef Event"));
        player.sendMessage(Color.translate("&b/slpeef forcestart &8- &8&o(&7&o&7Forcestart a Spleef Event"));
        player.sendMessage(Color.translate("&b/slpeef join &8- &8&o(&7&o&7Join ongoing Spleef Event"));
        player.sendMessage(Color.translate("&b/slpeef leave &8- &8&o(&7&o&7Leave ongoing Spleef Event"));
        player.sendMessage(Color.translate("&b/slpeef tp &8- &8&o(&7&o&7Teleport to the Spleef Event Arena"));
        player.sendMessage(Color.translate("&b/slpeef setspawn  &8- &8&o(&7&o&7Set the spawns for Spleef Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

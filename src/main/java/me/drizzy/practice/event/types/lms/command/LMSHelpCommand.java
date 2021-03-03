package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"ffa", "ffa help", "lms", "lms help"}, permission = "practice.lms")
public class LMSHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lLMS/FFA &8- &8&o(&7&o&7Information on LMS/FFA Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/lms cancel &8- &8&o(&7&o&7Cancel current LMS/FFA Event"));
        player.sendMessage(Color.translate("&b/lms cooldown &8- &8&o(&7&o&7Reset the LMS/FFA Event cooldown"));
        player.sendMessage(Color.translate("&b/lms host &8- &8&o(&7&o&7Host a LMS/FFA Event"));
        player.sendMessage(Color.translate("&b/lms forcestart &8- &8&o(&7&o&7Forcestart a LMS/FFA Event"));
        player.sendMessage(Color.translate("&b/lms join &8- &8&o(&7&o&7Join ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&b/lms leave &8- &8&o(&7&o&7Leave ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&b/lms tp &8- &8&o(&7&o&7Teleport to the LMS/FFA Event Arena"));
        player.sendMessage(Color.translate("&b/lms setspawn  &8- &8&o(&7&o&7Set the spawns for LMS/FFA Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
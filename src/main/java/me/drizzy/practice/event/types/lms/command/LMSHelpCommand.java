package me.drizzy.practice.event.types.lms.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"ffa", "ffa help", "lms", "lms help"}, permission = "practice.lms")
public class LMSHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lLMS/FFA &7- &7Information on LMS/FFA Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/lms cancel &7- &7Cancel current LMS/FFA Event"));
        player.sendMessage(Color.translate("&7» &b/lms cooldown &7- &7Reset the LMS/FFA Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/lms host &7- &7Host a LMS/FFA Event"));
        player.sendMessage(Color.translate("&7» &b/lms forcestart &7- &7Forcestart a LMS/FFA Event"));
        player.sendMessage(Color.translate("&7» &b/lms join &7- &7Join ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&7» &b/lms leave &7- &7Leave ongoing LMS/FFA Event"));
        player.sendMessage(Color.translate("&7» &b/lms tp &7- &7Teleport to the LMS/FFA Event Arena"));
        player.sendMessage(Color.translate("&7» &b/lms setspawn  &7- &7Set the spawns for LMS/FFA Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}
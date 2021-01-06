package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "practice.spleefhelp")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSLPEEF &7- &7Information on how to use slpeef commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/slpeef cancel &7- &7Cancel current Spleef Event"));
        player.sendMessage(Color.translate("&7» &b/slpeef cooldown &7- &7Reset the Spleef Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/slpeef host &7- &7Host a Spleef Event"));
        player.sendMessage(Color.translate("&7» &b/slpeef join &7- &7Join ongoing Spleef Event"));
        player.sendMessage(Color.translate("&7» &b/slpeef leave &7- &7Leave ongoing Spleef Event"));
        player.sendMessage(Color.translate("&7» &b/slpeef tp &7- &7Teleport to the Spleef Event Arena"));
        player.sendMessage(Color.translate("&7» &b/slpeef setspawn  &7- &7Set the spawns for Spleef Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

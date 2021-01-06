package me.array.ArrayPractice.event.impl.spleef.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef", "spleef help"}, permission = "practice.spleefhelp")
public class SpleefHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&4&lSLPEEF &8- &7Information on how to use slpeef commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7/slpeef cancel &8- &7Cancel current Spleef Event"));
        player.sendMessage(Color.translate("&7/slpeef cooldown &8- &7Reset the Spleef Event cooldown"));
        player.sendMessage(Color.translate("&7/slpeef host &8- &7Host a Spleef Event"));
        player.sendMessage(Color.translate("&7/slpeef join &8- &7Join ongoing Spleef Event"));
        player.sendMessage(Color.translate("&7/slpeef leave &8- &7Leave ongoing Spleef Event"));
        player.sendMessage(Color.translate("&7/slpeef tp &8- &7Teleport to the Spleef Event Arena"));
        player.sendMessage(Color.translate("&7/slpeef setspawn  &8- &7Set the spawns for Spleef Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

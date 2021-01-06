package me.array.ArrayPractice.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "practice.sumohelp")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSUMO &7- &7Information on how to use sumo commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/sumo cancel &7- &7Cancel current sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo cooldown &7- &7Reset the sumo Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/sumo host &7- &7Host a sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo join &7- &7Join ongoing sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo leave &7- &7Leave ongoing sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo tp &7- &7Teleport to the sumo Event Arena"));
        player.sendMessage(Color.translate("&7» &b/sumo setspawn  &7- &7Set the spawns for sumo Event"));
        player.sendMessage(Color.translate("&7(One = first spawn, Two = second spawn, Spec = spec spawn"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

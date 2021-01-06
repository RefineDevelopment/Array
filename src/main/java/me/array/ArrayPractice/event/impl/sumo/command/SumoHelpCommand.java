package me.array.ArrayPractice.event.impl.sumo.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "practice.sumohelp")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&4&lSUMO &8- &7Information on how to use sumo commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7/sumo cancel &8- &7Cancel current sumo Event"));
        player.sendMessage(Color.translate("&7/sumo cooldown &8- &7Reset the sumo Event cooldown"));
        player.sendMessage(Color.translate("&7/sumo host &8- &7Host a sumo Event"));
        player.sendMessage(Color.translate("&7/sumo join &8- &7Join ongoing sumo Event"));
        player.sendMessage(Color.translate("&7/sumo leave &8- &7Leave ongoing sumo Event"));
        player.sendMessage(Color.translate("&7/sumo tp &8- &7Teleport to the sumo Event Arena"));
        player.sendMessage(Color.translate("&7/sumo setspawn  &8- &7Set the spawns for sumo Event"));
        player.sendMessage(Color.translate("&7(One = first spawn, Two = second spawn, Spec = spec spawn"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

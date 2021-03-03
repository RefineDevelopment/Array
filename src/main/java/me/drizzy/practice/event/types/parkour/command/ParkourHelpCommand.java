package me.drizzy.practice.event.types.parkour.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "practice.parkourhelp")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lPARKOUR &8- &8&o(&7&o&7Information on how to use parkour commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/parkour cancel &8- &8&o(&7&o&7Cancel current Parkour Event"));
        player.sendMessage(Color.translate("&b/parkour cooldown &8- &8&o(&7&o&7Reset the Parkour Event cooldown"));
        player.sendMessage(Color.translate("&b/parkour host &8- &8&o(&7&o&7Host a Parkour Event"));
        player.sendMessage(Color.translate("&b/parkour forcestart &8- &8&o(&7&o&7Force Start a Parkour Event"));
        player.sendMessage(Color.translate("&b/parkour join &8- &8&o(&7&o&7Join ongoing Parkour Event"));
        player.sendMessage(Color.translate("&b/parkour leave &8- &8&o(&7&o&7Leave ongoing Parkour Event"));
        player.sendMessage(Color.translate("&b/parkour tp &8- &8&o(&7&o&7Teleport to the Parkour Event Arena"));
        player.sendMessage(Color.translate("&b/parkour setspawn  &8- &8&o(&7&o&7Set the spawns for Parkour Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

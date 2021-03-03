package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.util.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "practice.sumo")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSumo &8- &8&o(&7&o&7Information on how to use sumo commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/sumo cancel &8- &8&o(&7&o&7Cancel current sumo Event"));
        player.sendMessage(Color.translate("&b/sumo cooldown &8- &8&o(&7&o&7Reset the sumo Event cooldown"));
        player.sendMessage(Color.translate("&b/sumo host &8- &8&o(&7&o&7Host a sumo Event"));
        player.sendMessage(Color.translate("&b/sumo forcestart &8- &8&o(&7&o&7Force start a sumo event"));
        player.sendMessage(Color.translate("&b/sumo join &8- &8&o(&7&o&7Join ongoing sumo Event"));
        player.sendMessage(Color.translate("&b/sumo leave &8- &8&o(&7&o&7Leave ongoing sumo Event"));
        player.sendMessage(Color.translate("&b/sumo tp &8- &8&o(&7&o&7Teleport to the sumo Event Arena"));
        player.sendMessage(Color.translate("&b/sumo setspawn  &8- &8&o(&7&o&7Set the spawns for sumo Event"));
        player.sendMessage(Color.translate("&7(one = First spawn, two = Second spawn, spec = Spectator spawn"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

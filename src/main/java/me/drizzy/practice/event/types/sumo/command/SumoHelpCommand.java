package me.drizzy.practice.event.types.sumo.command;

import me.drizzy.practice.util.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"sumo", "sumo help"}, permission = "practice.sumo")
public class SumoHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSumo &7- &7Information on how to use sumo commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/sumo cancel &7- &7Cancel current sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo cooldown &7- &7Reset the sumo Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/sumo host &7- &7Host a sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo forcestart &7- &7Force start a sumo event"));
        player.sendMessage(Color.translate("&7» &b/sumo join &7- &7Join ongoing sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo leave &7- &7Leave ongoing sumo Event"));
        player.sendMessage(Color.translate("&7» &b/sumo tp &7- &7Teleport to the sumo Event Arena"));
        player.sendMessage(Color.translate("&7» &b/sumo setspawn  &7- &7Set the spawns for sumo Event"));
        player.sendMessage(Color.translate("&7(one = First spawn, two = Second spawn, spec = Spectator spawn"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

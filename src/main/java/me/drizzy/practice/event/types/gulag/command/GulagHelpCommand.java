package me.drizzy.practice.event.types.gulag.command;

import me.drizzy.practice.util.Color;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"gulag", "gulag help"}, permission = "practice.gulaghelp")
public class GulagHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lGulag &8- &8&o(&7&o&7Information on how to use gulag commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/gulag cancel &8- &8&o(&7&o&7Cancel current Gulag Event"));
        player.sendMessage(Color.translate("&b/gulag cooldown &8- &8&o(&7&o&7Reset the Gulag Event cooldown"));
        player.sendMessage(Color.translate("&b/gulag host &8- &8&o(&7&o&7Host a Gulag Event"));
        player.sendMessage(Color.translate("&b/gulag forcestart &8- &8&o(&7&o&7Force start a Gulag Event"));
        player.sendMessage(Color.translate("&b/gulag join &8- &8&o(&7&o&7Join ongoing Gulag Event"));
        player.sendMessage(Color.translate("&b/gulag leave &8- &8&o(&7&o&7Leave ongoing Gulag Event"));
        player.sendMessage(Color.translate("&b/gulag tp &8- &8&o(&7&o&7Teleport to the Gulag Event Arena"));
        player.sendMessage(Color.translate("&b/gulag setspawn  &8- &8&o(&7&o&7Set the spawns for Gulag Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

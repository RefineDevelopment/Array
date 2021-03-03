package me.drizzy.practice.event.types.brackets.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"brackets", "brackets help"}, permission = "practice.bracketshelp")
public class BracketsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lBRACKETS &8- &8&o(&7&o&7Information on how to use bracket commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b/brackets cancel &8- &8&o(&7&o&7Cancel current Gulag Event"));
        player.sendMessage(Color.translate("&b/brackets cooldown &8- &8&o(&7&o&7Reset the Gulag Event cooldown"));
        player.sendMessage(Color.translate("&b/brackets host &8- &8&o(&7&o&7Host a Gulag Event"));
        player.sendMessage(Color.translate("&b/brackets forcestart &8- &8&o(&7&o&7Force start a Gulag Event"));
        player.sendMessage(Color.translate("&b/brackets join &8- &8&o(&7&o&7Join ongoing Gulag Event"));
        player.sendMessage(Color.translate("&b/brackets leave &8- &8&o(&7&o&7Leave ongoing Gulag Event"));
        player.sendMessage(Color.translate("&b/brackets tp &8- &8&o(&7&o&7Teleport to the Gulag Event Arena"));
        player.sendMessage(Color.translate("&b/brackets setspawn  &8- &8&o(&7&o&7Set the spawns for Gulag Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

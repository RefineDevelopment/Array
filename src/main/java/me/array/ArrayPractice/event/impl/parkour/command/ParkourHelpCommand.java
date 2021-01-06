package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "practice.parkourhelp")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&4&lPARKOUR &8- &7Information on how to use parkour commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7/parkour cancel &8- &7Cancel current Parkour Event"));
        player.sendMessage(Color.translate("&7/parkour cooldown &8- &7Reset the Parkour Event cooldown"));
        player.sendMessage(Color.translate("&7/parkour host &8- &7Host a Parkour Event"));
        player.sendMessage(Color.translate("&7/parkour join &8- &7Join ongoing Parkour Event"));
        player.sendMessage(Color.translate("&7/parkour leave &8- &7Leave ongoing Parkour Event"));
        player.sendMessage(Color.translate("&7/parkour tp &8- &7Teleport to the Parkour Event Arena"));
        player.sendMessage(Color.translate("&7/parkour setspawn  &8- &7Set the spawns for Parkour Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

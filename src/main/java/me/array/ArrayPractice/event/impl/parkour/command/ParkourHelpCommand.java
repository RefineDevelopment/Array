package me.array.ArrayPractice.event.impl.parkour.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"parkour", "parkour help"}, permission = "practice.parkourhelp")
public class ParkourHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lPARKOUR &7- &7Information on how to use parkour commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/parkour cancel &7- &7Cancel current Parkour Event"));
        player.sendMessage(Color.translate("&7» &b/parkour cooldown &7- &7Reset the Parkour Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/parkour host &7- &7Host a Parkour Event"));
        player.sendMessage(Color.translate("&7» &b/parkour join &7- &7Join ongoing Parkour Event"));
        player.sendMessage(Color.translate("&7» &b/parkour leave &7- &7Leave ongoing Parkour Event"));
        player.sendMessage(Color.translate("&7» &b/parkour tp &7- &7Teleport to the Parkour Event Arena"));
        player.sendMessage(Color.translate("&7» &b/parkour setspawn  &7- &7Set the spawns for Parkour Event"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

package me.array.ArrayPractice.event.impl.skywars.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars", "skywars help"}, permission = "practice.skywarshelp")
public class SkyWarsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&4&lSKYWARS &8- &7Information on how to use skywars commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7/skywars cancel &8- &7Cancel current Skywars Event"));
        player.sendMessage(Color.translate("&7/skywars cooldown &8- &7Reset the Skywars Event cooldown"));
        player.sendMessage(Color.translate("&7/skywars host &8- &7Host a Skywars Event"));
        player.sendMessage(Color.translate("&7/skywars join &8- &7Join ongoing Skywars Event"));
        player.sendMessage(Color.translate("&7/skywars leave &8- &7Leave ongoing Skywars Event"));
        player.sendMessage(Color.translate("&7/skywars tp &8- &7Teleport to the Skywars Event Arena"));
        player.sendMessage(Color.translate("&7/parkour setspawn  &8- &7Set the spawns for Skywars Event"));
        player.sendMessage(Color.translate("&7(You can setup too 12 setspawns"));
        player.sendMessage(Color.translate("&7/parkour setchest <chest>  &8- &7Set the chest for Skywars Event"));
        player.sendMessage(Color.translate("&7(Chests: ISLAND, NORMAL, MID)"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

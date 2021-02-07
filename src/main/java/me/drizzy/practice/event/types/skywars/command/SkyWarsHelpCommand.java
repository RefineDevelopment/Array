package me.drizzy.practice.event.types.skywars.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"skywars", "skywars help"}, permission = "practice.skywars")
public class SkyWarsHelpCommand {

    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&b&lSkywars Event &7- &7Information on how to use skywars commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        player.sendMessage(Color.translate("&7» &b/skywars cancel &7- &7Cancel current Skywars Event"));
        player.sendMessage(Color.translate("&7» &b/skywars cooldown &7- &7Reset the Skywars Event cooldown"));
        player.sendMessage(Color.translate("&7» &b/skywars host &7- &7Host a Skywars Event"));
        player.sendMessage(Color.translate("&7» &b/skywars join &7- &7Join ongoing Skywars Event"));
        player.sendMessage(Color.translate("&7» &b/skywars leave &7- &7Leave ongoing Skywars Event"));
        player.sendMessage(Color.translate("&7» &b/skywars tp &7- &7Teleport to the Skywars Event Arena"));
        player.sendMessage(Color.translate("&7» &b/parkour setspawn  &7- &7Set the spawns for Skywars Event"));
        player.sendMessage(Color.translate("&7(You can set upto 12 spawns"));
        player.sendMessage(Color.translate("&7» &b/skywars setchest <chest>  &7- &7Set the chest for Skywars Event"));
        player.sendMessage(Color.translate("&7(Chests: ISLAND, NORMAL, MID)"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
    }
}

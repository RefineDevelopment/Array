package me.drizzy.practice.event;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"event", "event help"}, permission = "array.dev")
public class EventHelpCommand {
    public void execute(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate( "&bArray &7» Event Commands"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate(" &8• &b/host &8- &8&o(&7&oOpen Events Menu&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/brackets &8- &8&o(&7&oView Bracket Commands&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/sumo &8- &8&o(&7&oView Sumo Commands&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/lms &8- &8&o(&7&oView LMS Commands&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/parkour&8- &8&o(&7&oView Parkour Commands&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/wizard &8- &8&o(&7&oView Wizard Commands&8&o)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }
}

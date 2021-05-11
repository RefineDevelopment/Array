package me.drizzy.practice.events;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"events", "events help"}, permission = "array.dev")
public class EventHelpCommand {
    public void execute(Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Event Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/host &8(&7&oOpen Events Menu&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets &8(&7&oView Bracket Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo &8(&7&oView Sumo Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms &8(&7&oView LMS Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour&8(&7&oView Parkour Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag &8(&7&oView Gulag Commands&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}

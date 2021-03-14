package me.drizzy.practice.tournament.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament", "tournament help"}, permission="array.dev")
public class TournamentCommand {
    public void execute(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate("&bArray &7» Tournament Commands"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate(" &8• &b/tournament list &8- &8&o(&7&oList Active Tournaments&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/tournament host (team-size(1/2)) &8- &8&o(&7&oHost a tournament8&o)"));
        player.sendMessage(CC.translate(" &8• &b/tournament cancel &8- &8&o(&7&oCancel a tournament8&o) &7- &c&l[ADMIN]"));
        player.sendMessage(CC.translate(" &8• &b/tournament join &8- &8&o(&7&oJoin an on-going tournament8&o)"));
        player.sendMessage(CC.translate(" &8• &b/tournament leave &8- &8&o(&7&oLeave an on-going tournament8&o)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }
}



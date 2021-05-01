package me.drizzy.practice.tournament.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament", "tournament help"}, permission="array.dev")
public class TournamentCommand {
    public void execute(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m--------&7&m" + StringUtils.repeat("-", 37) + "&c&m--------"));
        player.sendMessage(CC.translate("&cArray &7» Tournament Commands"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m--------&7&m" + StringUtils.repeat("-", 37) + "&c&m--------"));
        player.sendMessage(CC.translate(" &8• &c/tournament list &8(&7&oList Active Tournaments&8&o)"));
        player.sendMessage(CC.translate(" &8• &c/tournament host (team-size(1/2)) &8(&7&oHost a tournament8&o)"));
        player.sendMessage(CC.translate(" &8• &c/tournament cancel &8(&7&oCancel a tournament8&o) &8- &c&l[ADMIN]"));
        player.sendMessage(CC.translate(" &8• &c/tournament join &8(&7&oJoin an on-going tournament8&o)"));
        player.sendMessage(CC.translate(" &8• &c/tournament leave &8(&7&oLeave an on-going tournament8&o)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&m--------&7&m" + StringUtils.repeat("-", 37) + "&c&m--------"));
    }
}



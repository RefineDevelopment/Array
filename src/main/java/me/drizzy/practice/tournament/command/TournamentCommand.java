package me.drizzy.practice.tournament.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament", "tournament help"}, permission="practice.tournament")
public class TournamentCommand {

    public void execute(Player player) {
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            player.sendMessage(CC.translate( "&bArray &7» Tournament Commands"));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            player.sendMessage(CC.translate("&7» &b/tournament list &7- Create a Kit."));
            player.sendMessage(CC.translate("&7» &b/tournament host (team-size(1/2)) &7- Host a tournament."));
            player.sendMessage(CC.translate("&7» &b/tournament cancel &7- Cancels a tournament."));
            player.sendMessage(CC.translate("&7» &b/tournament join &7- Join an on-going tournament."));
            player.sendMessage(CC.translate("&7» &b/tournament leave &7- Leave an on-going tournament."));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        }
    }



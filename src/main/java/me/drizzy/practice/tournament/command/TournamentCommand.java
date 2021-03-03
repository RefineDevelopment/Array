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
            player.sendMessage(CC.translate("&b/tournament list &8- &8&o(&7&oCreate a Kit."));
            player.sendMessage(CC.translate("&b/tournament host (team-size(1/2)) &8- &8&o(&7&oHost a tournament."));
            player.sendMessage(CC.translate("&b/tournament cancel &8- &8&o(&7&oCancels a tournament."));
            player.sendMessage(CC.translate("&b/tournament join &8- &8&o(&7&oJoin an on-going tournament."));
            player.sendMessage(CC.translate("&b/tournament leave &8- &8&o(&7&oLeave an on-going tournament."));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        }
    }



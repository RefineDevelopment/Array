package me.array.ArrayPractice.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament", "tournament help", "tournament ?"} )

public class TournamentCommand {
    public void execute(Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» TournamentManager Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate("&7» &b/tournament list &7- Create a Kit."));
        player.sendMessage(CC.translate("&7» &b/tournament host (kit) (team-size) &7- Host a tournament."));
        player.sendMessage(CC.translate("&7» &b/tournament cancel &7- Cancels a tournament."));
        player.sendMessage(CC.translate("&7» &b/tournament join &7- Join an on-going tournament."));
        player.sendMessage(CC.translate("&7» &b/tournament leave &7- Leave an on-going tournament."));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}

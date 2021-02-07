package me.drizzy.practice.match.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"status", "match", "match status"})
public class MatchStatusCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player);
        final Match match = profile.getMatch();
        if (profile.isInSomeSortOfFight()) {
            if (match != null) {
                if (match.isSoloMatch() && match.isSumoMatch()) {
                    final TeamPlayer self=match.getTeamPlayer(player);
                    final TeamPlayer opponent=match.getOpponentTeamPlayer(player);
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
                    player.sendMessage(CC.translate("&b&l        Match Status "));
                    player.sendMessage("");
                    player.sendMessage(CC.translate("&fPing: &b " + PlayerUtil.getPing(self.getPlayer()) + "ms &7&l⎜ &c" + PlayerUtil.getPing(opponent.getPlayer()) + "ms"));
                    player.sendMessage(CC.translate("&fMap: &b " + match.getArena().getName()));
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
                }
                 else {
                     player.sendMessage(CC.RED + "This command is only for queue matches.");
                }
            }
        }
    }
}

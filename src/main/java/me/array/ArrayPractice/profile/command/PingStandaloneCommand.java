package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "ping", "checkping", "pings", "match", "matchstatus", "status" })
public class PingStandaloneCommand
{
    public void execute(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());;
        if (!profile.isInSomeSortOfFight()) {
            player.sendMessage(ChatColor.AQUA + player.getName() + "'s Ping: " + CC.YELLOW + PlayerUtil.getPing(player) + "ms");
            return;
        }
        final Match match = profile.getMatch();
        if (profile.isInSomeSortOfFight()) {
            if (match != null) {
                if (match.isSoloMatch()) {
                    final TeamPlayer self = match.getTeamPlayer(player);
                    final TeamPlayer opponent = match.getOpponentTeamPlayer(player);
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
                    player.sendMessage(CC.translate("&b&l        Match Status "));
                    player.sendMessage("");
                    player.sendMessage(CC.translate("&fPing: &b " + PlayerUtil.getPing(self.getPlayer()) + "ms &7&l⎜ &c" + PlayerUtil.getPing(opponent.getPlayer()) + "ms"));
                    player.sendMessage(CC.translate("&fMap: &b " + match.getArena().getName()));
                    player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
                }
            }
        }
    }
}

package me.drizzy.practice.match.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
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
                    player.sendMessage(CC.CHAT_BAR);
                    player.sendMessage(CC.translate("&b&l        Match Status "));
                    player.sendMessage("");
                    player.sendMessage(CC.translate("&fPing: &b " + PlayerUtil.getPing(self.getPlayer()) + "ms &7&l⎜ &c" + PlayerUtil.getPing(opponent.getPlayer()) + "ms"));
                    player.sendMessage(CC.translate("&fMap: &b " + match.getArena().getDisplayName()));
                    player.sendMessage(CC.CHAT_BAR);
                }
                 else {
                     player.sendMessage(CC.translate("&fYour Ping: &b" + PlayerUtil.getPing(player)));
                     player.sendMessage(CC.translate("&Current Map: &b" + match.getArena().getDisplayName()));
                }
            }
        }
    }
}

package me.drizzy.practice.tournament.command;

import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament spec"}, permission = "")
public class TournamentSpecCommand {
    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null) {
            player.sendMessage(CC.RED + "There is no current tournament available!");
            return;
        }
        Profile profile = Profile.getByUuid(Tournament.CURRENT_TOURNAMENT.getParticipants().get(1).getLeader().getPlayer());
        if (Tournament.getTournamentMatch() != null && profile.getMatch() !=null) {
            Tournament.getTournamentMatch().addSpectator(player, profile.getPlayer());
        }
    }
}

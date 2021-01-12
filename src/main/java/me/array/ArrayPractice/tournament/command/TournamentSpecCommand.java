package me.array.ArrayPractice.tournament.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.tournament.Tournament;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tournament spec", "spec"}, permission = "")
public class TournamentSpecCommand {
    public void execute(Player player) {
        if (Tournament.CURRENT_TOURNAMENT == null) {
            player.sendMessage(CC.RED + "There is no current tournament available!");
            return;
        }
        Profile profile = Profile.getByUuid(Tournament.CURRENT_TOURNAMENT.getParticipants().get(1).getLeader().getPlayer());
        if (profile.getMatch() != null) {
            profile.getMatch().addTournamentSpectator(player, profile.getPlayer());
        }
    }
}

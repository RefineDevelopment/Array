package xyz.refinedev.practice.cmds;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentType;
import xyz.refinedev.practice.tournament.impl.SoloTournament;
import xyz.refinedev.practice.tournament.impl.TeamTournament;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

public class TournamentCommands {

    @Command(name = "host", aliases = "start", desc = "Start a tournament")
    public void host(@Sender Player player, Kit kit, TournamentType type) {
        if (type.equals(TournamentType.SOLO)) {
            new SoloTournament(player.getName(), kit);
        } else {
            new TeamTournament(player.getName(), 2, kit);
        }
        this.join(player);

    }

    @Command(name = "join", desc = "Join the on-going tournament")
    public void join(@Sender Player player) {
        if (Tournament.getCurrentTournament() == null) {
            player.sendMessage(CC.translate("&7There is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (Tournament.getCurrentTournament().isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&7You are already participating in a tournament!"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        } else if (profile.getParty() != null && profile.getParty().isFighting()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
        
        if (Tournament.getCurrentTournament() instanceof TeamTournament) {
            player.chat("/party create");
            Tournament.getCurrentTournament().join(profile.getParty());
        } else {
            Tournament.getCurrentTournament().join(player);
        }
        
    }

    @Command(name = "leave", aliases = "quit", desc = "Leave the on-going tournament")
    public void leave(@Sender Player player) {
        if (Tournament.getCurrentTournament() == null) {
            player.sendMessage(CC.translate("&7There is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (!Tournament.getCurrentTournament().isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&7You are not participating in any tournament!"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);

        if (Tournament.getCurrentTournament() instanceof TeamTournament) {
            if (profile.getParty() == null) {
                player.sendMessage(Locale.ERROR_NOTABLE.toString());
            }
            Tournament.getCurrentTournament().leave(profile.getParty());
        } else {
            Tournament.getCurrentTournament().leave(player);
        }
    }
}

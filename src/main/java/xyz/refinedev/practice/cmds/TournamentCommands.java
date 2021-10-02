package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentType;
import xyz.refinedev.practice.tournament.impl.SoloTournament;
import xyz.refinedev.practice.tournament.impl.TeamTournament;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class TournamentCommands {

    private final Array plugin;

    @Command(name = "host", aliases = "start", desc = "Start a tournament")
    @Require("array.tournament.host")
    public void host(@Sender Player player, Kit kit, TournamentType type) {
        if (type.equals(TournamentType.SOLO)) {
            new SoloTournament(plugin, player.getName(), 100, kit);
        } else {
            new TeamTournament(plugin, player.getName(), EventTeamSize.DOUBLES.getMaxTeamPlayers(), kit);
        }
        this.join(player);

    }

    @Command(name = "join", desc = "Join the on-going tournament")
    public void join(@Sender Player player) {
        if (plugin.getTournamentManager().getCurrentTournament() == null) {
            player.sendMessage(CC.translate("&7There is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (plugin.getTournamentManager().getCurrentTournament().isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&7You are already participating in a tournament!"));
            return;
        }

        Profile profile = plugin.getProfileManager().getByPlayer(player);
        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        } else if (profile.getParty() != null && profile.getParty().isFighting()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
        
        if (plugin.getTournamentManager().getCurrentTournament() instanceof TeamTournament) {
            player.chat("/party create");
            plugin.getTournamentManager().getCurrentTournament().join(profile.getParty());
        } else {
            plugin.getTournamentManager().getCurrentTournament().join(player);
        }
        
    }

    @Command(name = "leave", aliases = "quit", desc = "Leave the on-going tournament")
    public void leave(@Sender Player player) {
        if (plugin.getTournamentManager().getCurrentTournament() == null) {
            player.sendMessage(CC.translate("&cThere is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (!plugin.getTournamentManager().getCurrentTournament().isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are not participating in any tournament!"));
            return;
        }

        Profile profile = plugin.getProfileManager().getByPlayer(player);

        if (plugin.getTournamentManager().getCurrentTournament() instanceof TeamTournament) {
            if (profile.getParty() == null) {
                player.sendMessage(Locale.ERROR_NOTABLE.toString());
            }
            plugin.getTournamentManager().getCurrentTournament().leave(profile.getParty());
        } else {
            plugin.getTournamentManager().getCurrentTournament().leave(player);
        }
    }
}

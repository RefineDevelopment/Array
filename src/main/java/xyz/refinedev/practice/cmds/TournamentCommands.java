package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.party.Party;
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
        switch (type) {
            case SOLO: {
                SoloTournament tournament = new SoloTournament(plugin, player.getName(), 100, kit);
                plugin.getTournamentManager().setCurrentTournament(tournament);
                break;
            }
            case TEAM: {
               TeamTournament tournament = new TeamTournament(plugin, player.getName(), EventTeamSize.DOUBLES.getMaxTeamPlayers(), kit);
                plugin.getTournamentManager().setCurrentTournament(tournament);
                break;
            }
        }
        this.join(player);

    }

    @Command(name = "join", desc = "Join the on-going tournament")
    public void join(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        Tournament<?> tournament = plugin.getTournamentManager().getCurrentTournament();

        if (tournament == null) {
            player.sendMessage(CC.translate("&cThere is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (tournament.isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are already participating in a tournament!"));
            return;
        }

        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        } else if (profile.hasParty() && profile.getParty().isFighting()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
        }
        
        if (tournament instanceof TeamTournament) {
            TeamTournament teamTournament = (TeamTournament) tournament;
            Party party = profile.getParty();

            if (!profile.hasParty()) {
                player.chat("/party create");
            }
            if (party.getPlayers().size() < teamTournament.getIndividualSize()) {
                player.sendMessage(CC.RED + "Your party does not have enough players to join this tournament!");
                return;
            }

            teamTournament.join(party);
        } else if (tournament instanceof SoloTournament) {
            SoloTournament soloTournament = (SoloTournament) tournament;
            soloTournament.join(player);
        }
    }

    @Command(name = "leave", aliases = "quit", desc = "Leave the on-going tournament")
    public void leave(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        Tournament<?> tournament = plugin.getTournamentManager().getCurrentTournament();

        if (tournament == null) {
            player.sendMessage(CC.translate("&cThere is no active tournament currently, please use /tournament host to start one!"));
            return;
        }
        if (tournament.isParticipating(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are already participating in a tournament!"));
            return;
        }

        //TODO: Check if the player leaves the party, then remove him from the tournament correctly
        if (tournament instanceof TeamTournament) {
            TeamTournament teamTournament = (TeamTournament) tournament;

            if (profile.isInMatch()) {
                profile.getMatch().handleDeath(player, null, true);
            }

            teamTournament.getTeamPlayers().remove(player.getUniqueId());
        } else if (tournament instanceof SoloTournament) {
            SoloTournament soloTournament = (SoloTournament) tournament;
            soloTournament.leave(player);
        }

        Bukkit.broadcastMessage(Locale.TOURNAMENT_LEAVE.toString()
                .replace("<left>", player.getName())
                .replace("<participants_size>", String.valueOf(tournament.getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(tournament.getMaxPlayers())));
    }
}

package xyz.refinedev.practice.tournament.impl;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentState;
import xyz.refinedev.practice.tournament.TournamentType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/6/2021
 * Project: Array
 */

public class TeamTournament extends Tournament<Party> {

    public TeamTournament(String host, int teamSize, Kit kit) {
        super(host, TournamentType.TEAM, teamSize, 4, 100, kit);
    }

    @Override
    public void join(Party type) {
        Preconditions.checkState(getParticipatingCount() < getMaxPlayers(), "Can not join because max limit has exceeded!");
        Preconditions.checkState(getRound() == 0, "Can not join after tournament has started!");
        this.getParties().add(type);

        type.setInTournament(true);
        type.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 20F, 15F));
        Bukkit.broadcastMessage(Locale.TOURNAMENT_JOIN.toString()
                .replace("<joined>", type.getLeader().getUsername() + "'s Party")
                .replace("<participants_size>", String.valueOf(this.getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(this.getMaxPlayers())));
    }

    @Override
    public void leave(Party type) {
        Preconditions.checkState(getRound() == 0, "Can not leave after tournament has started!");
        this.getParties().remove(type);
        type.setInTournament(false);

        Bukkit.broadcastMessage(Locale.TOURNAMENT_LEAVE.toString()
                .replace("<left>", type.getLeader().getUsername() + "'s Party")
                .replace("<participants_size>", String.valueOf(this.getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(this.getMaxPlayers())));
    }

    @Override
    public void start() {
        this.setStarted(true);
        if (!getParties().isEmpty()) {
            this.nextStage();
        } else {
            this.end(null);
        }
    }

    @Override
    public void nextStage() {
        this.setState(TournamentState.WAITING);
        this.setRound(this.getRound() + 1);

        LinkedList<Party> teamShuffle = new LinkedList<>(this.getParties());
        Collections.shuffle(teamShuffle);

        Bukkit.broadcastMessage(Locale.TOURNAMENT_ROUND.toString().replace("<round>", String.valueOf(getRound())));
        this.setState(TournamentState.FIGHTING);
        TaskUtil.runTimer(new BukkitRunnable() {
            @Override
            public void run() {
                if (teamShuffle.isEmpty()) {
                    cancel();
                    return;
                }
                Team teamA = teamShuffle.poll();
                if (teamShuffle.isEmpty()) {
                    teamA.broadcast(Locale.TOURNAMENT_NOT_PICKED.toString());
                    return;
                }
                Team teamB = teamShuffle.poll();

                Arena arena = Arena.getRandom(getKit());

                if (arena == null) {
                    teamB.broadcast(CC.translate("&cTried to start a match but there are no available arenas."));
                    teamA.broadcast(CC.translate("&cTried to start a match but there are no available arenas."));
                    return;
                }
                if (getKit().getGameRules().isBuild()) arena.setActive(true);
                Match match = new TeamMatch(teamA, teamB, getKit(), arena);
                match.start();

                getMatches().add(match);
            }
        }, 1L, 1L);
    }

    @Override
    public void eliminateParticipant(Party participant, Party killer) {
        participant.setInTournament(false);
        this.getParties().remove(participant);

        Bukkit.broadcastMessage(Locale.TOURNAMENT_ELIMINATED.toString()
                .replace("<eliminated>", participant.getLeader().getUsername() + "'s Party")
                .replace("<participants_count>", String.valueOf(getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(getMaxPlayers()))
                .replace("<killer>", killer.getLeader().getUsername() + "'s Party"));
    }

    @Override
    public void end(Party winner) {
        this.setState(TournamentState.ENDED);
        this.getParties().clear();

        if (winner != null) {
            Bukkit.broadcastMessage(CC.CHAT_BAR);
            Bukkit.broadcastMessage(Locale.TOURNAMENT_WON.toString().replace("<winner>", winner.getLeader().getUsername() + "'s Party"));
            Bukkit.broadcastMessage(CC.CHAT_BAR);
        } else {
            Bukkit.broadcastMessage(Locale.TOURNAMENT_CANCELLED.toString());
        }
        TaskUtil.runLater(() -> setCurrentTournament(null), 20 * 30L);
    }
}

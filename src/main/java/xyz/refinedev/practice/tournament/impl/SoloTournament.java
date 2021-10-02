package xyz.refinedev.practice.tournament.impl;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentState;
import xyz.refinedev.practice.tournament.TournamentType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

public class SoloTournament extends Tournament<Player> {

    private final Array plugin;

    public SoloTournament(Array plugin, String host, int maxPlayers, Kit kit) {
        super(host, TournamentType.SOLO, 1, 2, maxPlayers, kit);

        this.plugin = plugin;
    }

    /**
     * Leave the tournament either as a
     * {@link Player} or {@link Party}
     *
     * @param player The type of joining entity
     */
    @Override
    public void join(Player player) {
        Preconditions.checkState(getParticipatingCount() < getMaxPlayers(), "Can not join because max limit has exceeded!");
        Preconditions.checkState(getRound() == 0, "Can not join after tournament has started!");

        TeamPlayer teamPlayer = new TeamPlayer(player);
        this.getTeamPlayers().put(player.getUniqueId(), teamPlayer);

        Button.playSuccess(player);
        Bukkit.broadcastMessage(Locale.TOURNAMENT_JOIN.toString()
                .replace("<joined>", player.getName())
                .replace("<participants_size>", String.valueOf(this.getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(this.getMaxPlayers())));
    }

    /**
     * Leave the tournament either as a
     * {@link Player} or {@link Party}
     *
     * @param player The type of leaving entity
     */
    @Override
    public void leave(Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);

        if (profile.getMatch() != null) {
            profile.getMatch().handleDeath(player);
        }

        this.getTeamPlayers().remove(player.getUniqueId());

        if (!this.getState().equals(TournamentState.FIGHTING)) {
            Bukkit.broadcastMessage(Locale.TOURNAMENT_LEAVE.toString()
                    .replace("<left>", player.getName())
                    .replace("<participants_size>", String.valueOf(this.getParticipatingCount()))
                    .replace("<participants_max>", String.valueOf(this.getMaxPlayers())));
        } else {
            Bukkit.broadcastMessage(CC.RED  + player.getName() + " &7has been eliminated from the tournament because they left! (" + this.getParticipatingCount() + "/" + this.getMaxPlayers() + ")");
        }
    }

    /**
     * Start the tournament
     */
    @Override
    public void start() {
        this.setStarted(true);

        if (this.getTeamPlayers().isEmpty()) {
            this.end(null);
            return;
        }
        this.nextStage();
    }

    /**
     * Move onwards to the next stage of the tournament
     */
    @Override
    public void nextStage() {
        this.setState(TournamentState.WAITING);
        this.setRound(this.getRound() + 1);

        LinkedList<TeamPlayer> teamShuffle = new LinkedList<>(this.getTeamPlayers().values());
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

                TeamPlayer playerA = teamShuffle.poll();
                if (teamShuffle.isEmpty()) {
                    playerA.getPlayer().sendMessage(CC.translate("&cWe couldn't find an opponent for you, please wait for the next round!"));
                    return;
                }

                TeamPlayer playerB = teamShuffle.poll();

                Arena arena = Arena.getRandom(getKit());

                if (arena == null) {
                    playerA.getPlayer().sendMessage(CC.translate("&cTried to start a match but there are no available arenas."));
                    playerB.getPlayer().sendMessage(CC.translate("&cTried to start a match but there are no available arenas."));
                    return;
                }

                if (getKit().getGameRules().isBuild()) arena.setActive(true);
                Match match = new SoloMatch(null, playerA, playerB, getKit(), arena, QueueType.RANKED);
                match.start();
                getMatches().add(match);
            }
        }, 1L, 1L);
    }

    /**
     * Eliminate a participant from the tournament
     *
     * @param player The participant being removed
     * @param killer The killer of the participant or their opponent
     */
    @Override
    public void eliminateParticipant(Player player, Player killer) {
        this.getTeamPlayers().remove(player.getUniqueId());

        Bukkit.broadcastMessage(Locale.TOURNAMENT_ELIMINATED.toString()
                .replace("<eliminated>", player.getName())
                .replace("<participants_count>", String.valueOf(getParticipatingCount()))
                .replace("<participants_max>", String.valueOf(getMaxPlayers()))
                .replace("<killer>", killer.getName()));

    }

    /**
     * End or cancel the tournament
     *
     * @param winner The winner of the tourney if there is one
     */
    @Override
    public void end(Player winner) {
        this.setState(TournamentState.ENDED);

        this.getTeamPlayers().clear();

        if (winner != null){
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(Locale.TOURNAMENT_WON.toString().replace("<winner>", winner.getName()));
            Bukkit.broadcastMessage("");
        } else {
            Bukkit.broadcastMessage(Locale.TOURNAMENT_CANCELLED.toString());
        }
        plugin.getTournamentManager().setCurrentTournament(null);
    }
}

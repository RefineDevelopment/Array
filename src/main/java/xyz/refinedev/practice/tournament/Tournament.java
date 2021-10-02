package xyz.refinedev.practice.tournament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/5/2021
 * Project: Array
 */

@Getter @Setter
public abstract class Tournament<T> {

    //Used for Team Tournaments
    //Credits to Nick for Filtering
    private final List<Party> parties = new ArrayList<Party>(){
        @Override
        public @NotNull Iterator<Party> iterator() {
            this.filter();
            return super.iterator();
        }

        @Override
        public int size() {
            this.filter();
            return super.size();
        }

        private void filter() {
            List<Party> toRemove = new ArrayList<>();
            for (int i = 0; i < super.size(); ++i) {
                Party party = this.get(i);
                if (party.isDisbanded()) toRemove.add(party);
            }
            this.removeAll(toRemove);
        }
    };

    private final Map<UUID, TeamPlayer> teamPlayers = new ConcurrentHashMap<>();
    private final List<Match> matches = new ArrayList<>();

    private TournamentType type;
    private TournamentState state = TournamentState.WAITING;

    private boolean started;

    private int round = 0;
    private int individualSize;
    private int participantsToStart;
    private int maxPlayers;
    private Kit kit;
    private String host;

    /**
     * Original constructor for a tournament
     *
     * @param host                The host of the tournament
     * @param tournamentType      {@link TournamentType}
     * @param individualSize      players per team or 1 if its solo
     * @param participantsToStart the amount of participants needed to start
     * @param maxPlayers          maximum players allowed in a tournament
     * @param kit                 the kit of the tournament
     */
    public Tournament(String host, TournamentType tournamentType, int individualSize, int participantsToStart, int maxPlayers, Kit kit) {
        this.host = host;
        this.type = tournamentType;
        this.individualSize = individualSize;
        this.participantsToStart = participantsToStart;
        this.maxPlayers = maxPlayers;
        this.kit = kit;
    }

    /**
     * Returns true if the uuid is in the tournament
     *
     * @param uuid {@link UUID} of the participant
     * @return {@link Boolean}
     */
    public boolean isParticipating(UUID uuid) {
       return this.teamPlayers.containsKey(uuid);
    }

    /**
     * Returns true if the {@link TournamentState} is Fighting
     *
     * @return {@link Boolean}
     */
    public boolean isFighting() {
        return started && this.state == TournamentState.FIGHTING;
    }

    /**
     * Returns true if the {@link TournamentState} is Waiting
     *
     * @return {@link Boolean}
     */
    public boolean isWaiting() {
        return this.state == TournamentState.WAITING;
    }

    /**
     * Returns true if the {@link TournamentState} is Starting
     *
     * @return {@link Boolean}
     */
    public boolean isStarting() {
        return this.state == TournamentState.STARTING;
    }

    /**
     * Returns the amount of participants in the tournament
     *
     * @return {@link Boolean}
     */
    public int getParticipatingCount() {
        return this.teamPlayers.size();
    }

    /**
     * Leave the tournament either as a
     * {@link Player} or {@link Party}
     *
     * @param type The type of joining entity
     */
    public abstract void join(T type);

    /**
     * Leave the tournament either as a
     * {@link Player} or {@link Party}
     *
     * @param type The type of leaving entity
     */
    public abstract void leave(T type);

    /**
     * Start the tournament
     */
    public abstract void start();

    /**
     * Move onwards to the next stage of the tournament
     */
    public abstract void nextStage();

    /**
     * Eliminate a participant from the tournament
     *
     * @param participant The participant being removed
     * @param killer The killer of the participant or their opponent
     */
    public abstract void eliminateParticipant(T participant, T killer);

    /**
     * End or cancel the tournament
     *
     * @param winner The winner of the tourney if there is one
     */
    public abstract void end(T winner);
}

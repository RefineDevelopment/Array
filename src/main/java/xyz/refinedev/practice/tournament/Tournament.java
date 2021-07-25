package xyz.refinedev.practice.tournament;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter @Setter
public class Tournament {

    @Getter public static BukkitRunnable RUNNABLE = null;
    @Getter public static Tournament CURRENT_TOURNAMENT = null;
    @Getter public static TournamentMatch tournamentMatch;

    private final List<Party> participants = new ArrayList<Party>() {
        @Override
        public @NotNull Iterator<Party> iterator() {
            filter();
            return super.iterator();
        }

        @Override
        public int size() {
            filter();
            return super.size();
        }

        private void filter() {

            List<Party> toRemove = new ArrayList<>();
            for ( int i=0; i < super.size(); i++ ) {
                Party party = get(i);
                if (party.isDisbanded()) {
                    toRemove.add(party);
                }
            }
            removeAll(toRemove);
        }
    };
    
    private final List<TournamentMatch> tournamentMatches = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();

    private Kit ladder;
    private String hostType;
    
    private int participatingCount;
    private int round;
    private int teamCount;
    
    private boolean canceled = false;


    /**
     * Returns a boolean for if the player is participating
     * in the on going tournament
     *
     * @param player The player to check if participating
     * @return {@link Boolean}
     */
    public boolean isParticipating(Player player) {
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            return false;
        }
        return participants.contains(party);
    }

    /**
     * Leave method for a party, this executes
     * all the leave tasks for the party
     *
     * @param party The party leaving the tournament
     */
    public void leave(Party party){
        Preconditions.checkState(round == 0  , "Can not join after start.");
        if(participants.contains(party)) {
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(Locale.TOURNAMENT_LEAVE.toString()
                            .replace("<left_party>", party.getLeader().getPlayer().getName())
                            .replace("<participants_size>", String.valueOf(this.getParticipatingCount())));
                }
            }
            participants.remove(party);
        }
    }

    /**
     * Join method for a party, this executes
     * all the join tasks for the party
     *
     * @param party The party joining the tournament
     */
    public void participate(Party party) {
        Preconditions.checkState(round == 0  , "Can not join after start.");
        if(!participants.contains(party)) {
            participants.add(party);
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(CC.translate(Locale.TOURNAMENT_JOIN.toString())
                            .replace("<joined_party>", party.getLeader().getPlayer().getName())
                            .replace("<participants_size>", String.valueOf(this.getParticipatingCount())));
                }
            }
        }
    }


    public boolean hasStarted() {
        return round != 0;
    }

    public int getParticipatingCount() {
        return participants.size();
    }

    public void cancel() {
        canceled = true;
        CURRENT_TOURNAMENT = null;
        participants.clear();
        tournamentMatches.clear();
    }

    public void tournamentstart() {
        round++;
        Collections.shuffle(participants);
        if(participatingCount == 0){
            participatingCount = participants.size();
        }
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if (Profile.getByPlayer(player).getSettings().isTmessagesEnabled()) {
                player.sendMessage(Locale.TOURNAMENT_ROUND.toString().replace("<round>", String.valueOf(round)));
            }
        }
        Iterator<Party> iterator = participants.iterator();
        while (iterator.hasNext()) {
            Party party = iterator.next();
            if (!iterator.hasNext()) {
                party.broadcast(Locale.TOURNAMENT_NOT_PICKED.toString());
                break;
            }
            
            Party other = iterator.next();
            Arena arena = Arena.getRandom(ladder);

            Team teamA = new Team(new TeamPlayer(party.getLeader().getPlayer()));
            Team teamB = new Team(new TeamPlayer(other.getLeader().getPlayer()));
            tournamentMatch = new TournamentMatch(teamA, teamB, getLadder(), arena);

            for ( Player player : party.getPlayers() ) {
                final Profile otherProfile = Profile.getByUuid(player.getUniqueId());

                otherProfile.setState(ProfileState.IN_FIGHT);
                otherProfile.setMatch(tournamentMatch);

                if (!party.isLeader(player.getUniqueId())) {
                    teamA.getTeamPlayers().add(new TeamPlayer(player));
                }
            }
            for ( Player player : other.getPlayers() ) {
                final Profile otherProfile = Profile.getByUuid(player.getUniqueId());

                otherProfile.setState(ProfileState.IN_FIGHT);
                otherProfile.setMatch(tournamentMatch);

                if (!other.isLeader(player.getUniqueId())) {
                    teamB.getTeamPlayers().add(new TeamPlayer(player));
                }
            }
            tournamentMatch.start();
            tournamentMatches.add(tournamentMatch);
        }
    }

    public class TournamentMatch extends TeamMatch {

        public TournamentMatch(Team teamA, Team teamB, Kit ladder, Arena arena) {
            super(teamA, teamB, ladder, arena);
        }

        @Override
        public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
            super.handleDeath(deadPlayer, killerPlayer, disconnected);
        }

        @Override
        public boolean onEnd() {
            if (!canceled) {
                final Team winningTeam = this.getWinningTeam();
                final Team losingTeam = this.getOpponentTeam(winningTeam);
                for (TeamPlayer losingPlayer : losingTeam.getTeamPlayers()) {
                    participants.remove(Profile.getByUuid(losingPlayer.getUuid()).getParty());
                }
                tournamentMatches.remove(TournamentMatch.this);
                StringBuilder builder = new StringBuilder();

                for (TeamPlayer matchPlayer : winningTeam.getTeamPlayers()) {
                    builder.append(Rank.getRankType().getFullName(matchPlayer.getPlayer()));
                    builder.append("&7, ");
                }
                StringBuilder builders = new StringBuilder();
                for (TeamPlayer matchPlayer : losingTeam.getTeamPlayers()) {
                    builders.append(Rank.getRankType().getFullName(matchPlayer.getPlayer()));
                    builders.append("&7, ");
                }
                if (builders.length() > 0) {
                    builders.setLength(builders.length() - 2);
                }

                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    Profile profile = Profile.getByPlayer(player);
                    if (profile.getSettings().isTmessagesEnabled()) {
                        player.sendMessage(CC.translate(Locale.TOURNAMENT_ELIMINATED.toString())
                                .replace("<eliminated>", CC.translate(builders.toString()))
                                .replace("<participants_size>", String.valueOf(participants.size()))
                                .replace("<participants_count>", String.valueOf(participatingCount)));
                    }
                }

                if (tournamentMatches.isEmpty()) {
                    if (participants.size() <= 1) {
                        Bukkit.broadcastMessage(Locale.TOURNAMENT_WON.toString().replace("<won>", CC.translate(builder.toString())));
                        if (participants.get(0) != null) {
                            if (getTeamCount() == 1) {
                                Party winner = participants.get(0);
                                Profile winnerProfile = Profile.getByUuid(winner.getLeader().getUuid());
                                winnerProfile.getStatisticsData().get(getKit()).setElo(winnerProfile.getStatisticsData().get(getKit()).getElo() + 10);
                                winnerProfile.calculateGlobalElo();
                                winnerProfile.save();
                            }
                        }
                        CURRENT_TOURNAMENT = null;
                    } else {
                        TaskUtil.runLater(Tournament.this::tournamentstart, 100L);
                    }
                }
            }
            super.onEnd();
            return true;
        }
    }
}

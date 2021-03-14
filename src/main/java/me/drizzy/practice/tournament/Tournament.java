package me.drizzy.practice.tournament;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.types.TeamMatch;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;

import java.util.*;

public class Tournament {
    public static BukkitRunnable RUNNABLE = null;
    public static Tournament CURRENT_TOURNAMENT = null;
    @Getter
    public static TournamentMatch tournamentMatch;


    @Getter
    private final List<Party> participants = new ArrayList<Party>() {
        @Override
        public Iterator<Party> iterator() {
            filter();
            return super.iterator();
        }

        @Override
        public int size() {
            filter();
            return super.size();
        }

        private void filter() {

            List<Party> toRemove = Lists.newArrayList();
            for (int i = 0; i < super.size(); i++) {
                Party party = get(i);
                if (party.isDisbanded()) {
                    toRemove.add(party);
                }
            }
            removeAll(toRemove);
        }
    };
    private final List<TournamentMatch> tournamentMatches = Lists.newArrayList();
    public final List<UUID> spectators = new ArrayList<>();

    @Getter
    @Setter
    private Kit ladder;
    @Getter
    @Setter
    private String hostType;
    private int participatingCount;
    private int round;
    @Getter
    @Setter
    private int teamCount;
    private boolean canceled = false;


    public boolean isParticipating(Player player) {
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if (party == null) {
            return false;
        }
        return participants.contains(Profile.getByUuid(player.getUniqueId()).getParty());
    }

    public boolean hasStarted() {
        return round != 0;
    }

    public int getParticipatingCount() {
        return participants.size();
    }

    public List<TournamentMatch> getTournamentMatches() {
        return tournamentMatches;
    }

    public int getRound() {
        return round;
    }

    public void leave(Party party){
        Preconditions.checkState(round == 0  , "Can not join after start.");
        if(participants.contains(party)) {
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(CC.translate("&8[&b&lTournament&8] &c" + party.getLeader().getPlayer().getDisplayName()) + CC.GRAY + " has left Tournament!" + CC.GRAY + "(&b" + participants.size() + "/" + "50" + "&8)");
                }
            }
            participants.remove(party);
        }
    }

    public void participate(Party party){
        Preconditions.checkState(round == 0  , "Can not join after start.");
        if(!participants.contains(party)) {
            participants.add(party);
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(CC.translate("&8[&b&lTournament&8] &b" + party.getLeader().getPlayer().getDisplayName() + CC.GRAY + " has joined the Tournament! " + "&8(&b" + participants.size() + "/" + "50" + "&8)"));
                }
            }
        }
    }

    public void cancel() {
        canceled = true;
        participants.clear();
        tournamentMatches.clear();
    }

    public void tournamentstart(){
        Collections.shuffle(participants);
        if(participatingCount == 0){
            participatingCount = participants.size();
        }
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            if (Profile.getByUuid(player).getSettings().isAllowTournamentMessages()) {
                player.sendMessage(CC.translate("&8[&9Round&8] &b" + (round) + ChatColor.GRAY + " has started!."));
            }
        }
        Iterator<Party> iterator = participants.iterator();
        while (iterator.hasNext()){
            Party player = iterator.next();
            if(!iterator.hasNext()){
                player.broadcast(CC.translate("&8[&b&lTournament&8] &7" + "You weren't picked this round, please wait for your turn!"));
                break;
            }
            Party other = iterator.next();
            Arena arena = Arena.getRandom(ladder);
            {
                Team teamA = new Team(new TeamPlayer(player.getLeader().getPlayer()));
                Team teamB = new Team(new TeamPlayer(other.getLeader().getPlayer()));
                tournamentMatch = new TournamentMatch(teamA, teamB, getLadder(), arena);

                for (Player player1 : player.getPlayers()) {
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                        otherData.setMatch(tournamentMatch);

                    if (!player.isLeader(player1.getUniqueId())) {
                        teamA.getTeamPlayers().add(new TeamPlayer(player1));
                    }
                }
                for (Player player1 : other.getPlayers()) {
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                    otherData.setMatch(tournamentMatch);

                    if (!other.isLeader(player1.getUniqueId())) {
                        teamB.getTeamPlayers().add(new TeamPlayer(player1));
                    }
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
                    builder.append(matchPlayer.getPlayer().getDisplayName());
                    builder.append("&7, ");
                }
                StringBuilder builders = new StringBuilder();
                for (TeamPlayer matchPlayer : losingTeam.getTeamPlayers()) {
                    builders.append(matchPlayer.getPlayer().getDisplayName());
                    builders.append("&7, ");
                }
                if (builders.length() > 0) {
                    builders.setLength(builders.length() - 2);
                }
                if (builder.length() > 0) {
                    builder.setLength(builder.length() - 2);
                }

                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    if (Profile.getByUuid(player).getSettings().isAllowTournamentMessages()) {
                        player.sendMessage(CC.translate("&8[&b&lTournament&8] &b" +  CC.RED + CC.translate(builders.toString()) + ChatColor.GRAY + " has been eliminated. " + "&8(&b" + participants.size() + "/" + participatingCount + "&8)"));
                    }
                }

                if (tournamentMatches.isEmpty()) {
                    if (participants.size() <= 1) {
                        Bukkit.broadcastMessage(CC.BLUE + CC.BOLD + "");
                        Bukkit.broadcastMessage(CC.translate("&8[&b&lTournament&8] &b" + CC.AQUA + CC.translate(builder.toString()) + ChatColor.GRAY + " won the" + CC.AQUA + " tournament" + CC.GRAY + "!"));
                        Bukkit.broadcastMessage(CC.BLUE + CC.BOLD + "");
                        if (participants.get(0) != null) {
                            if (getTeamCount() == 1) {
                                    Party winner = participants.get(0);
                                    Profile winnerProfile = Profile.getByUuid(winner.getLeader().getUuid());
                                    winnerProfile.getStatisticsData().get(getKit()).setElo(winnerProfile.getStatisticsData().get(getKit()).getElo() + 10);
                                    winnerProfile.calculateGlobalElo();
                            }
                        }
                        CURRENT_TOURNAMENT = null;
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                tournamentstart();
                            }
                        }.runTaskLater(Array.getInstance(), 100L);
                    }
                }
            }
            super.onEnd();
            return true;
        }
    }
}

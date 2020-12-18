package me.array.ArrayPractice.tournament;

import com.google.common.base.Preconditions;

import com.google.common.collect.Lists;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.impl.TeamMatch;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.external.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tournament {
    public static BukkitRunnable RUNNABLE = null;
    public static Tournament CURRENT_TOURNAMENT = null;

    @Getter private final List<Party> participants = new ArrayList<Party>() {
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

        private void filter(){

            List<Party> toRemove = Lists.newArrayList();
            for(int i = 0 ; i < super.size() ; i++){
                Party party = get(i);
                if(party.isDisbanded()){
                    toRemove.add(party);
                }
            }
            removeAll(toRemove);
        }
    };
    private final List<TournamentMatch> tournamentMatches = Lists.newArrayList();

    private Kit ladder;
    private int participatingCount;
    private int round;
    @Getter
    @Setter
    private int teamCount;
    private boolean canceled = false;



    public boolean isParticipating(Player player){
        Party party = Profile.getByUuid(player.getUniqueId()).getParty();
        if(party == null){
            return false;
        }
        return participants.contains(Profile.getByUuid(player.getUniqueId()).getParty());
    }
    public boolean hasStarted(){
        return round != 0;
    }


    public Kit getLadder(){
        return ladder;
    }

    public void setLadder(Kit ladder){
        this.ladder = ladder;
    }

    public int getStartingParticipatingCount(){
        if(participatingCount == 0){
            return getParticipatingCount();
        }
        return participatingCount;
    }
    public int getParticipatingCount(){
        return participants.size();
    }

    public List<TournamentMatch> getTournamentMatches() {
        return tournamentMatches;
    }

    public int getRound(){
        return round;
    }

    public void leave(Party party){
        Preconditions.checkState(round == 0  , "Can not join after start.");
        if(participants.contains(party)) {
            for (Party partyparticipants : participants) {
                for (Player player : partyparticipants.getPlayers()) {
                    player.sendMessage(CC.AQUA + CC.BOLD + "[Tournament] " + CC.RED + CC.translate(Array.get().getChat().getPlayerPrefix(party.getLeader().getPlayer()) + party.getLeader().getPlayer().getName()) + CC.WHITE + " has left " + CC.GRAY + "(" + participants.size() + "/" + "50" + ")");
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
                    player.sendMessage(CC.AQUA + CC.BOLD + "[Tournament] " + CC.GREEN + CC.translate(Array.get().getChat().getPlayerPrefix(party.getLeader().getPlayer()) + party.getLeader().getPlayer().getName()) + CC.WHITE + " has joined " + CC.GRAY + "(" + participants.size() + "/" + "50" + ")");
                }
            }
        }
    }

    public void cancel(){
        canceled = true;
        participants.clear();
        tournamentMatches.clear();
    }

    public void tournamentstart(){
        Collections.shuffle(participants);
        if(participatingCount == 0){
            participatingCount = participants.size();
        }
        Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "[Tournament] " + ChatColor.GOLD + "Round " + (round++ + 1) + ChatColor.WHITE + " has started!.");

        Iterator<Party> iterator = participants.iterator();
        while (iterator.hasNext()){
            Party player = iterator.next();
            if(!iterator.hasNext()){
                player.broadcast(CC.AQUA + CC.BOLD + "[Tournament] " + ChatColor.RED + "You do not have any player to fight! Please wait for the next round");
                break;
            }
            Party other = iterator.next();
            Arena arena = Arena.getRandom(ladder);
            TournamentMatch tournamentMatch;
            {
                Team teamA = new Team(new TeamPlayer(player.getLeader().getPlayer()));
                Team teamB = new Team(new TeamPlayer(other.getLeader().getPlayer()));
                tournamentMatch = new TournamentMatch(teamA, teamB, getLadder(), arena);

                for(Player player1 : player.getPlayers()){
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                    otherData.setMatch(tournamentMatch);

                    if(!player.isLeader(player1.getUniqueId())){
                        teamA.getTeamPlayers().add(new TeamPlayer(player1));
                    }
                }
                for(Player player1 : other.getPlayers()){
                    final Profile otherData = Profile.getByUuid(player1.getUniqueId());

                    otherData.setState(ProfileState.IN_FIGHT);
                    otherData.setMatch(tournamentMatch);

                    if(!other.isLeader(player1.getUniqueId())){
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
        public void onEnd() {
            if(!canceled) {
                final Team winningTeam = this.getWinningTeam();
                final Team losingTeam = this.getOpponentTeam(winningTeam);
                for(TeamPlayer losingPlayer : losingTeam.getTeamPlayers()){
                    participants.remove(Profile.getByUuid(losingPlayer.getUuid()).getParty());
                }
                tournamentMatches.remove(TournamentMatch.this);
                StringBuilder builder = new StringBuilder();
                for(TeamPlayer matchPlayer : winningTeam.getTeamPlayers()){
                    builder.append(CC.translate(Array.get().getChat().getPlayerPrefix(matchPlayer.getPlayer()) + matchPlayer.getPlayer().getName()));
                    builder.append(" ");
                }
                StringBuilder builders = new StringBuilder();
                for(TeamPlayer matchPlayer : losingTeam.getTeamPlayers()){
        			builders.append(CC.translate(Array.get().getChat().getPlayerPrefix(matchPlayer.getPlayer()) + matchPlayer.getPlayer().getName()));
        			builders.append(" ");
                }
                Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "[Tournament] " +  CC.RESET + CC.translate(builders.toString()) + ChatColor.WHITE + "has been eliminated. " + ChatColor.GRAY + "(" + participants.size() + "/" + participatingCount + ")");
                if (tournamentMatches.isEmpty()) {
                    if (participants.size() <= 1) {
                        Bukkit.broadcastMessage(CC.BLUE + CC.BOLD + "");
                        Bukkit.broadcastMessage(CC.AQUA + CC.BOLD + "[Tournament] " + CC.RESET + CC.translate(builder.toString()) + ChatColor.GOLD + "won the tournament");
                        Bukkit.broadcastMessage(CC.BLUE + CC.BOLD + "");
                        CURRENT_TOURNAMENT = null;
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                tournamentstart();
                            }
                        }.runTaskLater(Array.get(), 100L);
                    }
                }
            }
            super.onEnd();

        }
    }
}

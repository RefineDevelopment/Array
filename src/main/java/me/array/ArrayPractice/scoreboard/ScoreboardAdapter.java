

package me.array.ArrayPractice.scoreboard;

import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.event.impl.infected.Infected;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.ffa.FFA;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.match.kits.utils.ArmorClass;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.kits.Bard;
import me.array.ArrayPractice.event.impl.tournament.Tournament;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import me.array.ArrayPractice.util.external.CC;
import java.util.ArrayList;
import me.array.ArrayPractice.profile.Profile;
import java.util.List;
import org.bukkit.entity.Player;
import me.joeleoli.frame.FrameAdapter;

public class ScoreboardAdapter implements FrameAdapter
{
    private Array Array;
    
    public ScoreboardAdapter(final Array Array) {
        this.Array=Array;
    }
    
    @Override
    public String getTitle(final Player player) {
        return "&b&lMoonNight &7(Practice)";
    }
    
    @Override
    public List<String> getLines(final Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!profile.getOptions().isShowScoreboard()) {
            return null;
        }
        final List<String> toReturn = new ArrayList<String>();
        toReturn.add(CC.SB_BAR);
        if (profile.isInLobby() || profile.isInQueue()) {
            toReturn.add(CC.translate("&fOnline: &b" + Bukkit.getServer().getOnlinePlayers().size()));
            toReturn.add(CC.translate("&fIn Fights: &b" + this.getInFights()));
            toReturn.add(CC.translate("&fIn Queue: &b" + this.getInQueues()));
            if (!this.Array.getSumoManager().getCooldown().hasExpired()) {
                toReturn.add("&fSumo: &b" + TimeUtil.millisToTimer(this.Array.getSumoManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getBracketsManager().getCooldown().hasExpired()) {
                toReturn.add("&fBrackets: &b" + TimeUtil.millisToTimer(this.Array.getBracketsManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getFfaManager().getCooldown().hasExpired()) {
                toReturn.add("&fFFA: &b" + TimeUtil.millisToTimer(this.Array.getFfaManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getParkourManager().getCooldown().hasExpired()) {
                toReturn.add("&fParkour: &b" + TimeUtil.millisToTimer(this.Array.getParkourManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getWipeoutManager().getCooldown().hasExpired()) {
                toReturn.add("&fWipeout: &b" + TimeUtil.millisToTimer(this.Array.getWipeoutManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getSkyWarsManager().getCooldown().hasExpired()) {
                toReturn.add("&fSkyWars: &b" + TimeUtil.millisToTimer(this.Array.getSkyWarsManager().getCooldown().getRemaining()));
            }
            if (!this.Array.getSpleefManager().getCooldown().hasExpired()) {
                toReturn.add("&fSpleef: &b" + TimeUtil.millisToTimer(this.Array.getSpleefManager().getCooldown().getRemaining()));
            }
            if (profile.getParty() != null) {
                final Party party = profile.getParty();
                toReturn.add(CC.SB_BAR);
                toReturn.add("&bParty:");
                int added = 0;
                for (final TeamPlayer teamPlayer : party.getTeamPlayers()) {
                    ++added;
                    toReturn.add(" &7" + (party.isLeader(teamPlayer.getUuid()) ? "*" : "-") + " &r" + teamPlayer.getUsername());
                    if (added >= 4) {
                        break;
                    }
                }
            }
            if (profile.isInQueue()) {
                final Queue queue = profile.getQueue();
                toReturn.add(CC.SB_BAR);
                toReturn.add("&bQueued For:");
                toReturn.add("&7" + queue.getQueueName());
                if (queue.getQueueType().equals(QueueType.RANKED)) {
                    toReturn.add("&fRange: &b" + profile.getQueueProfile().getMinRange() + " -> " + profile.getQueueProfile().getMaxRange());
                }
            }
            else if (Tournament.CURRENT_TOURNAMENT != null) {
                final Tournament tournament = Tournament.CURRENT_TOURNAMENT;
                final String round = (tournament.getRound() > 0) ? Integer.toString(tournament.getRound()) : "&fStarting";
                toReturn.add("");
                toReturn.add("&b&lTournament: &r");
                toReturn.add("&fKit: &b" + tournament.getLadder().getName() + " &7(" + tournament.getTeamCount() + "v" + tournament.getTeamCount() + ")");
                toReturn.add("&fRound: &b" + round);
                toReturn.add(((tournament.getTeamCount() > 1) ? "&fParties: &b" : "&fPlayers: &b") + tournament.getParticipatingCount() + "/" + tournament.getStartingParticipatingCount());
            }
        }
        else if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (match != null) {
                if (match.isSoloMatch()) {
                    final TeamPlayer self = match.getTeamPlayer(player);
                    final TeamPlayer opponent = match.getOpponentTeamPlayer(player);
                    toReturn.add("&fOpponent: &c" + opponent.getUsername());
                    toReturn.add("&fDuration: &b" + match.getDuration());
                    toReturn.add("");
                    toReturn.add("&fYour Ping: &a" + self.getPing() + "ms");
                    toReturn.add("&fEnemy Ping: &c" + opponent.getPing() + "ms");
                }
                else if (match.isTeamMatch() || match.isHCFMatch()) {
                    final Team team = match.getTeam(player);
                    final Team opponentTeam = match.getOpponentTeam(player);
                    toReturn.add("&fDuration: &b" + match.getDuration());
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 6) {
                        toReturn.add("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")");
                        toReturn.add("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")");
                    }
                    else {
                        toReturn.add("&fTeam: &a" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        toReturn.add("&fOpponents: &c" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                    }
                    toReturn.add("");
                    if (match.isHCFMatch()) {
                        final ArmorClass pvpClass = this.Array.getArmorClassManager().getEquippedClass(player);
                        if (pvpClass != null && pvpClass instanceof Bard) {
                            final Bard bardClass = (Bard)pvpClass;
                            toReturn.add("&fBard Energy: &b" + bardClass.getEnergy(player));
                        }
                    }
                }
                else if (match.isKoTHMatch()) {
                    final Team team = match.getTeam(player);
                    final Team opponentTeam = match.getOpponentTeam(player);
                    toReturn.add("&fYour Points: &a" + team.getKothPoints() + "/5");
                    toReturn.add("&fEnemy Points: &c" + opponentTeam.getKothPoints() + "/5");
                    toReturn.add("");
                    toReturn.add("&fCapTime: &d" + match.getTimer() + "s");
                    toReturn.add("&fCapper: &r" + ((match.getCapper() != null) ? match.getCapper().getName() : "No-one"));
                    toReturn.add("");
                    toReturn.add("&fDuration: &b" + match.getDuration());
                }
                else if (match.isFreeForAllMatch()) {
                    final Team team = match.getTeam(player);
                    toReturn.add("&fOpponents: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    toReturn.add("&fDuration: &b" + match.getDuration());
                }
            }
        }
        else if (profile.isSpectating()) {
            final Match match = profile.getMatch();
            final Sumo sumo = profile.getSumo();
            final FFA ffa = profile.getFfa();
            final Juggernaut juggernaut = profile.getJuggernaut();
            final Brackets brackets = profile.getBrackets();
            final Parkour parkour = profile.getParkour();
            final Wipeout wipeout = profile.getWipeout();
            final SkyWars skyWars = profile.getSkyWars();
            final Spleef spleef = profile.getSpleef();
            final Infected infected = profile.getInfected();
            if (match != null) {
                if (!match.isHCFMatch() && !match.isKoTHMatch()) {
                    toReturn.add("&fKit: &b" + match.getKit().getName());
                }
                toReturn.add("&fDuration: &b" + match.getDuration());
                toReturn.add("");
                if (match.isSoloMatch()) {
                    toReturn.add(match.getTeamPlayerA().getUsername());
                    toReturn.add(match.getTeamPlayerB().getUsername());
                }
                else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch()) {
                    toReturn.add("&b" + match.getTeamA().getLeader().getUsername() + "'s Team");
                    toReturn.add("&7vs");
                    toReturn.add("&b" + match.getTeamB().getLeader().getUsername() + "'s Team");
                }
                else {
                    final Team team2 = match.getTeam(player);
                    toReturn.add("&fAlive: &b" + team2.getAliveCount() + "/" + team2.getTeamPlayers().size());
                }
            }
            else if (sumo != null) {
                toReturn.add(CC.translate("&fHost: &b" + sumo.getName()));
                if (sumo.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + sumo.getEventPlayers().size() + "/" + sumo.getMaxPlayers());
                    toReturn.add("");
                    if (sumo.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + sumo.getRoundDuration());
                    toReturn.add("");
                    toReturn.add("&b" + sumo.getRoundPlayerA().getUsername());
                    toReturn.add("&7vs");
                    toReturn.add("&b" + sumo.getRoundPlayerB().getUsername());
                }
            }
            else if (ffa != null) {
                toReturn.add(CC.translate("&fHost: &b" + ffa.getName()));
                if (ffa.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + ffa.getEventPlayers().size() + "/" + ffa.getMaxPlayers());
                    toReturn.add("");
                    if (ffa.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(ffa.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + ffa.getRemainingPlayers().size() + "/" + ffa.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + ffa.getRoundDuration());
                }
            }
            else if (juggernaut != null) {
                toReturn.add(CC.translate("&fHost: &b" + juggernaut.getName()));
                toReturn.add(CC.translate("&fJuggernaut: &b" + juggernaut.getJuggernaut().getPlayer().getName()));
                if (juggernaut.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + juggernaut.getEventPlayers().size() + "/" + juggernaut.getMaxPlayers());
                    toReturn.add("");
                    if (juggernaut.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(juggernaut.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + juggernaut.getRemainingPlayers().size() + "/" + juggernaut.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + juggernaut.getRoundDuration());
                }
            }
            else if (infected != null) {
                toReturn.add(CC.translate("&fHost: &b" + infected.getName()));
                if (infected.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + infected.getEventPlayers().size() + "/" + infected.getMaxPlayers());
                    toReturn.add("");
                    if (infected.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(infected.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fSurvivors: &a" + infected.getSurvivorPlayers().size() + "/" + infected.getTotalPlayers());
                    toReturn.add("&fInfected: &c" + infected.getInfectedPlayers().size() + "/" + infected.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + infected.getRoundDuration());
                }
            }
            else if (brackets != null) {
                toReturn.add(CC.translate("&fHost: &b" + brackets.getName()));
                if (brackets.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + brackets.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");
                    if (brackets.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + brackets.getRoundDuration());
                    toReturn.add("");
                    toReturn.add("&b" + brackets.getRoundPlayerA().getUsername());
                    toReturn.add("&7vs");
                    toReturn.add("&b" + brackets.getRoundPlayerB().getUsername());
                }
            }
            else if (parkour != null) {
                toReturn.add(CC.translate("&fHost: &b" + parkour.getName()));
                if (parkour.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + parkour.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");
                    if (parkour.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + parkour.getRoundDuration());
                }
            }
            else if (wipeout != null) {
                toReturn.add(CC.translate("&fHost: &b" + wipeout.getName()));
                if (wipeout.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + wipeout.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                    toReturn.add("");
                    if (wipeout.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(wipeout.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + wipeout.getRemainingPlayers().size() + "/" + wipeout.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + wipeout.getRoundDuration());
                }
            }
            else if (skyWars != null) {
                toReturn.add(CC.translate("&fHost: &b" + skyWars.getName()));
                if (skyWars.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + skyWars.getEventPlayers().size() + "/" + skyWars.getMaxPlayers());
                    toReturn.add("");
                    if (skyWars.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(skyWars.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + skyWars.getRemainingPlayers().size() + "/" + skyWars.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + skyWars.getRoundDuration());
                }
            }
            else if (spleef != null) {
                toReturn.add(CC.translate("&fHost: &b" + spleef.getName()));
                if (spleef.isWaiting()) {
                    toReturn.add("&fPlayers: &b" + spleef.getEventPlayers().size() + "/" + spleef.getMaxPlayers());
                    toReturn.add("");
                    if (spleef.getCooldown() == null) {
                        toReturn.add(CC.translate("&fWaiting for players..."));
                    }
                    else {
                        String remaining = TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining = "0.0";
                        }
                        toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
                    }
                }
                else {
                    toReturn.add("&fRemaining: &b" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
                    toReturn.add("&fDuration: &b" + spleef.getRoundDuration());
                }
            }
        }
        else if (profile.isInSumo()) {
            final Sumo sumo2 = profile.getSumo();
            toReturn.add(CC.translate("&fHost: &b" + sumo2.getName()));
            if (sumo2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + sumo2.getEventPlayers().size() + "/" + sumo2.getMaxPlayers());
                toReturn.add("");
                if (sumo2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(sumo2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + sumo2.getRemainingPlayers().size() + "/" + sumo2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + sumo2.getRoundDuration());
                toReturn.add("");
                toReturn.add("&b" + sumo2.getRoundPlayerA().getUsername());
                toReturn.add("&7vs");
                toReturn.add("&b" + sumo2.getRoundPlayerB().getUsername());
            }
        }
        else if (profile.isInBrackets()) {
            final Brackets brackets2 = profile.getBrackets();
            toReturn.add(CC.translate("&fHost: &b" + brackets2.getName()));
            if (brackets2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + brackets2.getEventPlayers().size() + "/" + brackets2.getMaxPlayers());
                toReturn.add("");
                if (brackets2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(brackets2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + brackets2.getRemainingPlayers().size() + "/" + brackets2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + brackets2.getRoundDuration());
                toReturn.add("");
                toReturn.add("&b" + brackets2.getRoundPlayerA().getUsername());
                toReturn.add("&7vs");
                toReturn.add("&b" + brackets2.getRoundPlayerB().getUsername());
            }
        }
        else if (profile.isInFfa()) {
            final FFA ffa2 = profile.getFfa();
            toReturn.add(CC.translate("&fHost: &b" + ffa2.getName()));
            if (ffa2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + ffa2.getEventPlayers().size() + "/" + ffa2.getMaxPlayers());
                toReturn.add("");
                if (ffa2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(ffa2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + ffa2.getRemainingPlayers().size() + "/" + ffa2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + ffa2.getRoundDuration());
            }
        }
        else if (profile.isInJuggernaut()) {
            final Juggernaut juggernaut2 = profile.getJuggernaut();
            toReturn.add(CC.translate("&fHost: &b" + juggernaut2.getName()));
            if (juggernaut2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + juggernaut2.getEventPlayers().size() + "/" + juggernaut2.getMaxPlayers());
                toReturn.add("");
                if (juggernaut2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(juggernaut2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add(CC.translate("&bJuggernaut: &r" + juggernaut2.getJuggernaut().getPlayer().getName()));
                toReturn.add("&fRemaining: &b" + juggernaut2.getRemainingPlayers().size() + "/" + juggernaut2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + juggernaut2.getRoundDuration());
            }
        }
        else if (profile.isInInfected()) {
            final Infected infected2 = profile.getInfected();
            toReturn.add(CC.translate("&fHost: &b" + infected2.getName()));
            if (infected2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + infected2.getEventPlayers().size() + "/" + infected2.getMaxPlayers());
                toReturn.add("");
                if (infected2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(infected2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fSurvivors: &a" + infected2.getSurvivorPlayers().size() + "/" + infected2.getTotalPlayers());
                toReturn.add("&fInfected: &c" + infected2.getInfectedPlayers().size() + "/" + infected2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + infected2.getRoundDuration());
            }
        }
        else if (profile.isInParkour()) {
            final Parkour parkour2 = profile.getParkour();
            toReturn.add(CC.translate("&fHost: &b" + parkour2.getName()));
            if (parkour2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + parkour2.getEventPlayers().size() + "/" + parkour2.getMaxPlayers());
                toReturn.add("");
                if (parkour2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(parkour2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + parkour2.getRemainingPlayers().size() + "/" + parkour2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + parkour2.getRoundDuration());
            }
        }
        else if (profile.isInWipeout()) {
            final Wipeout wipeout2 = profile.getWipeout();
            toReturn.add(CC.translate("&fHost: &b" + wipeout2.getName()));
            if (wipeout2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + wipeout2.getEventPlayers().size() + "/" + wipeout2.getMaxPlayers());
                toReturn.add("");
                if (wipeout2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(wipeout2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + wipeout2.getRemainingPlayers().size() + "/" + wipeout2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + wipeout2.getRoundDuration());
            }
        }
        else if (profile.isInSkyWars()) {
            final SkyWars skyWars2 = profile.getSkyWars();
            toReturn.add(CC.translate("&fHost: &b" + skyWars2.getName()));
            if (skyWars2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + skyWars2.getEventPlayers().size() + "/" + skyWars2.getMaxPlayers());
                toReturn.add("");
                if (skyWars2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(skyWars2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + skyWars2.getRemainingPlayers().size() + "/" + skyWars2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + skyWars2.getRoundDuration());
            }
        }
        else if (profile.isInSpleef()) {
            final Spleef spleef2 = profile.getSpleef();
            toReturn.add(CC.translate("&fHost: &b" + spleef2.getName()));
            if (spleef2.isWaiting()) {
                toReturn.add("&fPlayers: &b" + spleef2.getEventPlayers().size() + "/" + spleef2.getMaxPlayers());
                toReturn.add("");
                if (spleef2.getCooldown() == null) {
                    toReturn.add(CC.translate("&fWaiting for players..."));
                }
                else {
                    String remaining2 = TimeUtil.millisToSeconds(spleef2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2 = "0.0";
                    }
                    toReturn.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            }
            else {
                toReturn.add("&fRemaining: &b" + spleef2.getRemainingPlayers().size() + "/" + spleef2.getTotalPlayers());
                toReturn.add("&fDuration: &b" + spleef2.getRoundDuration());
            }
        }
        toReturn.add("");
        toReturn.add(CC.translate("&7&omoonnight.rip"));
        toReturn.add(CC.SB_BAR);
        return toReturn;
    }
    
    public int getInQueues() {
        int inQueues = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && profile.isInQueue()) {
                ++inQueues;
            }
        }
        return inQueues;
    }
    
    public int getInFights() {
        int inFights = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && (profile.isInFight() || profile.isInEvent())) {
                ++inFights;
            }
        }
        return inFights;
    }
}

package me.array.ArrayPractice.scoreboard;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.lms.LMS;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.tournament.Tournament;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scoreboard implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return "&b&lResolve &7(Practice)";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        Profile profile=Profile.getByUuid(player.getUniqueId());

        if (!profile.getOptions().isShowScoreboard()) {
            return null;
        }

        final List<String> lines=new ArrayList<>();
        lines.add(CC.SB_BAR);
        if (profile.isInLobby() || profile.isInQueue()) {
            lines.add(CC.translate("&fOnline: &b" + Bukkit.getServer().getOnlinePlayers().size()));
            lines.add(CC.translate("&fIn Fights: &b" + this.getInFights()));
            lines.add(CC.translate("&fIn Queue: &b" + this.getInQueues()));
            if (profile.isFollowMode()) {
                lines.add("&7");
                lines.add("&fFollowing: &4" + profile.getFollowing().getName());
            } else if (profile.isSilent()) {
                lines.add("&7");
                lines.add("&7&lSilent Mode");
            }
            lines.add("");
            lines.add(CC.translate("&fELO: &b" + profile.getGlobalElo()));
            lines.add(CC.translate("&fLeague: &b" + profile.getEloLeague()));

            if (profile.getParty() != null && Tournament.CURRENT_TOURNAMENT == null) {
                final Party party=profile.getParty();
                lines.add(CC.SB_BAR);
                lines.add("&bParty:");
                int added=0;
                for ( final TeamPlayer teamPlayer : party.getTeamPlayers() ) {
                    ++added;
                    lines.add(" &b" + (party.getLeader().equals(teamPlayer) ? "*" : "-") + " &r" + teamPlayer.getUsername());
                    if (added >= 4) {
                        break;
                    }
                }
            }
            if (profile.isInQueue()) {
                final Queue queue=profile.getQueue();
                lines.add(CC.SB_BAR);
                lines.add("&bQueued For:");
                lines.add("&7" + queue.getQueueName());
                if (queue.getQueueType().equals(QueueType.RANKED)) {
                    lines.add("&fRange: &b" + profile.getQueueProfile().getMinRange() + " -> " + profile.getQueueProfile().getMaxRange());
                }
            } else if (Tournament.CURRENT_TOURNAMENT != null && profile.getParty() != null) {
                final Tournament tournament=Tournament.CURRENT_TOURNAMENT;
                final String round=(tournament.getRound() > 0) ? Integer.toString(tournament.getRound()) : "&fStarting";
                lines.add("");
                lines.add("&b&lTournament: &r");
                lines.add("&fKit: &b" + tournament.getLadder().getName() + " &7(" + tournament.getTeamCount() + "v" + tournament.getTeamCount() + ")");
                lines.add("&fRound: &b" + round);
                lines.add(((tournament.getTeamCount() > 1) ? "&fParties: &b" : "&fPlayers: &b") + tournament.getParticipatingCount() + "/" + tournament.getParticipants().size());
            }
        } else if (profile.isInFight()) {
            final Match match=profile.getMatch();
            if (match != null) {
                if (match.isSoloMatch()) {
                    final TeamPlayer self=match.getTeamPlayer(player);
                    final TeamPlayer opponent=match.getOpponentTeamPlayer(player);
                    lines.add("&fOpponent: &c" + opponent.getUsername());
                    lines.add("&fDuration: &b" + match.getDuration());
                    if (profile.getOptions().isPingScoreboard()) {
                        lines.add("");
                        lines.add("&fYour Ping: &a" + self.getPing() + "ms");
                        lines.add("&fEnemy Ping: &c" + opponent.getPing() + "ms");
                    }
                } else if (match.isSumoMatch()) {
                    TeamPlayer self=match.getTeamPlayer(player);
                    TeamPlayer opponent=match.getOpponentTeamPlayer(player);

                    Profile targetProfile=Profile.getByUuid(opponent.getUuid());

                    int selfPoints=profile.getSumoRounds();
                    int opPoints=targetProfile.getSumoRounds();

                    lines.add("&fOpponent: &b" + opponent.getUsername());
                    lines.add("&fPing: &a" + self.getPing() + "ms &7┃ &c" + opponent.getPing() + "ms");
                    lines.add("&fPoints: &a" + selfPoints + " &7┃ &c" + opPoints + "");

                } else if (match.isSumoTeamMatch()) {
                    Team team=match.getTeam(player);
                    Team opponentTeam=match.getOpponentTeam(player);

                    if ((team.getPlayers().size() + opponentTeam.getPlayers().size()) == 2) {
                        Team self=match.getTeam(player);
                        Team opponent=match.getOpponentTeam(self);

                        int selfPoints=self.getSumoRounds();
                        int opPoints=opponent.getSumoRounds();

                        lines.add("&fOpponent: &b" + opponent.getTeamPlayers().get(0).getUsername());
                        lines.add("&fPing: &a" + self.getTeamPlayers().get(0).getPing() + "ms &f┃ &c" + opponent.getTeamPlayers().get(0).getPing() + "ms");
                        lines.add("&fPoints: &a" + selfPoints + " &f┃ &c" + opPoints + "");

                    } else {
                        int selfPoints=team.getSumoRounds();
                        int opPoints=opponentTeam.getSumoRounds();

                        lines.add("&fYour Team: &a" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        lines.add("&fEnemy Team: &c" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                        lines.add("&fPoints: &a" + selfPoints + " &f┃ &c" + opPoints + "");
                    }

                } else if (match.isTeamMatch() || match.isHCFMatch()) {
                    final Team team=match.getTeam(player);
                    final Team opponentTeam=match.getOpponentTeam(player);
                    lines.add("&fDuration: &b" + match.getDuration());
                    if (team.getTeamPlayers().size() + opponentTeam.getTeamPlayers().size() <= 6) {
                        lines.add("&aTeam &a(" + team.getAliveCount() + "/" + team.getTeamPlayers().size() + ")");
                        lines.add("&cOpponents &c(" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size() + ")");
                    } else {
                        lines.add("&fTeam: &a" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        lines.add("&fOpponents: &c" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                    }
                } else if (match.isFreeForAllMatch()) {
                    final Team team=match.getTeam(player);
                    lines.add("&fOpponents: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    lines.add("&fDuration: &b" + match.getDuration());
                }
            }
            } else if (profile.isSpectating()) {
                final Match match =profile.getMatch();
                final Sumo sumo=profile.getSumo();
                final LMS ffa=profile.getLms();
                final Brackets brackets=profile.getBrackets();
                final Parkour parkour=profile.getParkour();
                final Spleef spleef=profile.getSpleef();
                if (match != null) {
                    if (!match.isHCFMatch() && !match.isKoTHMatch()) {
                        lines.add("&fKit: &b" + match.getKit().getName());
                    }
                    lines.add("&fDuration: &b" + match.getDuration());
                    lines.add("");
                    if (match.isSoloMatch()) {
                        int playera=PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
                        int playerb=PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());
                        lines.add(CC.AQUA + match.getTeamPlayerA().getUsername() + CC.GRAY + " (" + playera + ")");
                        lines.add("&7vs");
                        lines.add(CC.AQUA + match.getTeamPlayerB().getUsername() + CC.GRAY + " (" + playerb + ")");
                    } else if (match.isTeamMatch() || match.isHCFMatch() || match.isKoTHMatch()) {
                        lines.add("&b" + match.getTeamA().getLeader().getUsername() + "'s Team");
                        lines.add("&7vs");
                        lines.add("&b" + match.getTeamB().getLeader().getUsername() + "'s Team");
                    } else {
                        final Team team2=match.getTeam(player);
                        lines.add("&fAlive: &b" + team2.getAliveCount() + "/" + team2.getTeamPlayers().size());
                    }
                } else if (sumo != null) {
                    lines.add(CC.translate("&fHost: &b" + sumo.getName()));
                    if (sumo.isWaiting()) {
                        lines.add("&fPlayers: &b" + sumo.getEventPlayers().size() + "/" + sumo.getMaxPlayers());
                        lines.add("");
                        if (sumo.getCooldown() == null) {
                            lines.add(CC.translate("&fWaiting for players..."));
                        } else {
                            String remaining=TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
                            if (remaining.startsWith("-")) {
                                remaining="0";
                            }
                            lines.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                        }
                    } else {
                        lines.add("&fRemaining: &b" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
                        lines.add("&fDuration: &b" + sumo.getRoundDuration());
                        lines.add("");
                        lines.add("&b" + sumo.getRoundPlayerA().getUsername());
                        lines.add("&7vs");
                        lines.add("&b" + sumo.getRoundPlayerB().getUsername());
                    }
                } else if (ffa != null) {
                    lines.add(CC.translate("&fHost: &b" + ffa.getName()));
                    if (ffa.isWaiting()) {
                        lines.add("&fPlayers: &b" + ffa.getEventPlayers().size() + "/" + ffa.getMaxPlayers());
                        lines.add("");
                        if (ffa.getCooldown() == null) {
                            lines.add(CC.translate("&fWaiting for players..."));
                        } else {
                            String remaining=TimeUtil.millisToSeconds(ffa.getCooldown().getRemaining());
                            if (remaining.startsWith("-")) {
                                remaining="0.0";
                            }
                            lines.add(CC.translate("&fStarting in " + remaining + "s"));
                        }
                    } else {
                        lines.add("&fRemaining: &b" + ffa.getRemainingPlayers().size() + "/" + ffa.getTotalPlayers());
                        lines.add("&fDuration: &b" + ffa.getRoundDuration());
                    }
                } else if (brackets != null) {
                    lines.add(CC.translate("&fHost: &b" + brackets.getName()));
                    if (brackets.isWaiting()) {
                        lines.add("&fPlayers: &b" + brackets.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
                        lines.add("");
                        if (brackets.getCooldown() == null) {
                            lines.add(CC.translate("&fWaiting for players..."));
                        } else {
                            String remaining=TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());
                            if (remaining.startsWith("-")) {
                                remaining="0.0";
                            }
                            lines.add(CC.translate("&fStarting in " + remaining + "s"));
                        }
                    } else {
                        lines.add("&fRemaining: &b" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
                        lines.add("&fDuration: &b" + brackets.getRoundDuration());
                        lines.add("");
                        lines.add("&b" + brackets.getRoundPlayerA().getUsername());
                        lines.add("&7vs");
                        lines.add("&b" + brackets.getRoundPlayerB().getUsername());
                    }
                } else if (parkour != null) {
                    lines.add(CC.translate("&fHost: &b" + parkour.getName()));
                    if (parkour.isWaiting()) {
                        lines.add("&fPlayers: &b" + parkour.getEventPlayers().size() + "/" + parkour.getMaxPlayers());
                        lines.add("");
                        if (parkour.getCooldown() == null) {
                            lines.add(CC.translate("&fWaiting for players..."));
                        } else {
                            String remaining=TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());
                            if (remaining.startsWith("-")) {
                                remaining="0.0";
                            }
                            lines.add(CC.translate("&fStarting in " + remaining + "s"));
                        }
                    } else {
                        lines.add("&fRemaining: &b" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
                        lines.add("&fDuration: &b" + parkour.getRoundDuration());
                    }
                } else if (spleef != null) {
                    lines.add(CC.translate("&fHost: &b" + spleef.getName()));
                    if (spleef.isWaiting()) {
                        lines.add("&fPlayers: &b" + spleef.getEventPlayers().size() + "/" + spleef.getMaxPlayers());
                        lines.add("");
                        if (spleef.getCooldown() == null) {
                            lines.add(CC.translate("&fWaiting for players..."));
                        } else {
                            String remaining=TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());
                            if (remaining.startsWith("-")) {
                                remaining="0.0";
                            }
                            lines.add(CC.translate("&fStarting in " + remaining + "s"));
                        }
                    } else {
                        lines.add("&fRemaining: &b" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
                        lines.add("&fDuration: &b" + spleef.getRoundDuration());
                    }
                }
            } else if (profile.isInSumo()) {
                final Sumo sumo2=profile.getSumo();
                lines.add(CC.translate("&fHost: &b" + sumo2.getName()));
                if (sumo2.isWaiting()) {
                    lines.add("&fPlayers: &b" + sumo2.getEventPlayers().size() + "/" + sumo2.getMaxPlayers());
                    lines.add("");
                    if (sumo2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(sumo2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&fRemaining: &b" + sumo2.getRemainingPlayers().size() + "/" + sumo2.getTotalPlayers());
                    lines.add("&fDuration: &b" + sumo2.getRoundDuration());
                    lines.add("");
                    lines.add("&b" + sumo2.getRoundPlayerA().getUsername());
                    lines.add("&7vs");
                    lines.add("&b" + sumo2.getRoundPlayerB().getUsername());
                }
            } else if (profile.isInBrackets()) {
                final Brackets brackets2=profile.getBrackets();
                lines.add(CC.translate("&fHost: &b" + brackets2.getName()));
                if (brackets2.isWaiting()) {
                    lines.add("&fPlayers: &b" + brackets2.getEventPlayers().size() + "/" + brackets2.getMaxPlayers());
                    lines.add("");
                    if (brackets2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(brackets2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0.0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&fRemaining: &b" + brackets2.getRemainingPlayers().size() + "/" + brackets2.getTotalPlayers());
                    lines.add("&fDuration: &b" + brackets2.getRoundDuration());
                    lines.add("");
                    lines.add("&b" + brackets2.getRoundPlayerA().getUsername());
                    lines.add("&7vs");
                    lines.add("&b" + brackets2.getRoundPlayerB().getUsername());
                }
            } else if (profile.isInLMS()) {
                final LMS ffa2=profile.getLms();
                lines.add(CC.translate("&fHost: &b" + ffa2.getName()));
                if (ffa2.isWaiting()) {
                    lines.add("&fPlayers: &b" + ffa2.getEventPlayers().size() + "/" + ffa2.getMaxPlayers());
                    lines.add("");
                    if (ffa2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(ffa2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0.0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&fRemaining: &b" + ffa2.getRemainingPlayers().size() + "/" + ffa2.getTotalPlayers());
                    lines.add("&fDuration: &b" + ffa2.getRoundDuration());
                }
            } else if (profile.isInParkour()) {
                final Parkour parkour2=profile.getParkour();
                lines.add(CC.translate("&fHost: &b" + parkour2.getName()));
                if (parkour2.isWaiting()) {
                    lines.add("&fPlayers: &b" + parkour2.getEventPlayers().size() + "/" + parkour2.getMaxPlayers());
                    lines.add("");
                    if (parkour2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(parkour2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0.0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&fRemaining: &b" + parkour2.getRemainingPlayers().size() + "/" + parkour2.getTotalPlayers());
                    lines.add("&fDuration: &b" + parkour2.getRoundDuration());
                }
            } else if (profile.isInSpleef()) {
                final Spleef spleef2=profile.getSpleef();
                lines.add(CC.translate("&fHost: &b" + spleef2.getName()));
                if (spleef2.isWaiting()) {
                    lines.add("&fPlayers: &b" + spleef2.getEventPlayers().size() + "/" + spleef2.getMaxPlayers());
                    lines.add("");
                    if (spleef2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(spleef2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0.0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&fRemaining: &b" + spleef2.getRemainingPlayers().size() + "/" + spleef2.getTotalPlayers());
                    lines.add("&fDuration: &b" + spleef2.getRoundDuration());
                }
            }
        lines.add("");
        lines.add(CC.translate("&7&oresolve.rip"));
        lines.add(CC.SB_BAR);
        return lines;
    }
    
    public int getInQueues() {
        int inQueues = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInQueue()) {
                ++inQueues;
            }
        }
        return inQueues;
    }
    
    public int getInFights() {
        int inFights = 0;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isInFight() || profile.isInEvent()) {
                ++inFights;
            }
        }
        return inFights;
    }
}

package me.drizzy.practice.scoreboard;

import me.drizzy.practice.Array;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.gulag.Gulag;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.hcf.HCFClasses;
import me.drizzy.practice.hcf.classes.Bard;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.TimeUtil;
import me.drizzy.practice.util.scoreboard.scoreboard.Board;
import me.drizzy.practice.util.scoreboard.scoreboard.BoardAdapter;
import me.drizzy.practice.util.scoreboard.scoreboard.cooldown.BoardCooldown;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scoreboard implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return "&b&lPurge &7&l┃ &fTest Server";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        Profile profile=Profile.getByUuid(player.getUniqueId());

        if (!profile.getSettings().isShowScoreboard()) {
            return null;
        }

        final List<String> lines=new ArrayList<>();
        lines.add(CC.SB_BAR);
        if (profile.isInLobby() || profile.isInQueue()) {
            lines.add("&b&lPractice");
            lines.add("&f Online: &b" + ArrayCache.getOnline());
            lines.add("&f Fighting: &b" + ArrayCache.getInFights());
            lines.add("");
            lines.add("&f Division: &b" + profile.getEloLeague());
            lines.add("&f ELO: &b" + profile.getGlobalElo());
            if (profile.getParty() != null && Tournament.CURRENT_TOURNAMENT == null) {
                final Party party=profile.getParty();
                lines.add("");
                lines.add("&f Leader: &b" + party.getLeader().getUsername());
                lines.add("&f Members: &b" + party.getPlayers().size());
            }
            if (profile.isInQueue()) {
                final Queue queue = profile.getQueue();
                lines.add(CC.SB_BAR);
                lines.add("&b" + queue.getQueueName());
                lines.add("&f Time: &b" + queue.getDuration(player));
                if (queue.getQueueType().equals(QueueType.RANKED)) {
                    lines.add("&f Range: &b" + profile.getQueueProfile().getMinRange() + " -> " + profile.getQueueProfile().getMaxRange());
                }
            } else if (Tournament.CURRENT_TOURNAMENT != null && profile.getParty() != null) {
                final Tournament tournament=Tournament.CURRENT_TOURNAMENT;
                final String round=(tournament.getRound() > 0) ? Integer.toString(tournament.getRound()) : "&fStarting";
                lines.add("");
                lines.add("&b&lTournament");
                lines.add("&f Kit: &b" + tournament.getLadder().getName() + " &7(" + tournament.getTeamCount() + "v" + tournament.getTeamCount() + ")");
                lines.add("&f Round: &b" + round);
                lines.add(((tournament.getTeamCount() > 1) ? "&f Parties: &b" : "&f Players: &b") + tournament.getParticipatingCount() + "/" + tournament.getParticipants().size());
            }
        } else if (profile.isInFight()) {
            final Match match=profile.getMatch();
            if (match != null) {
                if (match.isSoloMatch()) {
                    final TeamPlayer self=match.getTeamPlayer(player);
                    final TeamPlayer opponent=match.getOpponentTeamPlayer(player);
                    lines.add("&b&lFight");
                    lines.add("&f Rival: &b" + opponent.getUsername());
                    lines.add("&f Duration: &b" + match.getDuration());
                    if (profile.getSettings().isPingScoreboard()) {
                        lines.add("");
                        lines.add("&f Your Ping: &b" + self.getPing() + "ms");
                        lines.add("&f Their Ping: &b" + opponent.getPing() + "ms");
                    }
                } else if (match.isSumoMatch()) {
                    TeamPlayer self=match.getTeamPlayer(player);
                    TeamPlayer opponent=match.getOpponentTeamPlayer(player);

                    Profile targetProfile=Profile.getByUuid(opponent.getUuid());

                    int selfPoints=profile.getSumoRounds();
                    int opPoints=targetProfile.getSumoRounds();

                    lines.add("&b&lFight");
                    lines.add("&f Rival: &b" + opponent.getUsername());
                    lines.add("&f Points: &b" + selfPoints + " &7┃ &b" + opPoints + "");
                    lines.add("");
                    lines.add("&f Your Ping: &b" + self.getPing() + "ms");
                    lines.add("&f Their Ping: &b" + opponent.getPing() + "ms");
                } else if (match.isTheBridgeMatch()) {
                    TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;
                    TeamPlayer opponent=match.getOpponentTeamPlayer(player);

                    lines.add("&b&lFight");
                    lines.add("&f Rival: &b" + opponent.getUsername());
                    lines.add("&f Duration: &b" + match.getDuration());
                    lines.add("");
                    lines.add("&b&lRound #" + bridgeMatch.getRound());
                    lines.add("&c Red: &r" + this.getFormattedPoints(match.getTeamPlayerA().getPlayer()) + (match.getTeamPlayerA().getPlayer() == player ? " &7(You)" : ""));
                    lines.add("&9 Blue: &r" + this.getFormattedPoints(match.getTeamPlayerB().getPlayer()) + (match.getTeamPlayerB().getPlayer() == player ? " &7(You)" : ""));
                    if (!profile.getBowCooldown().hasExpired()) {
                        lines.add("");
                        lines.add("&b&lBow Cooldown");
                        lines.add(" &f" + profile.getBowCooldown().getTimeLeft());
                    }

                } else if (match.isSumoTeamMatch()) {
                    Team team=match.getTeam(player);
                    Team opponentTeam=match.getOpponentTeam(player);
                    lines.add("&b&lFight");
                    if ((team.getPlayers().size() + opponentTeam.getPlayers().size()) == 2) {
                        Team self=match.getTeam(player);
                        Team opponent=match.getOpponentTeam(self);

                        int selfPoints=self.getSumoRounds();
                        int opPoints=opponent.getSumoRounds();

                        lines.add("&f Rival: &b" + opponent.getLeader().getUsername() + "'s Party");
                        lines.add("&f Points: &b" + selfPoints + " &7┃ &b" + opPoints + "");
                        lines.add("");
                        lines.add("&f Your Ping: &b" + PlayerUtil.getPing(player) + "ms");
                        lines.add("&f Their Ping: &b" + opponent.getTeamPlayers().get(0).getPing() + "ms");
                    } else {
                        int selfPoints=team.getSumoRounds();
                        int opPoints=opponentTeam.getSumoRounds();

                        lines.add(" &fYour Team: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        lines.add(" &fTheir Team: &b" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                        lines.add(" &fPoints: &b" + selfPoints + " &f┃ &b" + opPoints);
                    }

                } else if (match.isTeamMatch() || match.isHCFMatch()) {
                    final Team team=match.getTeam(player);
                    final Team opponentTeam=match.getOpponentTeam(player);
                    lines.add("&b&lFight");
                    lines.add("&f Duration: &b" + match.getDuration());
                    lines.add("&f Kit: &b" + (match.isHCFMatch() ? "HCF" : match.getKit().getDisplayName()));
                    lines.add("");
                    lines.add("&f Your Team: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    lines.add("&f Rival Team: &b" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());

                    if (match.isHCFMatch()) {
                        final HCFClasses pvpClass=Array.getInstance().getHCFManager().getEquippedClass(player);
                        if (pvpClass instanceof Bard) {
                            final Bard bardClass=(Bard) pvpClass;
                            lines.add("&f Bard Energy: &b" + bardClass.getEnergy(player));
                        }
                    }
                } else if (match.isFreeForAllMatch()) {
                    final Team team=match.getTeam(player);
                    lines.add("&b&lFight");
                    lines.add("&f Players: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    lines.add("&f Duration: &b" + match.getDuration());
                }
            }
        } else if (profile.isSpectating()) {
            final Match match=profile.getMatch();
            final Sumo sumo=profile.getSumo();
            final LMS ffa=profile.getLms();
            final Brackets brackets=profile.getBrackets();
            final Parkour parkour=profile.getParkour();
            final Spleef spleef=profile.getSpleef();
            final Gulag gulag=profile.getGulag();
            if (match != null) {
                lines.add("&b&lFight");
                if (!match.isHCFMatch()) {
                    lines.add("&f Kit: &b" + match.getKit().getName());
                }
                lines.add("&f Duration: &b" + match.getDuration());
                lines.add("");
                if (match.isSoloMatch() || match.isSumoMatch() || match.isTheBridgeMatch()) {
                    int playera=PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());
                    lines.add(" " + CC.AQUA + match.getTeamPlayerA().getUsername() + CC.translate(" &8(&b" + playera + "&8)"));
                    lines.add("&7 vs");
                    lines.add(" " + CC.AQUA + match.getTeamPlayerB().getUsername() + CC.translate(" &8(&b" + playerb + "&8)"));
                } else if (match.isTeamMatch() || match.isHCFMatch() || match.isSumoTeamMatch()) {
                    lines.add("&b " + match.getTeamA().getLeader().getUsername() + "'s Team &8(&f" + match.getTeamA().getPlayers().size() + "&8)");
                    lines.add("&7 vs");
                    lines.add("&b " + match.getTeamB().getLeader().getUsername() + "'s Team &8(&f" + match.getTeamB().getPlayers().size() + "&8)");
                } else {
                    final Team team2=match.getTeam(player);
                    lines.add("&f Alive: &b" + team2.getAliveCount() + "/" + team2.getTeamPlayers().size());
                }
            } else if (sumo != null) {
                if (sumo.isWaiting()) {
                    lines.add("&b&lSumo Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + sumo.getName()));
                    lines.add("&fPlayers: &b" + sumo.getEventPlayers().size() + "/" + Sumo.getMaxPlayers());
                    lines.add("");
                    if (sumo.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining=TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0";
                        }
                        lines.add("&fStarting in " + CC.AQUA + remaining + "&fs");
                    }
                } else {
                    int playera=PlayerUtil.getPing(sumo.getRoundPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(sumo.getRoundPlayerB().getPlayer());
                    lines.add("&b&lSumo Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + sumo.getRemainingPlayers().size() + "/" + Sumo.getMaxPlayers());
                    lines.add("&fDuration: &b" + sumo.getRoundDuration());
                    lines.add("");
                    lines.add("&b" + sumo.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                    lines.add("&7vs");
                    lines.add("&b" + sumo.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
                }
            } else if (gulag != null) {
                if (gulag.isWaiting()) {
                    lines.add("&b&lGulag Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + gulag.getName()));
                    lines.add("&fPlayers: &b" + gulag.getEventPlayers().size() + "/" + Gulag.getMaxPlayers());
                    lines.add("");
                    if (gulag.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining=TimeUtil.millisToSeconds(gulag.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0";
                        }
                        lines.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                    }
                } else {
                    int playera=PlayerUtil.getPing(gulag.getRoundPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(gulag.getRoundPlayerB().getPlayer());
                    lines.add("&b&lGulag Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + gulag.getRemainingPlayers().size() + "/" + Gulag.getMaxPlayers());
                    lines.add("&fDuration: &b" + gulag.getRoundDuration());
                    lines.add("");
                    lines.add("&b" + gulag.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                    lines.add("&7vs");
                    lines.add("&b" + gulag.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
                }
            } else if (ffa != null) {
                if (ffa.isWaiting()) {
                    lines.add("&b&lLMS Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + ffa.getName()));
                    lines.add("&fPlayers: &b" + ffa.getEventPlayers().size() + "/" + LMS.getMaxPlayers());
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
                    lines.add("&bLMS Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + ffa.getRemainingPlayers().size() + "/" + LMS.getMaxPlayers());
                    lines.add("&fDuration: &b" + ffa.getRoundDuration());
                }
            } else if (brackets != null) {
                if (brackets.isWaiting()) {
                    lines.add("&b&lBrackets Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + brackets.getName()));
                    lines.add("&fPlayers: &b" + brackets.getEventPlayers().size() + "/" + Brackets.getMaxPlayers());
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
                    int playera=PlayerUtil.getPing(brackets.getRoundPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(brackets.getRoundPlayerB().getPlayer());
                    lines.add("&b&lBrackets Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + brackets.getRemainingPlayers().size() + "/" + Brackets.getMaxPlayers());
                    lines.add("&fDuration: &b" + brackets.getRoundDuration());
                    lines.add("");
                    lines.add("&b" + brackets.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                    lines.add("&7vs");
                    lines.add("&b" + brackets.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
                }
            } else if (parkour != null) {
                if (parkour.isWaiting()) {
                    lines.add("&b&lParkour Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + parkour.getName()));
                    lines.add("&fPlayers: &b" + parkour.getEventPlayers().size() + "/" + Parkour.getMaxPlayers());
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
                    lines.add("&b&lParkour Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + parkour.getRemainingPlayers().size() + "/" + Parkour.getMaxPlayers());
                    lines.add("&fDuration: &b" + parkour.getRoundDuration());
                }
            } else if (spleef != null) {
                if (spleef.isWaiting()) {
                    lines.add("&b&lSpleef Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + spleef.getName()));
                    lines.add("&fPlayers: &b" + spleef.getEventPlayers().size() + "/" + Spleef.getMaxPlayers());
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
                    lines.add("&b&lSpleef Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + spleef.getRemainingPlayers().size() + "/" + Spleef.getMaxPlayers());
                    lines.add("&fDuration: &b" + spleef.getRoundDuration());
                }
            }
        } else if (profile.isInSumo()) {
            final Sumo sumo2=profile.getSumo();
            if (sumo2.isWaiting()) {
                lines.add("&b&lSumo Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + sumo2.getName()));
                lines.add("&fPlayers: &b" + sumo2.getEventPlayers().size() + "/" + Sumo.getMaxPlayers());
                lines.add("");
                if (sumo2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(sumo2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0";
                    }
                    lines.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                }
            } else {
                int playera=PlayerUtil.getPing(sumo2.getRoundPlayerA().getPlayer());
                int playerb=PlayerUtil.getPing(sumo2.getRoundPlayerB().getPlayer());
                lines.add("&b&lSumo Event");
                lines.add("");
                lines.add("&fPlayers: &b" + sumo2.getRemainingPlayers().size() + "/" + Sumo.getMaxPlayers());
                lines.add("&fDuration: &b" + sumo2.getRoundDuration());
                lines.add("");
                lines.add("&b" + sumo2.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                lines.add("&7vs");
                lines.add("&b" + sumo2.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
            }
        } else if (profile.isInGulag()) {
            final Gulag gulag2=profile.getGulag();
            if (gulag2.isWaiting()) {
                lines.add("&b&lGulag Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + gulag2.getName()));
                lines.add("&fPlayers: &b" + gulag2.getEventPlayers().size() + "/" + Gulag.getMaxPlayers());
                lines.add("");
                if (gulag2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(gulag2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0";
                    }
                    lines.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                }
            } else {
                int playera=PlayerUtil.getPing(gulag2.getRoundPlayerA().getPlayer());
                int playerb=PlayerUtil.getPing(gulag2.getRoundPlayerB().getPlayer());
                lines.add("&b&lGulag Event");
                lines.add("");
                lines.add("&fPlayers: &b" + gulag2.getRemainingPlayers().size() + "/" + Gulag.getMaxPlayers());
                lines.add("&fDuration: &b" + gulag2.getRoundDuration());
                lines.add("");
                lines.add("&b" + gulag2.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                lines.add("&7vs");
                lines.add("&b" + gulag2.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
            }
        } else if (profile.isInBrackets()) {
            final Brackets brackets2=profile.getBrackets();
            if (brackets2.isWaiting()) {
                lines.add("&b&lBrackets Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + brackets2.getName()));
                lines.add("&fPlayers: &b" + brackets2.getEventPlayers().size() + "/" + Brackets.getMaxPlayers());
                lines.add("");
                if (brackets2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(brackets2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    lines.add(CC.translate("&fStarting in " + remaining + "s"));
                }
            } else {
                int playera=PlayerUtil.getPing(brackets2.getRoundPlayerA().getPlayer());
                int playerb=PlayerUtil.getPing(brackets2.getRoundPlayerB().getPlayer());
                lines.add("&b&lBrackets Event");
                lines.add("");
                lines.add("&fPlayers: &b" + brackets2.getRemainingPlayers().size() + "/" + Brackets.getMaxPlayers());
                lines.add("&fDuration: &b" + brackets2.getRoundDuration());
                lines.add("");
                lines.add("&a" + brackets2.getRoundPlayerA().getUsername() + " &8(&b" + playera + "&8)");
                lines.add("&7vs");
                lines.add("&c" + brackets2.getRoundPlayerB().getUsername() + " &8(&b" + playerb + "&8)");
            }
        } else if (profile.isInLMS()) {
            final LMS ffa2=profile.getLms();
            if (ffa2.isWaiting()) {
                lines.add("&b&lLMS Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + ffa2.getName()));
                lines.add("&fPlayers: &b" + ffa2.getEventPlayers().size() + "/" + LMS.getMaxPlayers());
                lines.add("");
                if (ffa2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(ffa2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    lines.add(CC.translate("&fStarting in " + remaining + "s"));
                }
            } else {
                lines.add("&bLMS Event");
                lines.add("");
                lines.add("&fPlayers: &b" + ffa2.getRemainingPlayers().size() + "/" + LMS.getMaxPlayers());
                lines.add("&fDuration: &b" + ffa2.getRoundDuration());
            }
        } else if (profile.isInParkour()) {
            final Parkour parkour2=profile.getParkour();
            if (parkour2.isWaiting()) {
                lines.add("&b&lParkour Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + parkour2.getName()));
                lines.add("&fPlayers: &b" + parkour2.getEventPlayers().size() + "/" + Parkour.getMaxPlayers());
                lines.add("");
                if (parkour2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(parkour2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    lines.add(CC.translate("&fStarting in " + remaining + "s"));
                }
            } else {
                lines.add("&b&lParkour Event");
                lines.add("");
                lines.add("&fPlayers: &b" + parkour2.getRemainingPlayers().size() + "/" + Parkour.getMaxPlayers());
                lines.add("&fDuration: &b" + parkour2.getRoundDuration());
            }
        } else if (profile.isInSpleef()) {
            final Spleef spleef2=profile.getSpleef();
            if (spleef2.isWaiting()) {
                lines.add("&b&lSpleef Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + spleef2.getName()));
                lines.add("&fPlayers: &b" + spleef2.getEventPlayers().size() + "/" + Spleef.getMaxPlayers());
                lines.add("");
                if (spleef2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining=TimeUtil.millisToSeconds(spleef2.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    lines.add(CC.translate("&fStarting in " + remaining + "s"));
                }
            } else {
                lines.add("&b&lSpleef Event");
                lines.add("");
                lines.add("&fPlayers: &b" + spleef2.getRemainingPlayers().size() + "/" + Spleef.getMaxPlayers());
                lines.add("&fDuration: &b" + spleef2.getRoundDuration());
            }
        }
        if (profile.isFollowMode()) {
            lines.add("&7");
            lines.add(" &fFollowing: &4" + profile.getFollowing().getName());
        }
        lines.add("");
        lines.add(CC.translate("&7&opurgecommunity.com"));
        lines.add(CC.SB_BAR);
        return lines;
    }


    @SuppressWarnings(value = "all")
    public String getFormattedPoints(Player player) {
        if( Profile.getByUuid(player) != null) {
            Profile profile=Profile.getByUuid(player);
            int points=profile.getBridgeRounds();

            if (points == 3) {
                return CC.translate("&a■&a■&a■");
            }

            if (points == 2) {
                return CC.translate("&a■&a■&7■");
            }

            if (points == 1) {
                return CC.translate("&a■&7■■");
            }
        }
        return CC.translate("&7■■■");
    }


}

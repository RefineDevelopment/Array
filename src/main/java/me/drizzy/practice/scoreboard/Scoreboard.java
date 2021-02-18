package me.drizzy.practice.scoreboard;

import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.skywars.SkyWars;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.kits.Bard;
import me.drizzy.practice.match.kits.utils.ArmorClass;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.scoreboard.scoreboard.Board;
import me.drizzy.practice.util.scoreboard.scoreboard.BoardAdapter;
import me.drizzy.practice.util.scoreboard.scoreboard.cooldown.BoardCooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scoreboard implements BoardAdapter {

    @Override
    public String getTitle(Player player) {
        return "&b&lPurge &7&l┃ &fPractice";
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
            lines.add(CC.translate("&fOnline: &b" + Bukkit.getServer().getOnlinePlayers().size()));
            lines.add(CC.translate("&fIn Fights: &b" + this.getInFights()));
            lines.add(CC.translate("&fIn Queue: &b" + this.getInQueues()));
            lines.add("");
            lines.add("&fELO: &b" + profile.getGlobalElo());
            lines.add("&fLeague: &b" + profile.getEloLeague());
            if (profile.isFollowMode()) {
                lines.add("&7");
                lines.add(" &fFollowing: &4" + profile.getFollowing().getName());
            } if (profile.isSilent()) {
                lines.add("&7");
                lines.add(" &7&lSilent Mode");
            }

            if (profile.getParty() != null && Tournament.CURRENT_TOURNAMENT == null) {
                final Party party=profile.getParty();
                lines.add("");
                lines.add("&bYour Party");
                int added=0;
                for ( final TeamPlayer teamPlayer : party.getTeamPlayers() ) {
                    ++added;
                    lines.add("&b" + (party.getLeader().equals(teamPlayer) ? "*" : "-") + " &r" + teamPlayer.getUsername());
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
                    lines.add("&fOpponent: &b" + opponent.getUsername());
                    lines.add("&fDuration: &b" + match.getDuration());
                    if (profile.getSettings().isPingScoreboard()) {
                        lines.add("");
                        lines.add("&fYour Ping: &b" + self.getPing() + "ms");
                        lines.add("&fEnemy Ping: &b" + opponent.getPing() + "ms");
                    }
                } else if (match.isSumoMatch()) {
                    TeamPlayer self=match.getTeamPlayer(player);
                    TeamPlayer opponent=match.getOpponentTeamPlayer(player);

                    Profile targetProfile=Profile.getByUuid(opponent.getUuid());

                    int selfPoints=profile.getSumoRounds();
                    int opPoints=targetProfile.getSumoRounds();

                    lines.add("&fOpponent: &b" + opponent.getUsername());
                    lines.add("&fPoints: &b" + selfPoints + " &7┃ &b" + opPoints + "");
                    lines.add("");
                    lines.add("&fYour Ping: &b" + self.getPing() + "ms");
                    lines.add("&fEnemy Ping: &b" + opponent.getPing() + "ms");

                } else if (match.isSumoTeamMatch()) {
                    Team team=match.getTeam(player);
                    Team opponentTeam=match.getOpponentTeam(player);

                    if ((team.getPlayers().size() + opponentTeam.getPlayers().size()) == 2) {
                        Team self=match.getTeam(player);
                        Team opponent=match.getOpponentTeam(self);

                        int selfPoints=self.getSumoRounds();
                        int opPoints=opponent.getSumoRounds();

                        lines.add("&fOpponent: &b" + opponent.getLeader().getUsername() + "'s Party");
                        lines.add("&fPoints: &b" + selfPoints + " &7┃ &b" + opPoints + "");
                        lines.add("");
                        lines.add("&fYour Ping: &b" + PlayerUtil.getPing(player) + "ms");
                        lines.add("&fEnemy Ping: &b" + opponent.getTeamPlayers().get(0).getPing() + "ms");
                    } else {
                        int selfPoints=team.getSumoRounds();
                        int opPoints=opponentTeam.getSumoRounds();

                        lines.add(" &fYour Team: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                        lines.add(" &fEnemy Team: &b" + opponentTeam.getAliveCount() + "/" + opponentTeam.getTeamPlayers().size());
                        lines.add(" &fPoints: &b" + selfPoints + " &f┃ &b" + opPoints);
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
                    if (match.isHCFMatch()) {
                        final ArmorClass pvpClass = Array.getInstance().getArmorClassManager().getEquippedClass(player);
                        if (pvpClass instanceof Bard) {
                            final Bard bardClass = (Bard)pvpClass;
                            lines.add("&fBard Energy: &e" + bardClass.getEnergy(player));
                        }
                    }
                } else if (match.isFreeForAllMatch()) {
                    final Team team=match.getTeam(player);
                    lines.add("&fPlayers: &b" + team.getAliveCount() + "/" + team.getTeamPlayers().size());
                    lines.add("&fDuration: &b" + match.getDuration());
                }
            }
        } else if (profile.isSpectating()) {
            final Match match=profile.getMatch();
            final Sumo sumo=profile.getSumo();
            final LMS ffa=profile.getLms();
            final Brackets brackets=profile.getBrackets();
            final Parkour parkour=profile.getParkour();
            final Spleef spleef=profile.getSpleef();
            if (match != null) {
                if (!match.isHCFMatch()) {
                    lines.add("&fKit: &b" + match.getKit().getName());
                }
                lines.add("&fDuration: &b" + match.getDuration());
                lines.add("");
                if (match.isSoloMatch()) {
                    int playera=PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());
                    lines.add(CC.GREEN + match.getTeamPlayerA().getUsername() + CC.translate(" &8(&a" + playera + "&8)"));
                    lines.add("&7vs");
                    lines.add(CC.RED + match.getTeamPlayerB().getUsername() + CC.translate(" &8(&c" + playerb + "&8)"));
                } else if (match.isTeamMatch() || match.isHCFMatch()) {
                    lines.add("&b" + match.getTeamA().getLeader().getUsername() + "'s Team");
                    lines.add("&7vs");
                    lines.add("&b" + match.getTeamB().getLeader().getUsername() + "'s Team");
                } else {
                    final Team team2=match.getTeam(player);
                    lines.add("&fAlive: &b" + team2.getAliveCount() + "/" + team2.getTeamPlayers().size());
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
                        lines.add(CC.translate("&fStarting in " + CC.AQUA + remaining + "&fs"));
                    }
                } else {
                    int playera=PlayerUtil.getPing(sumo.getRoundPlayerA().getPlayer());
                    int playerb=PlayerUtil.getPing(sumo.getRoundPlayerB().getPlayer());
                    lines.add("&b&lSumo Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + sumo.getRemainingPlayers().size() + "/" + Sumo.getMaxPlayers());
                    lines.add("&fDuration: &b" + sumo.getRoundDuration());
                    lines.add("");
                    lines.add("&a" + sumo.getRoundPlayerA().getUsername() + " &8(&a" + playera + "&8)");
                    lines.add("&7vs");
                    lines.add("&c" + sumo.getRoundPlayerB().getUsername() + " &8(&c" + playerb + "&8)");
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
                    lines.add("&a" + brackets.getRoundPlayerA().getUsername() + " &8(&a" + playera + "&8)");
                    lines.add("&7vs");
                    lines.add("&c" + brackets.getRoundPlayerB().getUsername() + " &8(&c" + playerb + "&8)");
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
            } else if (profile.getSkyWars() !=null) {
                final SkyWars skywars2=profile.getSkyWars();
                if (skywars2.isWaiting()) {
                    lines.add("&b&lSkywars Event");
                    lines.add("");
                    lines.add(CC.translate("&fHost: &b" + skywars2.getName()));
                    lines.add("&fPlayers: &b" + skywars2.getEventPlayers().size() + "/" + SkyWars.getMaxPlayers());
                    lines.add("");
                    if (skywars2.getCooldown() == null) {
                        lines.add(CC.translate("&fWaiting for players..."));
                    } else {
                        String remaining2=TimeUtil.millisToSeconds(skywars2.getCooldown().getRemaining());
                        if (remaining2.startsWith("-")) {
                            remaining2="0.0";
                        }
                        lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                    }
                } else {
                    lines.add("&b&lSkywars Event");
                    lines.add("");
                    lines.add("&fPlayers: &b" + skywars2.getRemainingPlayers().size() + "/" + SkyWars.getMaxPlayers());
                    lines.add("&fDuration: &b" + skywars2.getRoundDuration());
                }
            }
        } else if (profile.isInSumo()) {
            final Sumo sumo2 = profile.getSumo();
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
                lines.add("&a" + sumo2.getRoundPlayerA().getUsername() + " &8(" + playera + "&8)");
                lines.add("&7vs");
                lines.add("&c" + sumo2.getRoundPlayerB().getUsername() + " &8(" + playerb + "&8)");
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
                lines.add("&a" + brackets2.getRoundPlayerA().getUsername() + " &8(" + playera + "&8)");
                lines.add("&7vs");
                lines.add("&c" + brackets2.getRoundPlayerB().getUsername() + " &8(" + playerb + "&8)");
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
            final Parkour parkour2 = profile.getParkour();
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
        } else if (profile.isInSkyWars()) {
            final SkyWars skywars2=profile.getSkyWars();
            if (skywars2.isWaiting()) {
                lines.add("&b&lSkywars Event");
                lines.add("");
                lines.add(CC.translate("&fHost: &b" + skywars2.getName()));
                lines.add("&fPlayers: &b" + skywars2.getEventPlayers().size() + "/" + SkyWars.getMaxPlayers());
                lines.add("");
                if (skywars2.getCooldown() == null) {
                    lines.add(CC.translate("&fWaiting for players..."));
                } else {
                    String remaining2=TimeUtil.millisToSeconds(skywars2.getCooldown().getRemaining());
                    if (remaining2.startsWith("-")) {
                        remaining2="0.0";
                    }
                    lines.add(CC.translate("&fStarting in " + remaining2 + "s"));
                }
            } else {
                lines.add("&b&lSkywars Event");
                lines.add("");
                lines.add("&fPlayers: &b" + skywars2.getRemainingPlayers().size() + "/" + SkyWars.getMaxPlayers());
                lines.add("&fDuration: &b" + skywars2.getRoundDuration());
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
        lines.add("");
        lines.add(CC.translate("&7&opurgecommunity.club"));
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

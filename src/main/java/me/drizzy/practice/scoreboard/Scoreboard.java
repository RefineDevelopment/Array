package me.drizzy.practice.scoreboard;

import me.drizzy.practice.Array;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.hcf.HCFClasses;
import me.drizzy.practice.hcf.classes.Bard;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.tournament.Tournament;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.TimeUtil;
import me.drizzy.practice.util.scoreboard.scoreboard.Board;
import me.drizzy.practice.util.scoreboard.scoreboard.BoardAdapter;
import me.drizzy.practice.util.scoreboard.scoreboard.cooldown.BoardCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Drizzy
 * Created at 4/18/2021
 */

public class Scoreboard implements BoardAdapter {

    public final BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

    @Override
    public String getTitle(Player player) {
        return config.getStringOrDefault("SCOREBOARD.HEADER", CC.translate("&c&lPurge &7&l%splitter% &fTest Server")).replace("%splitter%", "┃").replace("|", "┃");
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (!profile.getSettings().isShowScoreboard()) {
            return null;
        }

        final List<String> lines = new ArrayList<>();
        lines.add(config.getStringOrDefault("SCOREOBARD.LINES", CC.SB_BAR));
        if (profile.isInLobby() || profile.isInQueue()) {

         config.getStringList("SCOREBOARD.LOBBY").forEach(line -> lines.add(CC.translate(line
                    .replace("<online>", String.valueOf(ArrayCache.getOnline()))
                    .replace("<in_fights>", String.valueOf(ArrayCache.getInFights()))
                    .replace("<in_queues>", String.valueOf(ArrayCache.getInQueues()))
                    .replace("%splitter%", "┃").replace("|", "┃").replace("|", "┃")
                    .replace("<elo_league>", ChatColor.stripColor(profile.getEloLeague()))
                    .replace("<global_elo>", String.valueOf(profile.getGlobalElo())))));

            if (profile.getParty() != null && Tournament.CURRENT_TOURNAMENT == null) {
                Party party = profile.getParty();

             config.getStringList("SCOREBOARD.PARTY").forEach(line -> lines.add(CC.translate(line
                        .replace("<party_leader>", party.getLeader().getPlayer().getName())
                        .replace("<party_size>", String.valueOf(party.getPlayers().size()))
                        .replace("<party_limit>", String.valueOf(party.getLimit()))
                        .replace("<party_privacy>", party.getPrivacy().toString())).replace("%splitter%", "┃").replace("|", "┃")));

            }
            if (profile.isInQueue()) {
                Queue queue = profile.getQueue();
                if (queue.getType() == QueueType.UNRANKED) {

                 config.getStringList("SCOREBOARD.UNRANKED_QUEUE").forEach(line -> lines.add(CC.translate(line
                            .replace("<queue_kit>", queue.getKit().getDisplayName())
                            .replace("<queue_duration>", queue.getDuration(player))
                            .replace("<queue_name>", queue.getQueueName())).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (queue.getType() == QueueType.RANKED) {

                 config.getStringList("SCOREBOARD.RANKED_QUEUE").forEach(line -> lines.add(CC.translate(line
                            .replace("<queue_kit>", queue.getKit().getDisplayName())
                            .replace("<queue_duration>", queue.getDuration(player))
                            .replace("<queue_range>", this.getEloRangeFormat(profile))
                            .replace("<queue_name>", queue.getQueueName())).replace("%splitter%", "┃").replace("|", "┃")));

                }
            } else if (Tournament.CURRENT_TOURNAMENT != null && profile.getParty() != null) {
                final Tournament tournament = Tournament.CURRENT_TOURNAMENT;
                final String round = (tournament.getRound() > 0) ? String.valueOf(tournament.getRound()) : "&fStarting";
                final String particpantType = (tournament.getTeamCount() > 1) ? "Parties" : "Players";

                 config.getStringList("SCOREBOARD.TOURNAMENT").forEach(line -> lines.add(CC.translate(line
                            .replace("<round>", round)
                            .replace("<kit>", tournament.getLadder().getName())
                            .replace("<team>", String.valueOf(tournament.getTeamCount()))
                            .replace("<participant_type>", particpantType)
                            .replace("<participant_count>", String.valueOf(tournament.getParticipants().size()))
                            .replace("<participant_size>", String.valueOf(50))).replace("%splitter%", "┃").replace("|", "┃")));

            }
        } else if (profile.isInFight()) {
            final Match match = profile.getMatch();
            if (match != null) {
                if (match.isEnding()) {

                    config.getStringList("SCOREBOARD.MATCH.ENDING").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_duration>", match.getDuration())
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isSoloMatch() && !match.isEnding()) {
                    final TeamPlayer self = match.getTeamPlayer(player);
                    final TeamPlayer opponent = match.getOpponentTeamPlayer(player);

                    config.getStringList("SCOREBOARD.MATCH.SOLO").forEach(line -> lines.add(CC.translate(line
                                .replace("<opponent_name>", opponent.getPlayer().getName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<your_name>", player.getName())
                                .replace("<match_type>", "Solo")).replace("%splitter%", "┃").replace("|", "┃")));

                    if (profile.getSettings().isPingScoreboard()) {

                        config.getStringList("SCOREBOARD.MATCH.PING_ADDITION").forEach(line -> lines.add(CC.translate(line
                                    .replace("<your_ping>", String.valueOf(self.getPing()))
                                    .replace("<opponent_ping>", String.valueOf(opponent.getPing()))).replace("%splitter%", "┃").replace("|", "┃")));
                    }
                    if (profile.getSettings().isCpsScoreboard()) {

                        config.getStringList("SCOREBOARD.MATCH.CPS_ADDITION").forEach(line -> lines.add(CC.translate(line
                                     .replace("<your_cps>", String.valueOf(self.getCps()))
                                     .replace("<opponent_cps>", String.valueOf(opponent.getCps()))).replace("%splitter%", "┃").replace("|", "┃")));

                    }
                } else if (match.isTheBridgeMatch() && !match.isEnding()) {
                    final TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;
                    TeamPlayer opponent = match.getOpponentTeamPlayer(player);
                    Profile opponentProfile = Profile.getByPlayer(opponent.getPlayer());

                    config.getStringList("SCOREBOARD.MATCH.BRIDGE").forEach(line -> lines.add(CC.translate(line
                                .replace("<opponent_name>", opponent.getPlayer().getName())
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<bridge_round>", String.valueOf(bridgeMatch.getRound()))
                                .replace("<match_type>", "Bridge")
                                .replace("<your_name>", player.getName())
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<opponent_points>", String.valueOf(opponentProfile.getBridgeRounds()))
                                .replace("<your_points>", String.valueOf(profile.getBridgeRounds()))
                                .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamPlayerA().getPlayer()) + (match.getTeamPlayerA().getPlayer() == player ? " &7(You)" : ""))
                                .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamPlayerB().getPlayer()) + (match.getTeamPlayerB().getPlayer() == player ? " &7(You)" : ""))).replace("%splitter%", "┃").replace("|", "┃")));

                    if (!profile.getBowCooldown().hasExpired()) {

                        config.getStringList("SCOREBOARD.MATCH.BRIDGE_BOW_COOLDOWN").forEach(line -> lines.add(CC.translate(line
                                .replace("<bow_cooldown>", profile.getBowCooldown().getTimeLeft() + "s")).replace("%splitter%", "┃").replace("|", "┃")));
                    }

                } else if (match.isTeamMatch() || match.isHCFMatch()) {
                    final Team team = match.getTeam(player);
                    final Team opponentTeam = match.getOpponentTeam(player);
                    if (match.isTeamMatch() && !match.isEnding()) {

                        config.getStringList("SCOREBOARD.MATCH.TEAM").forEach(line -> lines.add(CC.translate(line
                                    .replace("<match_duration>", match.getDuration())
                                    .replace("<match_kit>", match.getKit().getDisplayName())
                                    .replace("<match_type>", "Team")
                                    .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                    .replace("<your_name>", player.getName())
                                    .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                                    .replace("<opponent_team_alive>", String.valueOf(opponentTeam.getAliveCount()))
                                    .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                                    .replace("<opponent_team_count>", String.valueOf(opponentTeam.getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));
                    }

                    if (match.isHCFMatch() && !match.isEnding()) {
                        final HCFClasses pvpClass = Array.getInstance().getHCFManager().getEquippedClass(player);

                        config.getStringList("SCOREBOARD.MATCH.HCF").forEach(line -> lines.add(CC.translate(line
                                    .replace("<match_duration>", match.getDuration())
                                    .replace("<match_kit>", "HCF")
                                    .replace("<match_type>", "HCF")
                                    .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                    .replace("<your_name>", player.getName())
                                    .replace("<your_class>", pvpClass == null ? "None" : pvpClass.getName())
                                    .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                                    .replace("<opponent_team_alive>", String.valueOf(opponentTeam.getAliveCount()))
                                    .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                                    .replace("<opponent_team_count>", String.valueOf(opponentTeam.getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                        if (pvpClass instanceof Bard) {
                            final Bard bardClass = (Bard) pvpClass;

                            config.getStringList("SCOREBOARD.MATCH.HCF_BARD_ADDITION").forEach(line -> lines.add(CC.translate(line
                                       .replace("<your_bard_energy>", bardClass.getEnergy(player) + "E")).replace("%splitter%", "┃").replace("|", "┃")));
                        }
                    }
                } else if (match.isFreeForAllMatch() && !match.isEnding()) {
                    final Team team = match.getTeam(player);

                    config.getStringList("SCOREBOARD.MATCH.FFA").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_duration>", match.getDuration())
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_type>", "FFA")
                                .replace("<player_count>", String.valueOf(team.getPlayers().size()))
                                .replace("<your_name>", player.getName())
                                .replace("<players_alive>", String.valueOf(team.getAliveCount()))).replace("%splitter%", "┃").replace("|", "┃")));
                    
                }
            }
        } else if (profile.isSpectating()) {
            
            final Match match = profile.getMatch();
            final Sumo sumo = profile.getSumo();
            final LMS lms = profile.getLms();
            final Brackets brackets = profile.getBrackets();
            final Parkour parkour = profile.getParkour();
            final Spleef spleef = profile.getSpleef();
            final Gulag gulag = profile.getGulag();
            
            if (match != null) {
                if (match.isEnding()) {

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.ENDING").forEach(line -> lines.add(CC.translate(line
                            .replace("<match_duration>", match.getDuration())
                            .replace("<player_count>", String.valueOf(match.getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isSoloMatch() && !match.isEnding()) {
                    int playera = PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
                    int playerb = PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.SOLO").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                                .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                                .replace("<playerA_ping>", String.valueOf(playera))
                                .replace("<playerB_ping>", String.valueOf(playerb))
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<match_type>", "Solo")).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isTheBridgeMatch() && !match.isEnding()) {
                    int playera = PlayerUtil.getPing(match.getTeamPlayerA().getPlayer());
                    int playerb = PlayerUtil.getPing(match.getTeamPlayerB().getPlayer());
                    TheBridgeMatch bridgeMatch = (TheBridgeMatch) match;

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.BRIDGE").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                                .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                                .replace("<playerA_ping>", String.valueOf(playera))
                                .replace("<playerB_ping>", String.valueOf(playerb))
                                .replace("<bridge_round>", String.valueOf(bridgeMatch.getRound()))
                                .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamPlayerB().getPlayer()))
                                .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamPlayerA().getPlayer()))
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<match_type>", "Bridge")).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isTeamMatch() && !match.isEnding()) {

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.TEAM").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<match_type>", "Team")
                                .replace("<teamA_leader_name>", match.getTeamA().getLeader().getUsername())
                                .replace("<teamA_leader_name>", match.getTeamB().getLeader().getUsername())
                                .replace("<teamA_size>", String.valueOf(match.getTeamA().getPlayers().size()))
                                .replace("<teamB_size>", String.valueOf(match.getTeamB().getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isHCFMatch() && !match.isEnding()) {

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.HCF").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_kit>", "HCF")
                                .replace("<match_duration>", match.getDuration())
                                .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                                .replace("<match_type>", "HCF")
                                .replace("<teamA_leader_name>", match.getTeamA().getLeader().getUsername())
                                .replace("<teamA_leader_name>", match.getTeamB().getLeader().getUsername())
                                .replace("<teamA_size>", String.valueOf(match.getTeamA().getPlayers().size()))
                                .replace("<teamB_size>", String.valueOf(match.getTeamB().getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else if (match.isFreeForAllMatch() && !match.isEnding()) {
                    final Team team = match.getTeam(player);

                    config.getStringList("SCOREBOARD.SPECTATOR.MATCH.FFA").forEach(line -> lines.add(CC.translate(line
                                .replace("<match_kit>", match.getKit().getDisplayName())
                                .replace("<match_duration>", match.getDuration())
                                .replace("<match_type>", "FFA")
                                .replace("<alive_count>", String.valueOf(team.getAliveCount()))
                                .replace("<total_count>", String.valueOf(team.getPlayers().size()))).replace("%splitter%", "┃").replace("|", "┃")));

                }
            } else if (sumo != null) {
                if (sumo.isWaiting()) {

                    String status;
                    if (sumo.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_WAITING")
                                .replace("<sumo_host_name>", sumo.getName())
                                .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                                .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_COUNTING")
                                .replace("<sumo_host_name>", sumo.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                                .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.SUMO.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<sumo_host_name>", sumo.getName())
                            .replace("<status>", status)
                            .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                            .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.SUMO.STATUS_WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<sumo_host_name>", sumo.getName())
                            .replace("<sumo_duration>", sumo.getRoundDuration())
                            .replace("<sumo_players_alive>", String.valueOf(sumo.getRemainingPlayers().size()))
                            .replace("<sumo_playerA_name>", sumo.getRoundPlayerA().getUsername())
                            .replace("<sumo_playerA_ping>", String.valueOf(sumo.getRoundPlayerA().getPing()))
                            .replace("<sumo_playerB_name>", sumo.getRoundPlayerB().getUsername())
                            .replace("<sumo_playerB_ping>", String.valueOf(sumo.getRoundPlayerB().getPing()))
                            .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                            .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
                }
            } else if (gulag != null) {
                if (gulag.isWaiting()) {

                    String status;
                    if (gulag.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_WAITING")
                                .replace("<gulag_host_name>", gulag.getName())
                                .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                                .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(gulag.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_COUNTING")
                                .replace("<gulag_host_name>", gulag.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                                .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.GULAG.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<gulag_host_name>", gulag.getName())
                            .replace("<status>", status)
                            .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                            .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.GULAG.FIGHTING").forEach(line -> lines.add(CC.translate(line
                            .replace("<gulag_host_name>", gulag.getName())
                            .replace("<gulag_duration>", gulag.getRoundDuration())
                            .replace("<gulag_players_alive>", String.valueOf(gulag.getRemainingPlayers().size()))
                            .replace("<gulag_playerA_name>", gulag.getRoundPlayerA().getUsername())
                            .replace("<gulag_playerA_ping>", String.valueOf(gulag.getRoundPlayerA().getPing()))
                            .replace("<gulag_playerB_name>", gulag.getRoundPlayerB().getUsername())
                            .replace("<gulag_playerB_ping>", String.valueOf(gulag.getRoundPlayerB().getPing()))
                            .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                            .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
                }
            } else if (lms != null) {
                if (lms.isWaiting()) {

                    String status;
                    if (lms.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_WAITING")
                                .replace("<lms_host_name>", lms.getName())
                                .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                                .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(lms.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_COUNTING")
                                .replace("<lms_host_name>", lms.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                                .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.LMS.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<lms_host_name>", lms.getName())
                            .replace("<status>", status)
                            .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                            .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.LMS.FIGHTING").forEach(line -> lines.add(CC.translate(line
                            .replace("<lms_host_name>", lms.getName())
                            .replace("<lms_duration>", lms.getRoundDuration())
                            .replace("<lms_players_alive>", String.valueOf(lms.getRemainingPlayers().size()))
                            .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                            .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                }
            } else if (brackets != null) {
                if (brackets.isWaiting()) {

                    String status;
                    if (brackets.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_WAITING")
                                .replace("<brackets_host_name>", brackets.getName())
                                .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                                .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_COUNTING")
                                .replace("<brackets_host_name>", brackets.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                                .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.BRACKETS.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<brackets_host_name>", brackets.getName())
                            .replace("<status>", status)
                            .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                            .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.BRACKETS.FIGHTING").forEach(line -> lines.add(CC.translate(line
                            .replace("<brackets_host_name>", brackets.getName())
                            .replace("<brackets_duration>", brackets.getRoundDuration())
                            .replace("<brackets_players_alive>", String.valueOf(brackets.getRemainingPlayers().size()))
                            .replace("<brackets_playerA_name>", brackets.getRoundPlayerA().getUsername())
                            .replace("<brackets_playerA_ping>", String.valueOf(brackets.getRoundPlayerA().getPing()))
                            .replace("<brackets_playerB_name>", brackets.getRoundPlayerB().getUsername())
                            .replace("<brackets_playerB_ping>", String.valueOf(brackets.getRoundPlayerB().getPing()))
                            .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                            .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
                }
            } else if (parkour != null) {
                if (parkour.isWaiting()) {

                    String status;
                    if (parkour.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_WAITING")
                                .replace("<parkour_host_name>", parkour.getName())
                                .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                                .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_COUNTING")
                                .replace("<parkour_host_name>", parkour.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                                .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.PARKOUR.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<parkour_host_name>", parkour.getName())
                            .replace("<status>", status)
                            .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                            .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.PARKOUR.FIGHTING").forEach(line -> lines.add(CC.translate(line
                            .replace("<parkour_host_name>", parkour.getName())
                            .replace("<parkour_duration>", parkour.getRoundDuration())
                            .replace("<parkour_players_alive>", String.valueOf(parkour.getRemainingPlayers().size()))
                            .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                            .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                }
            } else if (spleef != null) {
                if (spleef.isWaiting()) {

                    String status;
                    if (spleef.getCooldown() == null) {

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_WAITING")
                                .replace("<spleef_host_name>", spleef.getName())
                                .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                                .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    } else {
                        String remaining=TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());
                        if (remaining.startsWith("-")) {
                            remaining="0.0";
                        }
                        String finalRemaining = remaining;

                        status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_COUNTING")
                                .replace("<spleef_host_name>", spleef.getName())
                                .replace("<remaining>", finalRemaining)
                                .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                                .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                    }

                    config.getStringList("SCOREBOARD.EVENT.SPLEEF.WAITING").forEach(line -> lines.add(CC.translate(line
                            .replace("<spleef_host_name>", spleef.getName())
                            .replace("<status>", status)
                            .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                            .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                } else {

                    config.getStringList("SCOREBOARD.EVENT.SPLEEF.FIGHTING").forEach(line -> lines.add(CC.translate(line
                            .replace("<spleef_host_name>", spleef.getName())
                            .replace("<spleef_duration>", spleef.getRoundDuration())
                            .replace("<spleef_players_alive>", String.valueOf(spleef.getRemainingPlayers().size()))
                            .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                            .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

                }
            }
        } else if (profile.isInSumo()) {
            final Sumo sumo = profile.getSumo();
            if (sumo.isWaiting()) {

                String status;
                if (sumo.getCooldown() == null) {

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_WAITING")
                            .replace("<sumo_host_name>", sumo.getName())
                            .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                            .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_COUNTING")
                            .replace("<sumo_host_name>", sumo.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                            .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.SUMO.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<sumo_host_name>", sumo.getName())
                        .replace("<status>", status)
                        .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                        .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.SUMO.STATUS_WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<sumo_host_name>", sumo.getName())
                        .replace("<sumo_duration>", sumo.getRoundDuration())
                        .replace("<sumo_players_alive>", String.valueOf(sumo.getRemainingPlayers().size()))
                        .replace("<sumo_playerA_name>", sumo.getRoundPlayerA().getUsername())
                        .replace("<sumo_playerA_ping>", String.valueOf(sumo.getRoundPlayerA().getPing()))
                        .replace("<sumo_playerB_name>", sumo.getRoundPlayerB().getUsername())
                        .replace("<sumo_playerB_ping>", String.valueOf(sumo.getRoundPlayerB().getPing()))
                        .replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
                        .replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
            }
        } else if (profile.isInGulag()) {
            final Gulag gulag = profile.getGulag();
            if (gulag.isWaiting()) {

                String status;
                if (gulag.getCooldown() == null) {

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_WAITING")
                            .replace("<gulag_host_name>", gulag.getName())
                            .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                            .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(gulag.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_COUNTING")
                            .replace("<gulag_host_name>", gulag.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                            .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.GULAG.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<gulag_host_name>", gulag.getName())
                        .replace("<status>", status)
                        .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                        .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.GULAG.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<gulag_host_name>", gulag.getName())
                        .replace("<gulag_duration>", gulag.getRoundDuration())
                        .replace("<gulag_players_alive>", String.valueOf(gulag.getRemainingPlayers().size()))
                        .replace("<gulag_playerA_name>", gulag.getRoundPlayerA().getUsername())
                        .replace("<gulag_playerA_ping>", String.valueOf(gulag.getRoundPlayerA().getPing()))
                        .replace("<gulag_playerB_name>", gulag.getRoundPlayerB().getUsername())
                        .replace("<gulag_playerB_ping>", String.valueOf(gulag.getRoundPlayerB().getPing()))
                        .replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
                        .replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
            }
        } else if (profile.isInBrackets()) {
            final Brackets brackets = profile.getBrackets();
            if (brackets.isWaiting()) {

                String status;
                if (brackets.getCooldown() == null) {

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_WAITING")
                            .replace("<brackets_host_name>", brackets.getName())
                            .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                            .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_COUNTING")
                            .replace("<brackets_host_name>", brackets.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                            .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.BRACKETS.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<brackets_host_name>", brackets.getName())
                        .replace("<status>", status)
                        .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                        .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.BRACKETS.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<brackets_host_name>", brackets.getName())
                        .replace("<brackets_duration>", brackets.getRoundDuration())
                        .replace("<brackets_players_alive>", String.valueOf(brackets.getRemainingPlayers().size()))
                        .replace("<brackets_playerA_name>", brackets.getRoundPlayerA().getUsername())
                        .replace("<brackets_playerA_ping>", String.valueOf(brackets.getRoundPlayerA().getPing()))
                        .replace("<brackets_playerB_name>", brackets.getRoundPlayerB().getUsername())
                        .replace("<brackets_playerB_ping>", String.valueOf(brackets.getRoundPlayerB().getPing()))
                        .replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
                        .replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
            }
        } else if (profile.isInLMS()) {
            final LMS lms = profile.getLms();
            if (lms.isWaiting()) {

                String status;
                if (lms.getCooldown() == null) {

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_WAITING")
                            .replace("<lms_host_name>", lms.getName())
                            .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                            .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(lms.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.LMS.STATUS_COUNTING")
                            .replace("<lms_host_name>", lms.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                            .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.LMS.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<lms_host_name>", lms.getName())
                        .replace("<status>", status)
                        .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                        .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.LMS.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<lms_host_name>", lms.getName())
                        .replace("<lms_duration>", lms.getRoundDuration())
                        .replace("<lms_players_alive>", String.valueOf(lms.getRemainingPlayers().size()))
                        .replace("<lms_player_count>", String.valueOf(lms.getEventPlayers().size()))
                        .replace("<lms_max_players>", String.valueOf(LMS.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            }
        } else if (profile.isInParkour()) {
            final Parkour parkour = profile.getParkour();
            if (parkour.isWaiting()) {

                String status;
                if (parkour.getCooldown() == null) {

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_WAITING")
                            .replace("<parkour_host_name>", parkour.getName())
                            .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                            .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_COUNTING")
                            .replace("<parkour_host_name>", parkour.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                            .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.PARKOUR.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<parkour_host_name>", parkour.getName())
                        .replace("<status>", status)
                        .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                        .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.PARKOUR.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<parkour_host_name>", parkour.getName())
                        .replace("<parkour_duration>", parkour.getRoundDuration())
                        .replace("<parkour_players_alive>", String.valueOf(parkour.getRemainingPlayers().size()))
                        .replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
                        .replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            }
        } else if (profile.isInSpleef()) {
            final Spleef spleef = profile.getSpleef();
            if (spleef.isWaiting()) {

                String status;
                if (spleef.getCooldown() == null) {

                  status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_WAITING")
                            .replace("<spleef_host_name>", spleef.getName())
                            .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                            .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                } else {
                    String remaining=TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) {
                        remaining="0.0";
                    }
                    String finalRemaining = remaining;

                    status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_COUNTING")
                            .replace("<spleef_host_name>", spleef.getName())
                            .replace("<remaining>", finalRemaining)
                            .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                            .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

                }

                config.getStringList("SCOREBOARD.EVENT.SPLEEF.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<spleef_host_name>", spleef.getName())
                        .replace("<status>", status)
                        .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                        .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            } else {

                config.getStringList("SCOREBOARD.EVENT.SPLEEF.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<spleef_host_name>", spleef.getName())
                        .replace("<spleef_duration>", spleef.getRoundDuration())
                        .replace("<spleef_players_alive>", String.valueOf(spleef.getRemainingPlayers().size()))
                        .replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
                        .replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

            }
        }
        lines.add("");
        lines.add(config.getStringOrDefault("SCOREBOARD.FOOTER", "&7&opurgecommunity.com"));
        lines.add(config.getStringOrDefault("SCOREOBARD.LINES", CC.SB_BAR));
        return lines;
    }


    @SuppressWarnings(value = "all")
    public String getFormattedPoints(Player player) {
        if( Profile.getByPlayer(player) != null) {
            Profile profile=Profile.getByPlayer(player);
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

    public String getEloRangeFormat(Profile profile) {
        return config.getStringOrDefault("SCOREBOARD.ELO_RANGE_FORMAT", "<min_range> -> <max_range>")
                .replace("<min_range>", String.valueOf(profile.getQueueProfile().getMinRange()))
                .replace("<max_range>", String.valueOf(profile.getQueueProfile().getMaxRange()));
    }


}

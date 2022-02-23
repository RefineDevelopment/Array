package xyz.refinedev.practice.adapters;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.managers.*;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.kit.BattleRushMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.divisions.ProfileDivision;
import xyz.refinedev.practice.pvpclasses.PvPClass;
import xyz.refinedev.practice.pvpclasses.classes.Bard;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueProfile;
import xyz.refinedev.practice.tournament.Tournament;
import xyz.refinedev.practice.tournament.TournamentState;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.other.TimeUtil;
import xyz.refinedev.practice.util.scoreboard.AssembleAdapter;
import xyz.refinedev.practice.util.timer.PlayerTimer;
import xyz.refinedev.practice.util.timer.TimerHandler;
import xyz.refinedev.practice.util.timer.impl.BridgeArrowTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/24/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ScoreboardAdapter implements AssembleAdapter {

    private final Array plugin;
    private final BasicConfigurationFile config;

    /**
     * Gets the scoreboard title.
     *
     * @param player who's title is being displayed.
     * @return title.
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("SCOREBOARD.TITLE")
                .replace("%splitter%", "┃")
                .replace("|", "┃");
    }

    /**
     * Gets the scoreboard lines.
     *
     * @param player who's lines are being displayed.
     * @return lines.
     */
    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        if (!profile.getSettings().isScoreboardEnabled()) return lines;

        lines.addAll(this.fetchLines(player, profile));

        if (this.plugin.isPlaceholderAPI()) {
            return lines.stream()
                    .map(line -> PlaceholderAPI.setPlaceholders(player, line))
                    .collect(Collectors.toList());
        }

        return lines.stream()
                .map(line -> line
                        .replace("%splitter%", "┃")
                        .replace("|", "┃"))
                .collect(Collectors.toList());
    }

    private List<String> fetchLines(Player player, Profile profile) {
        switch (profile.getState()) {
            case IN_LOBBY: {
                if (profile.isInTournament()) return this.getTournamentLines(player);
                if (profile.hasParty()) return this.getPartyLines(player);
                if (profile.hasClan()) return this.getClanLines(player);
                return this.getLobbyLines(player);
            }
            case IN_FIGHT: {
                if (profile.isInTournament()) return this.getTournamentLines(player);
                if (profile.isInMatch()) return this.getFightLines(player);
            }
            case IN_QUEUE: return this.getQueueLines(player);
            case IN_EVENT: return this.getEventLines(player);
            case SPECTATING: return this.getSpectateLines(player);
        }
        return new ArrayList<>();
    }

    private List<String> getLobbyLines(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        QueueManager queueManager = this.plugin.getQueueManager();
        MatchManager matchManager = this.plugin.getMatchManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        ProfileDivision division = profileManager.getDivision(profile);

        return config.getStringList("SCOREBOARD.LOBBY").stream().map(line -> CC.translate(line
                .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                .replace("<elo_league>", division.getDisplayName())
                .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))))
                .collect(Collectors.toList());
    }

    private List<String> getQueueLines(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        ClanManager clanManager = this.plugin.getClanManager();
        QueueManager queueManager = this.plugin.getQueueManager();
        MatchManager matchManager = this.plugin.getMatchManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Queue queue = queueManager.getByUUID(profile.getQueue());
        QueueProfile queueProfile = queueManager.getProfileByUUID(player.getUniqueId());
        ProfileDivision division = profileManager.getDivision(profile);

        switch (queue.getType()) {
            case UNRANKED: {
                return config.getStringList("SCOREBOARD.UNRANKED_QUEUE").stream().map(line -> CC.translate(line
                        .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                        .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                        .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                        .replace("<elo_league>", division.getDisplayName())
                        .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                        .replace("<queue_kit>", queue.getKit().getDisplayName())
                        .replace("<queue_name>", queue.getQueueName())
                        .replace("<queue_duration>", TimeUtil.millisToTimer(queueProfile.getPassed()))))
                        .collect(Collectors.toList());
            }
            case RANKED: {
                return config.getStringList("SCOREBOARD.RANKED_QUEUE").stream().map(line -> CC.translate(line
                        .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                        .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                        .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                        .replace("<elo_league>", division.getDisplayName())
                        .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                        .replace("<queue_kit>", queue.getKit().getDisplayName())
                        .replace("<queue_duration>", TimeUtil.millisToTimer(queueProfile.getPassed()))
                        .replace("<queue_range>", this.getEloRangeFormat(queueProfile))
                        .replace("<queue_name>", queue.getQueueName())))
                        .collect(Collectors.toList());
            }
            case CLAN: {
                Clan clan = clanManager.getByUUID(profile.getClan());
                Profile leaderProfile = profileManager.getProfileByUUID(clan.getLeader().getUniqueId());
                return config.getStringList("SCOREBOARD.CLAN_QUEUE").stream().map(line -> CC.translate(line
                        .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                        .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                        .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                        .replace("<elo_league>", division.getDisplayName())
                        .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                        .replace("<queue_kit>", queue.getKit().getDisplayName())
                        .replace("<queue_duration>", TimeUtil.millisToTimer(queueProfile.getPassed()))
                        .replace("<queue_range>", this.getEloRangeFormat(queueProfile))
                        .replace("<queue_name>", queue.getQueueName())
                        .replace("<clan_name>", clan.getName())
                        .replace("<clan_leader>", leaderProfile.getName())
                        .replace("<clan_members_online>", String.valueOf(clan.getOnlineMembers().size()))
                        .replace("<clan_members_total>", String.valueOf(clan.getAllMembers().size()))
                        .replace("<clan_elo>",String.valueOf(clan.getElo()))))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private List<String> getClanLines(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        ClanManager clanManager = this.plugin.getClanManager();
        QueueManager queueManager = this.plugin.getQueueManager();
        MatchManager matchManager = this.plugin.getMatchManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Clan clan = clanManager.getByUUID(profile.getClan());
        ProfileDivision division = profileManager.getDivision(profile);

        return config.getStringList("SCOREBOARD.CLAN").stream().map(line -> CC.translate(line
                .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                .replace("<elo_league>", division.getDisplayName())
                .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                .replace("<clan_leader>", plugin.getProfileManager().getProfileByUUID(clan.getLeader().getUniqueId()).getName())
                .replace("<clan_members_online>", String.valueOf(clan.getOnlineMembers().size()))
                .replace("<clan_members_total>", String.valueOf(clan.getAllMembers().size()))
                .replace("<clan_elo>",String.valueOf(clan.getElo()))
                .replace("<clan_name>", clan.getName())))
                .collect(Collectors.toList());
    }

    private List<String> getPartyLines(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        PartyManager partyManager = this.plugin.getPartyManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());
        String armorClass = party.getKits().get(player.getUniqueId());

        return config.getStringList("SCOREBOARD.PARTY").stream().map(line -> CC.translate(line
                .replace("<party_leader>", party.getLeader().getUsername())
                .replace("<party_size>", String.valueOf(party.getPlayers().size()))
                .replace("<party_limit>", String.valueOf(party.getLimit()))
                .replace("<party_privacy>", party.getPrivacy())
                .replace("<party_class>", armorClass == null ? "None" : armorClass)))
                .collect(Collectors.toList());
    }

    private List<String> getTournamentLines(Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        MatchManager matchManager = this.plugin.getMatchManager();
        TournamentManager tournamentManager = this.plugin.getTournamentManager();
        PartyManager partyManager = this.plugin.getPartyManager();
        QueueManager queueManager = this.plugin.getQueueManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Party party = partyManager.getPartyByUUID(profile.getParty());
        Tournament tournament = tournamentManager.getTournamentById(profile.getTournament());
        ProfileDivision division = profileManager.getDivision(profile);
        String armorClass = party.getKits().get(player.getUniqueId());
        String round = !tournament.getTournamentState().equals(TournamentState.STARTING) ? String.valueOf(tournament.getCurrentRound()) : "Starting";

        if (tournament.getTeamSize() > 1)
            return config.getStringList("SCOREBOARD.TOURNAMENT_PARTY").stream().map(line -> CC.translate(line
                    .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                    .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                    .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                    .replace("<elo_league>", division.getDisplayName())
                    .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                    .replace("<party_leader>", party.getLeader().getUsername())
                    .replace("<party_size>", String.valueOf(party.getPlayers().size()))
                    .replace("<party_limit>", String.valueOf(party.getLimit()))
                    .replace("<party_privacy>", party.getPrivacy())
                    .replace("<party_class>", armorClass == null ? "None" : armorClass)
                    .replace("<tournament_round>", round)
                    .replace("<tournament_kit>", tournament.getKitName())
                    .replace("<tournament_team_size>", String.valueOf(tournament.getTeamSize()))
                    .replace("<tournament_max_players>", String.valueOf(tournament.getSize()))
                    .replace("<tournament_players>", String.valueOf(tournament.getPlayers().size()))))
                    .collect(Collectors.toList());

        return config.getStringList("SCOREBOARD.TOURNAMENT").stream().map(line -> CC.translate(line
                .replace("<online>", String.valueOf(this.plugin.getServer().getOnlinePlayers().size()))
                .replace("<in_fights>", String.valueOf(matchManager.getInFights()))
                .replace("<in_queues>", String.valueOf(queueManager.getInQueues()))
                .replace("<elo_league>", division.getDisplayName())
                .replace("<global_elo>", String.valueOf(profile.getGlobalElo()))
                .replace("<tournament_round>", round)
                .replace("<tournament_kit>", tournament.getKitName())
                .replace("<tournament_team_size>", String.valueOf(tournament.getTeamSize()))
                .replace("<tournament_max_players>", String.valueOf(tournament.getSize()))
                .replace("<tournament_players>", String.valueOf(tournament.getPlayers().size()))))
                .collect(Collectors.toList());
    }

    private List<String> getFightLines(Player player) {
        List<String> lines = new ArrayList<>();
        ProfileManager profileManager = this.plugin.getProfileManager();
        TimerHandler timerHandler = this.plugin.getTimerHandler();

        PlayerTimer bridgeTimer = timerHandler.getTimer(BridgeArrowTimer.class);
        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (match.isEnding()) {
            config.getStringList("SCOREBOARD.MATCH.ENDING").forEach(line -> lines.add(CC.translate(line
                    .replace("<match_duration>", match.getDuration())
                    .replace("<player_count>", String.valueOf(match.getPlayers().size())))));
        }

        if (match.isSoloMatch()) {
            TeamPlayer self = match.getTeamPlayer(player);
            TeamPlayer opponent = match.getOpponentTeamPlayer(player);

            if (match.isBattleRushMatch()) {
                for ( String line : config.getStringList("MATCH.SOLO_BATTLERUSH") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_type>", "BattleRush"))
                            .replace("<your_ping>", String.valueOf(self.getPing()))
                            .replace("<your_cps>", String.valueOf(self.getCps()))
                            .replace("<your_kills>", String.valueOf(self.getKills()))
                            .replace("<your_potions_thrown>", String.valueOf(self.getPotionsThrown()))
                            .replace("<your_potions_missed>", String.valueOf(self.getPotionsMissed()))
                            .replace("<your_hits>", String.valueOf(self.getHits()))
                            .replace("<your_name>", self.getUsername())
                            .replace("<opponent_potions_thrown>", String.valueOf(opponent.getPotionsThrown()))
                            .replace("<opponent_potions_missed>", String.valueOf(opponent.getPotionsMissed()))
                            .replace("<opponent_kills>", String.valueOf(opponent.getKills()))
                            .replace("<opponent_ping>", String.valueOf(opponent.getPing()))
                            .replace("<opponent_cps>", String.valueOf(opponent.getCps()))
                            .replace("<opponent_name>", opponent.getUsername())
                            .replace("<opponent_hits>", String.valueOf(opponent.getHits()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size())));
                }
            } else if (match.isBoxingMatch()) {
                String hits = this.getHitDifference(self.getHits(), opponent.getHits());

                for ( String line : config.getStringList("MATCH.SOLO_BOXING") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_type>", "Boxing"))
                            .replace("<your_ping>", String.valueOf(self.getPing()))
                            .replace("<your_cps>", String.valueOf(self.getCps()))
                            .replace("<your_kills>", String.valueOf(self.getKills()))
                            .replace("<your_potions_thrown>", String.valueOf(self.getPotionsThrown()))
                            .replace("<your_potions_missed>", String.valueOf(self.getPotionsMissed()))
                            .replace("<your_hits>", String.valueOf(self.getHits()))
                            .replace("<your_name>", self.getUsername())
                            .replace("<opponent_potions_thrown>", String.valueOf(opponent.getPotionsThrown()))
                            .replace("<opponent_potions_missed>", String.valueOf(opponent.getPotionsMissed()))
                            .replace("<opponent_kills>", String.valueOf(opponent.getKills()))
                            .replace("<opponent_ping>", String.valueOf(opponent.getPing()))
                            .replace("<opponent_cps>", String.valueOf(opponent.getCps()))
                            .replace("<opponent_name>", opponent.getUsername())
                            .replace("<opponent_hits>", String.valueOf(opponent.getHits()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                            .replace("<hit_difference>", hits));
                }
            } else if (match.isTheBridgeMatch()) {
                SoloBridgeMatch soloBridgeMatch = (SoloBridgeMatch) match;

                int yourPoints = match.getTeamPlayerA().equals(self) ? soloBridgeMatch.getPlayerAPoints() : soloBridgeMatch.getPlayerBPoints();
                int opponentPoints = match.getTeamPlayerA().equals(opponent) ? soloBridgeMatch.getPlayerAPoints() : soloBridgeMatch.getPlayerBPoints();

                for ( String line : config.getStringList("MATCH.SOLO_BRIDGE") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_type>", "Bridges"))
                            .replace("<your_ping>", String.valueOf(self.getPing()))
                            .replace("<your_cps>", String.valueOf(self.getCps()))
                            .replace("<your_kills>", String.valueOf(self.getKills()))
                            .replace("<your_potions_thrown>", String.valueOf(self.getPotionsThrown()))
                            .replace("<your_potions_missed>", String.valueOf(self.getPotionsMissed()))
                            .replace("<your_hits>", String.valueOf(self.getHits()))
                            .replace("<your_name>", self.getUsername())
                            .replace("<your_points>", String.valueOf(yourPoints))
                            .replace("<opponent_potions_thrown>", String.valueOf(opponent.getPotionsThrown()))
                            .replace("<opponent_potions_missed>", String.valueOf(opponent.getPotionsMissed()))
                            .replace("<opponent_kills>", String.valueOf(opponent.getKills()))
                            .replace("<opponent_ping>", String.valueOf(opponent.getPing()))
                            .replace("<opponent_cps>", String.valueOf(opponent.getCps()))
                            .replace("<opponent_name>", opponent.getUsername())
                            .replace("<opponent_hits>", String.valueOf(opponent.getHits()))
                            .replace("<opponent_points>", String.valueOf(opponentPoints))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                            .replace("<bow_cooldown>", TimeUtil.millisToTimer(bridgeTimer.getRemaining(player)))
                            .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamPlayerA().getPlayer()) + (match.getTeamPlayerA().getPlayer() == player ? " &7(You)" : ""))
                            .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamPlayerB().getPlayer()) + (match.getTeamPlayerB().getPlayer() == player ? " &7(You)" : "")));
                }
            } else {
                for ( String line : config.getStringList("SCOREBOARD.MATCH.SOLO") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_type>", "Solo"))
                            .replace("<your_ping>", String.valueOf(self.getPing()))
                            .replace("<your_cps>", String.valueOf(self.getCps()))
                            .replace("<your_kills>", String.valueOf(self.getKills()))
                            .replace("<your_potions_thrown>", String.valueOf(self.getPotionsThrown()))
                            .replace("<your_potions_missed>", String.valueOf(self.getPotionsMissed()))
                            .replace("<your_hits>", String.valueOf(self.getHits()))
                            .replace("<your_name>", self.getUsername())
                            .replace("<opponent_potions_thrown>", String.valueOf(opponent.getPotionsThrown()))
                            .replace("<opponent_potions_missed>", String.valueOf(opponent.getPotionsMissed()))
                            .replace("<opponent_kills>", String.valueOf(opponent.getKills()))
                            .replace("<opponent_ping>", String.valueOf(opponent.getPing()))
                            .replace("<opponent_cps>", String.valueOf(opponent.getCps()))
                            .replace("<opponent_name>", opponent.getUsername())
                            .replace("<opponent_hits>", String.valueOf(opponent.getHits()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                            .replace("|", "┃"));
                }
            }
        } else if (match.isTeamMatch()) {
            Team team = match.getTeam(player);
            Team opponentTeam = match.getOpponentTeam(player);

            if (match.isTheBridgeMatch()) {
                TeamBridgeMatch teamBridgeMatch = (TeamBridgeMatch) match;

                int yourPoints = match.getTeamA().equals(team) ? teamBridgeMatch.getTeamAPoints() : teamBridgeMatch.getTeamBPoints();
                int opponentPoints = match.getTeamB().equals(opponentTeam) ? teamBridgeMatch.getTeamAPoints() : teamBridgeMatch.getTeamBPoints();

                config.getStringList("MATCH.TEAM_BRIDGE").forEach(line -> lines.add(CC.translate(line
                        .replace("<opponent_points>", String.valueOf(opponentPoints))
                        .replace("<your_points>", String.valueOf(yourPoints))
                        .replace("<match_round>", String.valueOf(teamBridgeMatch.getRound()))
                        .replace("<bow_cooldown>", TimeUtil.millisToTimer(bridgeTimer.getRemaining(player)))
                        .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamA().getLeader().getPlayer()) + (match.getTeamA().containsPlayer(player) ? " &7(You)" : ""))
                        .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamB().getLeader().getPlayer()) + (match.getTeamB().containsPlayer(player) ? " &7(You)" : ""))
                        .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                        .replace("<match_type>", "Bridge"))
                ));

            } else if (match.isHCFMatch()) {
                PvPClass pvpClass = plugin.getPvpClassManager().getEquippedClass(player);

                config.getStringList("SCOREBOARD.MATCH.TEAM_HCF").forEach(line -> lines.add(CC.translate(line
                        .replace("<match_duration>", match.getDuration())
                        .replace("<match_kit>", "HCFTeamFight")
                        .replace("<match_arena>", match.getArena().getDisplayName())
                        .replace("<match_type>", "HCF")
                        .replace("<your_name>", player.getName())
                        .replace("<your_class>", pvpClass == null ? "None" : pvpClass.getName())
                        .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                        .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                        .replace("<opponent_team_alive>", String.valueOf(opponentTeam.getAliveCount()))
                        .replace("<opponent_team_count>", String.valueOf(opponentTeam.getPlayers().size())))
                        .replace("<player_count>", String.valueOf(match.getPlayers().size()))));

                if (pvpClass instanceof Bard) {
                    Bard bardClass = (Bard) pvpClass;
                    config.getStringList("SCOREBOARD.MATCH.HCF_BARD_ADDITION").forEach(line -> lines.add(CC.translate(line
                            .replace( "<your_bard_energy>", String.valueOf(bardClass.getEnergy(player))))));
                }
            } else {
                config.getStringList("SCOREBOARD.MATCH.TEAM").forEach(line -> lines.add(CC.translate(line
                        .replace("<match_duration>", match.getDuration())
                        .replace("<match_kit>", match.getKit().getDisplayName())
                        .replace("<match_type>", "Team")
                        .replace("<match_arena>", match.getArena().getDisplayName())
                        .replace("<your_name>", player.getName())
                        .replace("<your_team_alive>", String.valueOf(team.getAliveCount()))
                        .replace("<your_team_count>", String.valueOf(team.getPlayers().size()))
                        .replace("<opponent_team_alive>", String.valueOf(opponentTeam.getAliveCount()))
                        .replace("<opponent_team_count>", String.valueOf(opponentTeam.getPlayers().size())))
                        .replace("<player_count>", String.valueOf(match.getPlayers().size()))));
            }
        } else if (match.isFreeForAllMatch()) {
            Team team = match.getTeam(player);

            config.getStringList("SCOREBOARD.MATCH.FFA").forEach(line -> lines.add(CC.translate(line
                    .replace("<match_duration>", match.getDuration())
                    .replace("<match_kit>", match.getKit().getDisplayName())
                    .replace("<match_arena>", match.getArena().getDisplayName())
                    .replace("<match_type>", "FFA")
                    .replace("<your_name>", player.getName())
                    .replace("<player_count>", String.valueOf(team.getPlayers().size()))
                    .replace("<players_alive>", String.valueOf(team.getAliveCount())))
                    ));
        }

        return lines;
    }

    private List<String> getSpectateLines(Player player) {
        List<String> lines = new ArrayList<>();
        ProfileManager profileManager = this.plugin.getProfileManager();
        TimerHandler timerHandler = this.plugin.getTimerHandler();

        PlayerTimer timer = timerHandler.getTimer(BridgeArrowTimer.class);
        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (match.isEnding()) {
            config.getStringList("SCOREBOARD.SPECTATOR.MATCH.ENDING").forEach(line -> lines.add(CC.translate(line
                    .replace("<match_duration>", match.getDuration())
                    .replace("<player_count>", String.valueOf(match.getPlayers().size())))));
        }

        if (match.isSoloMatch()) {
            TeamPlayer playerA = match.getTeamPlayerA();
            TeamPlayer playerB = match.getTeamPlayerB();

            if (match.isBattleRushMatch()) {
                BattleRushMatch battleRushMatch = (BattleRushMatch) match;

                for ( String line : config.getStringList("SCOREBOARD.SPECTATOR.MATCH.SOLO_BATTLERUSH") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_round>", String.valueOf(battleRushMatch.getRound()))
                            .replace("<match_type>", "BattleRush"))
                            .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                            .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                            .replace("<playerA_ping>", String.valueOf(playerA))
                            .replace("<playerB_ping>", String.valueOf(playerB))
                            .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamPlayerB().getPlayer()))
                            .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamPlayerA().getPlayer()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size())));
                }
            } else if (match.isBoxingMatch()) {
                for ( String line : config.getStringList("SCOREBOARD.SPECTATOR.MATCH.SOLO_BOXING") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_type>", "Boxing"))
                            .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                            .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                            .replace("<playerA_ping>", String.valueOf(playerA))
                            .replace("<playerB_ping>", String.valueOf(playerB))
                            .replace("<playerA_hits>", String.valueOf(match.getTeamPlayerA().getHits()))
                            .replace("<playerB_hits>", String.valueOf(match.getTeamPlayerB().getHits()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size())));
                }
            } else if (match.isTheBridgeMatch()) {
                SoloBridgeMatch soloBridgeMatch = (SoloBridgeMatch) match;

                for ( String line : config.getStringList("SCOREBOARD.SPECTATOR.MATCH.SOLO_BRIDGE") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<match_round>", String.valueOf(soloBridgeMatch.getRound()))
                            .replace("<match_type>", "Bridges"))
                            .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                            .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                            .replace("<playerA_ping>", String.valueOf(playerA))
                            .replace("<playerB_ping>", String.valueOf(playerB))
                            .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamPlayerB().getPlayer()))
                            .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamPlayerA().getPlayer()))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size())));
                }
            } else {
                for ( String line : config.getStringList("SCOREBOARD.SPECTATOR.MATCH.SOLO") ) {
                    if (line.contains("cps>") && !profile.getSettings().isCpsScoreboard() && plugin.getConfigHandler().isCPS_SCOREBOARD_SETTING()) continue;
                    if (line.contains("ping>") && !profile.getSettings().isPingScoreboard() && plugin.getConfigHandler().isPING_SCOREBOARD_SETTING()) continue;

                    lines.add(CC.translate(line
                            .replace("<match_kit>", match.getKit().getDisplayName())
                            .replace("<match_duration>", match.getDuration())
                            .replace("<match_arena>", match.getArena().getDisplayName())
                            .replace("<playerA_name>", match.getTeamPlayerA().getUsername())
                            .replace("<playerB_name>", match.getTeamPlayerB().getUsername())
                            .replace("<playerA_ping>", String.valueOf(playerA))
                            .replace("<playerB_ping>", String.valueOf(playerB))
                            .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                            .replace("<match_type>", "Solo")));
                }
            }
        } else if (match.isTeamMatch()) {
            if (match instanceof TeamBridgeMatch) {
                TeamBridgeMatch teamBridgeMatch = (TeamBridgeMatch) match;

                int teamAPoints = teamBridgeMatch.getTeamAPoints();
                int teamBPoints = teamBridgeMatch.getTeamBPoints();

                config.getStringList("SCOREBOARD.SPECTATOR.MATCH.TEAM_BRIDGE").forEach(line -> lines.add(CC.translate(line
                        .replace("<teamA_points>", String.valueOf(teamAPoints))
                        .replace("<teamB_points>", String.valueOf(teamBPoints))
                        .replace("<match_round>", String.valueOf(teamBridgeMatch.getRound()))
                        .replace("<bow_cooldown>", TimeUtil.millisToTimer(timer.getRemaining(player)))
                        .replace("<red_points_formatted>", this.getFormattedPoints(match.getTeamA().getLeader().getPlayer()))
                        .replace("<blue_points_formatted>", this.getFormattedPoints(match.getTeamB().getLeader().getPlayer()))
                        .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                        .replace("<match_type>", "Bridge"))
                ));

            } else {
                config.getStringList("SCOREBOARD.SPECTATOR.MATCH.TEAM").forEach(line -> lines.add(CC.translate(line
                        .replace("<match_kit>", match.getKit().getDisplayName())
                        .replace("<match_duration>", match.getDuration())
                        .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                        .replace("<match_type>", "Team")
                        .replace("<match_arena>", match.getArena().getDisplayName())
                        .replace("<teamA_leader_name>", match.getTeamA().getLeader().getUsername())
                        .replace("<teamB_leader_name>", match.getTeamB().getLeader().getUsername())
                        .replace("<teamA_size>", String.valueOf(match.getTeamA().getPlayers().size()))
                        .replace("<teamB_size>", String.valueOf(match.getTeamB().getPlayers().size())))));
            }
        } else if (match.isHCFMatch()) {
            config.getStringList("SCOREBOARD.SPECTATOR.MATCH.HCF").forEach(line -> lines.add(CC.translate(line
                    .replace("<match_kit>", "HCF")
                    .replace("<match_duration>", match.getDuration())
                    .replace("<player_count>", String.valueOf(match.getPlayers().size()))
                    .replace("<match_type>", "HCF")
                    .replace("<match_arena>", match.getArena().getDisplayName())
                    .replace("<teamA_leader_name>", match.getTeamA().getLeader().getUsername())
                    .replace("<teamB_leader_name>", match.getTeamB().getLeader().getUsername())
                    .replace("<teamA_size>", String.valueOf(match.getTeamA().getPlayers().size()))
                    .replace("<teamB_size>", String.valueOf(match.getTeamB().getPlayers().size())))));
        } else if (match.isFreeForAllMatch()) {
            final Team team = match.getTeam(player);

            config.getStringList("SCOREBOARD.SPECTATOR.MATCH.FFA").forEach(line -> lines.add(CC.translate(line
                    .replace("<match_kit>", match.getKit().getDisplayName())
                    .replace("<match_duration>", match.getDuration())
                    .replace("<match_type>", "FFA")
                    .replace("<match_arena>", match.getArena().getDisplayName())
                    .replace("<alive_count>", String.valueOf(team.getAliveCount()))
                    .replace("<total_count>", String.valueOf(team.getPlayers().size())))));
        }

        return lines;
    }

    private List<String> getEventLines(Player player) {
        List<String> lines = new ArrayList<>();

        /*ProfileManager profileManager = this.plugin.getProfileManager();
        EventManager eventManager = this.plugin.getEventManager();

        Profile profile = profileManager.getProfileByUUID(player.getUniqueId());
        Event event = eventManager.getEventByUUID(profile.getEvent());

        if (!event.isTeam()) {
            if (event.isWaiting()) {
                String status;
                if (event.getCooldown() == null) {
                    status = CC.translate(config.getString("SCOREBOARD.EVENT.SOLO.STATUS_WAITING")
                            .replace("<event_name>", event.getName())
                            .replace("<event_host_name>", event.getHost())
                            .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())));

                } else {
                    String remaining = TimeUtil.millisToSeconds(event.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) remaining = "0.0";

                    String finalRemaining=remaining;
                    status = CC.translate(config.getString("SCOREBOARD.EVENT.SOLO.STATUS_COUNTING")
                            .replace("<event_host_name>", event.getHost())
                            .replace("<event_name>", event.getName())
                            .replace("<event_remaining>", finalRemaining)
                            .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())));

                }
                config.getStringList("SCOREBOARD.EVENT.SOLO.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<event_host_name>", event.getHost())
                        .replace("<event_name>", event.getName())
                        .replace("<event_status>", status)
                        .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                        .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))));

            } else {
                config.getStringList("SCOREBOARD.EVENT.SOLO.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<event_host_name>", event.getName())
                        .replace("<event_name>", event.getName())
                        .replace("<event_duration>", event.getDuration())
                        .replace("<event_players_alive>", String.valueOf(event.getRemainingPlayers().size()))
                        .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                        .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))));

                if (!event.isFreeForAll()) {
                    config.getStringList("SCOREBOARD.EVENT.SOLO_ROUND_ADDITION").forEach(line -> lines.add(CC.translate(line
                            .replace("<event_playerA_name>", event.getRoundPlayerA().getUsername())
                            .replace("<event_playerA_ping>", String.valueOf(event.getRoundPlayerA().getPing()))
                            .replace("<event_playerB_name>", event.getRoundPlayerB().getUsername())
                            .replace("<event_playerB_ping>", String.valueOf(event.getRoundPlayerB().getPing()))
                            .replace("<event_host_name>", event.getName())
                            .replace("<event_name>", event.getName())
                            .replace("<event_duration>", event.getDuration())
                            .replace("<event_players_alive>", String.valueOf(event.getRemainingPlayers().size()))
                            .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))));
                }
            }
        } else {
            if (event.isWaiting()) {
                String status;
                if (event.getCooldown() == null) {
                    status = CC.translate(config.getString("SCOREBOARD.EVENT.TEAM.STATUS_WAITING")
                            .replace("<event_name>", event.getName())
                            .replace("<event_host_name>", event.getHost())
                            .replace("<event_player_count>", String.valueOf(event.getEventTeamPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())));

                } else {
                    String remaining = TimeUtil.millisToSeconds(event.getCooldown().getRemaining());
                    if (remaining.startsWith("-")) remaining = "0.0";

                    String finalRemaining=remaining;
                    status = CC.translate(config.getString("SCOREBOARD.EVENT.TEAM.STATUS_COUNTING")
                            .replace("<event_host_name>", event.getHost())
                            .replace("<event_name>", event.getName())
                            .replace("<event_remaining>", finalRemaining)
                            .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())));

                }
                config.getStringList("SCOREBOARD.EVENT.TEAM.WAITING").forEach(line -> lines.add(CC.translate(line
                        .replace("<event_host_name>", event.getHost())
                        .replace("<event_name>", event.getName())
                        .replace("<event_status>", status)
                        .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                        .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))));

            } else {
                config.getStringList("SCOREBOARD.EVENT.TEAM.FIGHTING").forEach(line -> lines.add(CC.translate(line
                        .replace("<event_host_name>", event.getHost())
                        .replace("<event_duration>", event.getDuration())
                        .replace("<event_name>", event.getName())
                        .replace("<event_players_alive>", String.valueOf(event.getRemainingPlayers().size()))
                        .replace("<event_player_count>", String.valueOf(event.getEventPlayers().size()))
                        .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))));

                if (!event.isFreeForAll()) {
                    config.getStringList("SCOREBOARD.EVENT.TEAM_ROUND_ADDITION").forEach(line -> lines.add(CC.translate(line
                            .replace("<event_teamA_name>", event.getRoundTeamA().getColor().getTitle())
                            .replace("<event_teamB_name>", event.getRoundTeamB().getColor().getTitle())
                            .replace("<event_teamA_size>", String.valueOf(event.getRoundTeamA().getPlayers().size()))
                            .replace("<event_teamB_size>", String.valueOf(event.getRoundTeamB().getPlayers().size()))
                            .replace("<event_players_alive>", String.valueOf(event.getRemainingPlayers().size()))
                            .replace("<event_player_count>", String.valueOf(event.getEventTeamPlayers().size()))
                            .replace("<event_max_players>", String.valueOf(event.getMaxPlayers())))
                            .replace("<event_host_name>", event.getHost())
                            .replace("<event_name>", event.getName())
                            .replace("<event_duration>", event.getDuration())));
                }
            }
        }*/

        return lines;
    }

    private String getFormattedPoints(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        Match match = profile.getMatch();
        int points = 0;
        String color = "&a";

        if (match instanceof SoloBridgeMatch) {
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            SoloBridgeMatch soloBridgeMatch = (SoloBridgeMatch) match;
            points = match.getTeamPlayerA().equals(teamPlayer) ? soloBridgeMatch.getPlayerAPoints() : soloBridgeMatch.getPlayerBPoints();
            color = match.getTeamPlayerA().equals(teamPlayer) ? "&c" : "&9";
        } else if (match instanceof TeamBridgeMatch) {
            Team team = match.getTeam(player);
            TeamBridgeMatch teamBridgeMatch = (TeamBridgeMatch) match;
            points = match.getTeamA().equals(team) ? teamBridgeMatch.getTeamAPoints() : teamBridgeMatch.getTeamBPoints();
            color = match.getTeamA().equals(team) ? "&c" : "&9";
        } else if (match.isBattleRushMatch()) {
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            BattleRushMatch battleRushMatch = (BattleRushMatch) match;
            points = match.getTeamPlayerA().equals(teamPlayer) ? battleRushMatch.getPlayerAPoints() : battleRushMatch.getPlayerBPoints();
            color = match.getTeamPlayerA().equals(teamPlayer) ? "&c" : "&9";
        }

        switch (points) {
            case 3:
                return CC.translate(color + "\u2b24" + color + "\u2b24" + color + "\u2b24");
            case 2:
                return CC.translate(color + "\u2b24" + color + "\u2b24&7\u2b24");
            case 1:
                return CC.translate(color + "\u2b24&7\u2b24\u2b24");
        }
        return CC.translate("&7\u2b24\u2b24\u2b24");
    }

    private String getEloRangeFormat(QueueProfile profile) {
        return config.getStringOrDefault("SCOREBOARD.ELO_RANGE_FORMAT", "<min_range> -> <max_range>")
                .replace("<min_range>", String.valueOf(profile.getMinRange()))
                .replace("<max_range>", String.valueOf(profile.getMaxRange()));
    }

    private String getHitDifference(int own, int opp) {
        String hits = "&e(" + (opp - own) + ")";

        if (own < opp) {
            hits = "&c(-" + (opp - own) + ")";
        } else if (own > opp) {
            hits = "&a(+" + (opp - own) + ")";
        }
        return hits;
    }
}

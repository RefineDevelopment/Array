package xyz.refinedev.practice.events.impl.sumo.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.EventTeamSize;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.impl.sumo.team.task.SumoTeamRoundEndTask;
import xyz.refinedev.practice.events.impl.sumo.team.task.SumoTeamRoundStartTask;
import xyz.refinedev.practice.events.impl.sumo.team.task.SumoTeamStartTask;
import xyz.refinedev.practice.events.meta.group.EventGroup;
import xyz.refinedev.practice.events.meta.group.EventGroupColor;
import xyz.refinedev.practice.events.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayerState;
import xyz.refinedev.practice.events.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerSnapshot;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

@Getter
@Setter
public class SumoTeam extends Event {

    public final List<EventGroup> teams = new ArrayList<>();

    private BukkitRunnable waterTask;

    private EventGroup roundTeamA;
    private EventGroup roundTeamB;

    public SumoTeam(Player host, EventTeamSize size) {
        super("Sumo", new PlayerSnapshot(host.getUniqueId(), host.getName()), size.getMaxParticipants(), EventType.SUMO_TEAM);

        for ( int i = 0; i <= size.getTeams(); i++ ) {
            EventGroupColor color = EventGroupColor.values()[i];
            EventGroup eventGroup = new EventGroup(size.getMaxTeamPlayers(), color);
            this.teams.add(eventGroup);
        }

        this.setEvent_Prefix(Locale.EVENT_PREFIX.toString().replace("<event_name>", this.getName()));
    }

    @Override
    public boolean isFreeForAll() {
        return false;
    }

    @Override
    public boolean isTeam() {
        return true;
    }

    @Override
    public void handleJoin(Player player) {
        this.getEventTeamPlayers().put(player.getUniqueId(), new EventTeamPlayer(player));

        this.broadcastMessage(Locale.EVENT_JOIN.toString()
                .replace("<event_name>", this.getName())
                .replace("<joined>", player.getName())
                .replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
                .replace("<event_max_players>", String.valueOf(getMaxPlayers())));

        player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", this.getName()));

        this.onJoin(player);

        player.teleport(this.getEventManager().getSpectator(this));

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setEvent(this);
        profile.setState(ProfileState.IN_EVENT);
        profile.refreshHotbar();

        TaskUtil.runAsync(() -> {
            for (Player otherPlayer : getPlayers()) {
                Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                otherProfile.handleVisibility(otherPlayer, player);
                profile.handleVisibility(player, otherPlayer);

                this.getPlugin().getNameTagHandler().reloadPlayer(player);
                this.getPlugin().getNameTagHandler().reloadOthersFor(player);
            }
        });
    }

    @Override
    public void handleLeave(Player player) {
        if (this.isFighting(this.getTeamByPlayer(player))) {
            this.handleDeath(player);
        }

        EventTeamPlayer loser = (EventTeamPlayer) this.getEventPlayer(player.getUniqueId());
        loser.setState(EventPlayerState.ELIMINATED);

        if (loser.getGroup() != null && loser.getGroup().getAlivePlayers() == 0) {
            loser.getGroup().setState(EventPlayerState.ELIMINATED);
        }

        this.getEventTeamPlayers().remove(player.getUniqueId());
        this.onLeave(player);

        Profile profile = Profile.getByPlayer(player);

        TaskUtil.runAsync(() -> {
            for (Player otherPlayer : getPlayers()) {
                Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                otherProfile.handleVisibility(otherPlayer, player);
                profile.handleVisibility(player, otherPlayer);

                this.getPlugin().getNameTagHandler().reloadPlayer(player);
                this.getPlugin().getNameTagHandler().reloadOthersFor(player);
            }
        });

        if (getState() == EventState.WAITING) {
            broadcastMessage(Locale.EVENT_LEAVE.toString()
                    .replace("<event_name>", this.getName())
                    .replace("<left>", player.getName())
                    .replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
                    .replace("<event_max_players>", String.valueOf(getMaxPlayers())));
        }

        player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", this.getName()));

        profile.setState(ProfileState.IN_LOBBY);
        profile.setEvent(null);
        profile.refreshHotbar();
        profile.teleportToSpawn();
    }

    @Override
    public void onJoin(Player player) {
        this.getPlugin().getKnockbackManager().knockback(player, this.getEventManager().getSumoKB());
    }

    @Override
    public void onLeave(Player player) {
        this.getPlugin().getKnockbackManager().resetKnockback(player);
    }

    @Override
    public void onRound() {
        List<EventTeamPlayer> noTeamPlayers = this.getEventTeamPlayers().values().stream().filter(eventPlayer -> eventPlayer.getGroup() == null).collect(Collectors.toList());
        
        for (EventTeamPlayer teamPlayer : noTeamPlayers) {
            this.getAvailableTeams().addPlayer(teamPlayer);
        }
        
        this.getTeams().removeAll(this.getTeams().stream().filter(team -> team.getPlayers().size() == 0).collect(Collectors.toList()));
        this.getEventPlayers().values().stream().filter(eventPlayer -> eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).forEach(eventPlayer -> Profile.getByUuid(eventPlayer.getUuid()).refreshHotbar());
        
        this.setState(EventState.ROUND_STARTING);

        //Reset Previous Team A
        if (this.roundTeamA != null) {
            for (Player player : this.roundTeamA.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                if (player != null) {
                    player.teleport(this.getEventManager().getSpectator(this));
                    
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    if (!profile.isInEvent() || (profile.isInEvent() && !profile.getEvent().isSumoTeam())) continue;
                    profile.refreshHotbar();
                }
            }
            
            this.roundTeamA = null;
        }
        
        //Reset Previous Team B
        if (this.roundTeamB != null) {
            for (Player player : this.roundTeamB.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                if (player != null) {
                    player.teleport(this.getEventManager().getSpectator(this));
                    
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    if (!profile.isInEvent() || (profile.isInEvent() && !profile.getEvent().isSumoTeam())) continue;
                    profile.refreshHotbar();
                }
            }
            this.roundTeamB = null;
        }
        
        this.roundTeamA = this.findRoundTeam();
        this.roundTeamB = this.findRoundTeam();
        
        for (Player playerA : this.roundTeamA.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
            PlayerUtil.reset(playerA);
            PlayerUtil.denyMovement(playerA);

            playerA.teleport(this.getEventManager().getSpawn1(this));

            this.roundTeamA.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
            
            this.roundTeamB.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
        }
        
        for (Player playerB : this.roundTeamB.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
            PlayerUtil.reset(playerB);
            PlayerUtil.denyMovement(playerB);
           
            playerB.teleport(this.getEventManager().getSpawn2(this));
            
            this.roundTeamB.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
            
            this.roundTeamA.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
        }
        
        this.setEventTask(new SumoTeamRoundStartTask(this));
    }

    private EventGroup findRoundTeam() {
        EventGroup eventTeam = null;

        for (EventGroup check : this.getAliveTeams()) {
            if (!isFighting(check) && check.getState() == EventPlayerState.WAITING) {
                if (eventTeam == null) {
                    eventTeam = check;
                    continue;
                }

                if (check.getRoundWins() == 0) {
                    eventTeam = check;
                    continue;
                }

                if (check.getRoundWins() <= eventTeam.getRoundWins()) eventTeam = check;
            }
        }

        if (eventTeam == null) {
            this.end();
            throw new RuntimeException("Could not find a new round player");
        }

        return eventTeam;
    }

    @Override
    public void onDeath(Player player) {
        EventGroup losingTeam = this.getTeamByPlayer(player);
        EventGroup winningTeam = this.roundTeamA.equals(losingTeam) ? this.roundTeamB : this.roundTeamA;

        if (player != null && player.isOnline()) {
            TaskUtil.runLater(() -> {
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.refreshHotbar();
                player.teleport(getEventManager().getSpectator(this));
            }, 2L);
        }

        if (losingTeam.getAlivePlayers() == 0) {
            this.broadcastMessage(
                    Locale.EVENT_ELIMINATED.toString()
                                 .replace("<eliminated_name>", "Team " + losingTeam.getColor().getTitle())
                                 .replace("<eliminator_name>", "Team " + winningTeam.getColor().getTitle()));
            
            this.setState(EventState.ROUND_ENDING);
            this.setEventTask(new SumoTeamRoundEndTask(this));

            winningTeam.setState(EventPlayerState.WAITING);
            winningTeam.getPlayers().stream().filter(eventPlayer -> eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).forEach(eventPlayer -> eventPlayer.setState(EventPlayerState.WAITING));
            winningTeam.setRoundWins(winningTeam.getRoundWins() + 1);

            losingTeam.setState(EventPlayerState.ELIMINATED);
            losingTeam.getPlayers().forEach(eventPlayer -> eventPlayer.setState(EventPlayerState.ELIMINATED));

            TaskUtil.runLater(() -> {
                for (Player winner : winningTeam.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                    Profile profile = Profile.getByUuid(winner.getUniqueId());
                    profile.refreshHotbar();
                    this.refreshNametag();

                    winner.teleport(getEventManager().getSpectator(this));
                }

                for (Player loser : losingTeam.getPlayers().stream().filter(eventPlayer -> eventPlayer != null && eventPlayer.getPlayer() != null && eventPlayer.getPlayer().isOnline()).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                    Profile profile = Profile.getByUuid(loser.getUniqueId());
                    profile.refreshHotbar();
                    this.refreshNametag();

                    loser.teleport(getEventManager().getSpectator(this));
                }
            }, 2L);
        }
    }

    @Override
    public void end() {
        if (waterTask != null) waterTask.cancel();

        getEventManager().setActiveEvent(null);
        getEventManager().setEventCooldown(new Cooldown(600000L));
        
        this.setEventTask(null);
        
        EventGroup winner = this.getWinningTeam();
        if (winner == null) {
            this.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", this.getName()));
        } else {
            String winners = winner.getPlayers().stream().map(EventPlayer::getUsername).collect(Collectors.joining(", "));
           Locale.EVENT_TEAM_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
                                               .replace("<winner_name>","Team " + CC.RED + CC.BOLD + winner.getColor().getTitle())
                                               .replace("<event_name>", this.getName())
                                               .replace("<players>", winners)));
        }
        
        for (EventTeamPlayer eventTeamPlayer : this.getEventTeamPlayers().values()) {
            Player player = eventTeamPlayer.getPlayer();
            if (player == null) continue;
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.setState(ProfileState.IN_LOBBY);
            profile.setEvent(null);
            profile.refreshHotbar();
            profile.teleportToSpawn();
        }
        
        getSpectatorsList().forEach(this::removeSpectator);
        getPlayers().stream().map(Profile::getByPlayer).forEach(Profile::handleVisibility);
    }

    public void refreshNametag() {
        this.getEventPlayers().values().forEach(eventPlayer -> {
            this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
            this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
        });
    }

    @Override
    public EventGroup getWinningTeam() {
        for (EventGroup eventGroup : this.getTeams()) {
            if (eventGroup.getState() != EventPlayerState.ELIMINATED) {
                return eventGroup;
            }
        }
        return null;
    }

    @Override
    public void handleStart() {
        this.setEventTask(new SumoTeamStartTask(this));
        waterTask = new EventWaterTask(this);
        waterTask.runTaskTimer(Array.getInstance(), 20L, 20L);
    }

    @Override
    public EventPlayer getRoundPlayerA() {
        throw new IllegalArgumentException("Unable to get a EventPlayer from a Team Event");
    }

    @Override
    public EventPlayer getRoundPlayerB() {
        throw new IllegalArgumentException("Unable to get a EventPlayer from a Team Event");
    }

    @Override
    public boolean isFighting(UUID uuid) {
        EventGroup group = this.getTeamByPlayer(uuid);
        if (group != null) {
            return isFighting(group);
        }
        return false;
    }

    @Override
    public boolean isFighting(EventGroup group) {
        return this.roundTeamA != null && this.roundTeamA.equals(group) || this.roundTeamB != null && this.roundTeamB.equals(group);
    }

    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            if (!this.isFighting()) {
                return this.getPlugin().getConfigHandler().getEventColor();
            }
            return org.bukkit.ChatColor.GREEN;
        }

        boolean[] booleans = new boolean[]{
                roundTeamA.contains(viewer),
                roundTeamB.contains(viewer),
                roundTeamA.contains(target),
                roundTeamB.contains(target)
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer.getUniqueId())) {
            return roundTeamA.contains(target) ?  org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return ChatColor.AQUA;
        }
    }

    @Override
    public void addSpectator(Player player) {
        this.getSpectators().add(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setEvent(this);
        profile.setState(ProfileState.SPECTATING);
        profile.refreshHotbar();
        profile.handleVisibility();
        TaskUtil.runAsync(() -> getPlayers().stream().map(Profile::getByPlayer).forEach(Profile::handleVisibility));

        player.teleport(getEventManager().getSpectator(this));
    }

    @Override
    public void removeSpectator(Player player) {
        this.getSpectators().remove(player.getUniqueId());
        this.getEventTeamPlayers().remove(player.getUniqueId());

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.setEvent(null);
        profile.setState(ProfileState.IN_LOBBY);
        profile.refreshHotbar();
        profile.handleVisibility();
        profile.teleportToSpawn();
        TaskUtil.runAsync(() -> getPlayers().stream().map(Profile::getByPlayer).forEach(Profile::handleVisibility));
    }

    @Override
    public List<EventGroup> getTeams() {
        return this.teams;
    }

    public EventGroup getAvailableTeams() {
        return this.teams.stream().filter((team) -> team.getPlayers().size() != team.getMaxMembers()).findFirst().orElse(null);
    }

    public EventGroup getTeamByPlayer(Player player) {
        return this.teams.stream().filter((team) -> team.getPlayers().stream().anyMatch(teamplayer -> teamplayer.getUuid().equals(player.getUniqueId()))).findFirst().orElse(null);
    }

    public EventGroup getTeamByPlayer(UUID player) {
        return this.teams.stream().filter((team) -> team.getPlayers().stream().anyMatch(teamplayer -> teamplayer.getUuid().equals(player))).findFirst().orElse(null);
    }

    public List<EventGroup> getAliveTeams() {
        return this.teams.stream().filter(team -> team.getPlayers().stream().anyMatch(player -> player.getState() == EventPlayerState.WAITING)).collect(Collectors.toList());
    }
}

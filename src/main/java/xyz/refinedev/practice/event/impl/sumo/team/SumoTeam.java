package xyz.refinedev.practice.event.impl.sumo.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.group.EventGroupColor;
import xyz.refinedev.practice.event.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayerState;
import xyz.refinedev.practice.event.task.EventStartTask;
import xyz.refinedev.practice.event.task.EventTeamRoundEndTask;
import xyz.refinedev.practice.event.task.EventTeamRoundStartTask;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.PlayerUtil;

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

    private final Array plugin = this.getPlugin();
    public final List<EventGroup> teams = new ArrayList<>();

    private BukkitRunnable waterTask;

    private EventGroup roundTeamA;
    private EventGroup roundTeamB;

    public SumoTeam(Array plugin, Player host, EventTeamSize size) {
        super(plugin, host, EventType.SUMO, size);

        for ( int i = 0; i <= size.getTeams(); i++ ) {
            EventGroupColor color = EventGroupColor.values()[i];
            EventGroup eventGroup = new EventGroup(size.getMaxTeamPlayers(), color);
            this.teams.add(eventGroup);
        }
    }

    @Override
    public boolean isTeam() {
        return true;
    }


    @Override
    public void onJoin(Player player) {
        this.getPlugin().getSpigotHandler().knockback(player, this.plugin.getEventManager().getHelper().getSumoKB());
    }

    @Override
    public void onLeave(Player player) {
        EventTeamPlayer loser = (EventTeamPlayer) this.getEventPlayer(player.getUniqueId());
        loser.setState(EventPlayerState.ELIMINATED);

        if (loser.getGroup() != null && loser.getGroup().getAlivePlayers() == 0) {
            loser.getGroup().setState(EventPlayerState.ELIMINATED);
        }

        this.plugin.getSpigotHandler().resetKnockback(player);
    }

    @Override
    public void onRound() {
        List<EventTeamPlayer> noTeamPlayers = this.getEventTeamPlayers().values().stream().filter(eventPlayer -> eventPlayer.getGroup() == null).collect(Collectors.toList());
        
        for (EventTeamPlayer teamPlayer : noTeamPlayers) {
            this.getAvailableTeams().addPlayer(teamPlayer);
        }
        
        this.getTeams().removeAll(this.getTeams().stream().filter(team -> team.getPlayers().size() == 0).collect(Collectors.toList()));
        this.getEventPlayers().values().stream().filter(this::isApplicable).forEach(eventPlayer -> {
            Profile profile = plugin.getProfileManager().getProfileByUUID(eventPlayer.getUuid());
            plugin.getProfileManager().refreshHotbar(profile);
        });
        
        this.setState(EventState.ROUND_STARTING);

        //Reset Previous Team A
        if (this.roundTeamA != null) {
            for (Player player : this.roundTeamA.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                player.teleport(this.plugin.getEventManager().getHelper().getSpectator(this));

                Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
                if (!this.isRemovable(player)) continue;

                plugin.getProfileManager().refreshHotbar(profile);
            }
            this.roundTeamA = null;
        }
        
        //Reset Previous Team B
        if (this.roundTeamB != null) {
            for (Player player : this.roundTeamB.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                player.teleport(this.plugin.getEventManager().getHelper().getSpectator(this));

                Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
                if (!this.isRemovable(player)) continue;

                plugin.getProfileManager().refreshHotbar(profile);
            }
            this.roundTeamB = null;
        }
        
        this.roundTeamA = this.findRoundTeam();
        this.roundTeamB = this.findRoundTeam();
        
        for (Player playerA : this.roundTeamA.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
            PlayerUtil.reset(playerA);
            PlayerUtil.denyMovement(playerA);

            playerA.teleport(this.plugin.getEventManager().getHelper().getSpawn1(this));

            this.roundTeamA.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
            
            this.roundTeamB.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
        }
        
        for (Player playerB : this.roundTeamB.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
            PlayerUtil.reset(playerB);
            PlayerUtil.denyMovement(playerB);
           
            playerB.teleport(this.plugin.getEventManager().getHelper().getSpawn2(this));
            
            this.roundTeamB.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
            
            this.roundTeamA.getPlayers().forEach(eventPlayer -> {
                this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
                this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
            });
        }
        
        this.setEventTask(new EventTeamRoundStartTask(this.plugin, this));
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
            this.handleEnd();
            throw new RuntimeException("Could not find a new round player");
        }

        return eventTeam;
    }

    @Override
    public void onDeath(Player player) {
        EventGroup losingTeam = this.getTeamByPlayer(player);
        EventGroup winningTeam = this.roundTeamA.equals(losingTeam) ? this.roundTeamB : this.roundTeamA;

        if (player != null && player.isOnline()) {
            this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
                plugin.getProfileManager().refreshHotbar(profile);
                player.teleport(this.plugin.getEventManager().getHelper().getSpectator(this));
            }, 2L);
        }

        if (losingTeam.getAlivePlayers() == 0) {
            this.broadcastMessage(
                    Locale.EVENT_ELIMINATED.toString()
                                 .replace("<eliminated_name>", "Team " + losingTeam.getColor().getTitle())
                                 .replace("<eliminator_name>", "Team " + winningTeam.getColor().getTitle()));
            
            this.setState(EventState.ROUND_ENDING);
            this.setEventTask(new EventTeamRoundEndTask(this.plugin, this));

            winningTeam.setState(EventPlayerState.WAITING);
            winningTeam.getPlayers().stream().filter(this::isApplicable).forEach(eventPlayer -> eventPlayer.setState(EventPlayerState.WAITING));
            winningTeam.setRoundWins(winningTeam.getRoundWins() + 1);

            losingTeam.setState(EventPlayerState.ELIMINATED);
            losingTeam.getPlayers().forEach(eventPlayer -> eventPlayer.setState(EventPlayerState.ELIMINATED));

            this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (Player winner : winningTeam.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                    Profile profile = plugin.getProfileManager().getProfileByUUID(winner.getUniqueId());
                    plugin.getProfileManager().refreshHotbar(profile);
                    this.refreshNameTag();

                    winner.teleport(this.plugin.getEventManager().getHelper().getSpectator(this));
                }

                for (Player loser : losingTeam.getPlayers().stream().filter(this::isApplicable).map(EventPlayer::getPlayer).collect(Collectors.toList())) {
                    Profile profile = plugin.getProfileManager().getProfileByUUID(loser.getUniqueId());
                    plugin.getProfileManager().refreshHotbar(profile);
                    this.refreshNameTag();

                    loser.teleport(this.plugin.getEventManager().getHelper().getSpectator(this));
                }
            }, 2L);
        }
    }

    public void refreshNameTag() {
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
        this.setEventTask(new EventStartTask(plugin, this));
        this.waterTask = new EventWaterTask(this.getPlugin(), this);
        this.waterTask.runTaskTimer(this.plugin, 20L, 20L);
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
    public List<EventGroup> getTeams() {
        return this.teams;
    }
    
    public boolean isApplicable(EventPlayer player) {
        return player != null && player.getPlayer() != null && player.getPlayer().isOnline();
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

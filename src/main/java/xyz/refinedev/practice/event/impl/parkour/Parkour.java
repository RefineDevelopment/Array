package xyz.refinedev.practice.event.impl.parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.parkour.task.ParkourRoundEndTask;
import xyz.refinedev.practice.event.impl.parkour.task.ParkourRoundStartTask;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.other.PlayerSnapshot;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

public class Parkour extends Event {

    private BukkitRunnable waterTask;
    private Player winner;

    public Parkour(Array plugin, Player host) {
        super(plugin, plugin.getEventManager(), "Parkour", new PlayerSnapshot(host.getUniqueId(), host.getName()), 100, EventType.PARKOUR, EventTeamSize.SOLO);

        this.setEvent_Prefix(Locale.EVENT_PREFIX.toString().replace("<event_name>", this.getName()));
    }

    @Override
    public boolean isFreeForAll() {
        return true;
    }

    @Override
    public boolean isTeam() {
        return false;
    }

    @Override
    public void handleJoin(Player player) {
        this.getEventPlayers().put(player.getUniqueId(), new EventPlayer(player));

        this.broadcastMessage(Locale.EVENT_JOIN.toString()
                .replace("<event_name>", this.getName())
                .replace("<joined>", player.getName())
                .replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
                .replace("<event_max_players>", String.valueOf(getMaxPlayers())));

        this.onJoin(player);

        Profile profile = this.getPlugin().getProfileManager().getByUUID(player.getUniqueId());
        profile.setEvent(this);
        profile.setState(ProfileState.IN_EVENT);

        this.getPlugin().getProfileManager().handleVisibility(profile);
        this.getPlugin().getProfileManager().refreshHotbar(profile);

        player.teleport(this.getEventManager().getSpawn(this));

        PlayerUtil.denyMovement(player);
    }

    @Override
    public void onRound() {
        this.setState(EventState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player == null) return;
            player.teleport(this.getEventManager().getSpawn(this));

            Profile profile = this.getPlugin().getProfileManager().getByUUID(player.getUniqueId());
            if (profile.isInEvent() && profile.getEvent().equals(this)) {
                this.getPlugin().getProfileManager().refreshHotbar(profile);
            }
        }
        this.setEventTask(new ParkourRoundStartTask(this));
    }

    @Override
    public void onDeath(Player player) {
    }

    @Override
    public Player getWinner() {
        return this.winner;
    }

    public void handleWin(Player player) {
        this.winner = player;

        this.setState(EventState.ROUND_ENDING);
        this.setEventTask(new ParkourRoundEndTask(this));
    }

    @Override
    public void handleStart() {
        this.setEventTask(new ParkourRoundStartTask(this));
        waterTask = new EventWaterTask(this.getPlugin(), this);
        waterTask.runTaskTimer(this.getPlugin(), 20L, 20L);
    }

    @Override
    public void end() {
        if (waterTask != null) waterTask.cancel();
        super.end();
    }

    @Override
    public boolean isFighting(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        if (this.isFighting()) {
            return getRemainingPlayers().contains(player);
        }
        return false;
    }

    @Override
    public EventGroup getWinningTeam() {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

    @Override
    public List<EventGroup> getTeams() {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

    @Override
    public EventPlayer getRoundPlayerA() {
        throw new IllegalArgumentException("Can not get a Player from an FFA Event");
    }

    @Override
    public EventPlayer getRoundPlayerB() {
        throw new IllegalArgumentException("Can not get a Player from an FFA Event");
    }

    @Override
    public EventGroup getRoundTeamA() {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

    @Override
    public EventGroup getRoundTeamB() {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

    @Override
    public boolean isFighting(EventGroup group) {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

    @Override
    public void onJoin(Player player) {

    }
    @Override
    public void onLeave(Player player) {
    }

    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        return null;
    }
}

package xyz.refinedev.practice.event.impl.parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.parkour.task.ParkourRoundStartTask;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.util.other.PlayerSnapshot;

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

    public Parkour(Array plugin, Player host) {
        super(plugin, plugin.getEventManager(), "Parkour", new PlayerSnapshot(host.getUniqueId(), host.getName()), 100, EventType.PARKOUR);

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
    public void onJoin(Player player) {

    }

    @Override
    public void onLeave(Player player) {

    }

    @Override
    public void onRound() {

    }

    @Override
    public void onDeath(Player player) {

    }

    @Override
    public void handleStart() {
        this.setEventTask(new ParkourRoundStartTask(this));
        waterTask = new EventWaterTask(this);
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
    public ChatColor getRelationColor(Player viewer, Player target) {
        return null;
    }
}

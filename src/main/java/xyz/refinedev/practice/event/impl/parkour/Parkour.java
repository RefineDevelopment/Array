package xyz.refinedev.practice.event.impl.parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.*;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventRoundEndTask;
import xyz.refinedev.practice.event.task.EventRoundStartTask;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
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

    private final Array plugin;

    private BukkitRunnable waterTask;
    private Player winner;

    public Parkour(Array plugin, Player host) {
        super(plugin, host, EventType.PARKOUR, EventTeamSize.SOLO);

        this.plugin = plugin;
    }

    @Override
    public boolean isFreeForAll() {
        return true;
    }

    @Override
    public void handleJoin(Player player) {
        super.handleJoin(player);
        PlayerUtil.denyMovement(player);
    }

    @Override
    public void onRound() {
        this.setState(EventState.ROUND_STARTING);

        for (Player player : this.getRemainingPlayers()) {
            if (player == null) return;
            player.teleport(EventHelperUtil.getSpawn(this));

            Profile profile = this.getPlugin().getProfileManager().getProfileByUUID(player.getUniqueId());
            if (this.isRemovable(player)) {
                this.getPlugin().getProfileManager().refreshHotbar(profile);
            }
        }
        this.setEventTask(new EventRoundStartTask(plugin, this));
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
        this.setEventTask(new EventRoundEndTask(plugin, this));
    }

    @Override
    public void handleStart() {
        this.setEventTask(new EventRoundStartTask(plugin, this));
        waterTask = new EventWaterTask(this.getPlugin(), this);
        waterTask.runTaskTimer(this.getPlugin(), 20L, 20L);
    }

    @Override
    public void handleEnd() {
        if (waterTask != null) waterTask.cancel();
        super.handleEnd();
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
        if (!isFighting()) return this.plugin.getConfigHandler().getEventColor();

        if (viewer.equals(target)) {
            return ChatColor.GREEN;
        }

        return ChatColor.RED;
    }
}

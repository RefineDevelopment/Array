package xyz.refinedev.practice.event.impl.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.*;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventRoundEndTask;
import xyz.refinedev.practice.event.task.EventRoundStartTask;
import xyz.refinedev.practice.event.task.EventStartTask;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.hotbar.HotbarItem;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/13/2021
 * Project: Array
 */

public class Spleef extends Event {

    private final Array plugin;
    private BukkitRunnable waterTask;

    public Spleef(Array plugin, Player host) {
        super(plugin, host, EventType.SPLEEF, EventTeamSize.SOLO);

        this.plugin = plugin;
    }

    @Override
    public boolean isFreeForAll() {
        return true;
    }

    @Override
    public void onJoin(Player player) {
        this.plugin.getSpigotHandler().knockback(player, EventHelperUtil.getSpleefKB());
    }

    @Override
    public void onLeave(Player player) {
        this.plugin.getSpigotHandler().resetKnockback(player);
    }

    @Override
    public void onRound() {
        this.setState(EventState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {
            List<Location> circleLocations = LocationUtil.getCircle(EventHelperUtil.getSpawn(this), this.plugin.getConfigHandler().getFFA_SPAWN_RADIUS(), this.getPlayers().size());

            Location center = EventHelperUtil.getSpawn(this).clone();
            Location loc = circleLocations.get(i);
            Location target = loc.setDirection(center.subtract(loc).toVector());
            HotbarItem item = this.plugin.getHotbarManager().getHotbarItem(HotbarType.SPLEEF_MATCH);

            player.teleport(target.add(0, 0.5, 0));
            player.getInventory().addItem(item.getItem());

            circleLocations.remove(i);
            i++;
        }
        this.setEventTask(new EventRoundStartTask(plugin, this));
    }

    @Override
    public void onDeath(Player player) {
        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());
        this.plugin.getProfileManager().handleVisibility(profile);
        this.plugin.getProfileManager().refreshHotbar(profile);

        this.broadcastMessage(Locale.EVENT_DIED.toString().replace("<eliminated_name>", player.getName()));

        if (canEnd()) {
            this.setState(EventState.ROUND_ENDING);
            this.setEventTask(new EventRoundEndTask(plugin, this));
        }
    }

    @Override
    public void handleStart() {
        this.setEventTask(new EventStartTask(this.plugin, this));
        waterTask = new EventWaterTask(this.plugin, this);
        waterTask.runTaskTimer(this.plugin, 20L, 20L);
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
    public EventPlayer getRoundPlayerA() {
        throw new IllegalArgumentException("Can not get a Round Player from an FFA Event");
    }

    @Override
    public EventPlayer getRoundPlayerB() {
        throw new IllegalArgumentException("Can not get a Round Player from an FFA Event");
    }

    @Override
    public EventGroup getWinningTeam() {
        throw new IllegalArgumentException("Can not get a Team from a Solo Event");
    }

    @Override
    public List<EventGroup> getTeams() {
        throw new IllegalArgumentException("Can not get a Team from a Solo Event");
    }

    @Override
    public EventGroup getRoundTeamA() {
        throw new IllegalArgumentException("Can not get a Team from a Solo Event");
    }

    @Override
    public EventGroup getRoundTeamB() {
        throw new IllegalArgumentException("Can not get a Team from a Solo Event");
    }

    @Override
    public boolean isFighting(EventGroup group) {
        throw new IllegalArgumentException("Can not get a Team from a Solo Event");
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

package xyz.refinedev.practice.event.impl.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.spleef.task.SpleefRoundEndTask;
import xyz.refinedev.practice.event.impl.spleef.task.SpleefRoundStartTask;
import xyz.refinedev.practice.event.impl.spleef.task.SpleefStartTask;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventWaterTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.hotbar.HotbarItem;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.util.location.Circle;
import xyz.refinedev.practice.util.other.PlayerSnapshot;
import xyz.refinedev.practice.util.other.TaskUtil;

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

    private final Array plugin = this.getPlugin();

    private BukkitRunnable waterTask;

    public Spleef(Player host) {
        super("Spleef", new PlayerSnapshot(host.getUniqueId(), host.getName()), 100, EventType.SPLEEF);

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
        plugin.getKnockbackManager().knockback(player, this.getEventManager().getSpleefKB());
    }

    @Override
    public void onLeave(Player player) {
        plugin.getKnockbackManager().resetKnockback(player);
    }

    @Override
    public void onRound() {
        this.setState(EventState.ROUND_STARTING);

        int i = 0;
        for (Player player : this.getRemainingPlayers()) {
            Location midSpawn = plugin.getEventManager().getSpawn(this);
            List<Location> circleLocations = Circle.getCircle(midSpawn, plugin.getConfigHandler().getFFA_SPAWN_RADIUS(), this.getPlayers().size());

            Location center = midSpawn.clone();
            Location loc = circleLocations.get(i);
            Location target = loc.setDirection(center.subtract(loc).toVector());
            HotbarItem item = plugin.getHotbarManager().getHotbarItem(HotbarType.SPLEEF_MATCH);

            player.teleport(target.add(0, 0.5, 0));
            player.getInventory().addItem(item.getItem());

            circleLocations.remove(i);
            i++;
        }
        this.setEventTask(new SpleefRoundStartTask(this));
    }

    @Override
    public void onDeath(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        this.broadcastMessage(Locale.EVENT_DIED.toString().replace("<eliminated_name>", player.getName()));

        if (canEnd()) {
            this.setState(EventState.ROUND_ENDING);
            this.setEventTask(new SpleefRoundEndTask(this));
        }

        TaskUtil.runAsync(() -> {
            for (Player otherPlayer : getPlayers()) {
                Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
                otherProfile.handleVisibility(otherPlayer, player);
                profile.handleVisibility(player, otherPlayer);

                this.getPlugin().getNameTagHandler().reloadPlayer(player);
                this.getPlugin().getNameTagHandler().reloadOthersFor(player);
            }
        });
        profile.refreshHotbar();
    }

    @Override
    public void handleStart() {
        this.setEventTask(new SpleefStartTask(this));
        waterTask = new EventWaterTask(this);
        waterTask.runTaskTimer(Array.getInstance(), 20L, 20L);
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
        if (!isFighting()) return this.getPlugin().getConfigHandler().getEventColor();

        if (viewer.equals(target)) {
            return ChatColor.GREEN;
        }

        return ChatColor.RED;
    }
}

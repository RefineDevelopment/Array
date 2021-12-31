package xyz.refinedev.practice.event.impl.sumo.solo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.*;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayerState;
import xyz.refinedev.practice.event.task.EventRoundEndTask;
import xyz.refinedev.practice.event.task.EventRoundStartTask;
import xyz.refinedev.practice.event.task.EventStartTask;
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
 * Created at 6/25/2021
 * Project: Array
 */

@Getter
@Setter
public class SumoSolo extends Event {

    private final Array plugin;
    
    private BukkitRunnable waterTask;

    private EventPlayer roundPlayerA;
    private EventPlayer roundPlayerB;

    public SumoSolo(Array plugin, Player host) {
        super(plugin, host, EventType.SUMO, EventTeamSize.SOLO);
        
        this.plugin = plugin;
    }

    @Override
    public void onJoin(Player player) {
        this.plugin.getSpigotHandler().knockback(player, EventHelperUtil.getSumoKB());
    }

    @Override
    public void onLeave(Player player) {
        this.plugin.getSpigotHandler().resetKnockback(player);
    }

    @Override
    public void onRound() {
        this.setState(EventState.ROUND_STARTING);

        if (roundPlayerA != null) {
            Player player = roundPlayerA.getPlayer();

            if (player != null) {
                player.teleport(EventHelperUtil.getSpectator(this));

                Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());

                if (this.isRemovable(player)) {
                    this.plugin.getProfileManager().refreshHotbar(profile);
                }
            }

            roundPlayerA = null;
        }

        if (roundPlayerB != null) {
            Player player = roundPlayerB.getPlayer();

            if (player != null) {
                player.teleport(EventHelperUtil.getSpectator(this));

                Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());

                if (this.isRemovable(player)) {
                    this.plugin.getProfileManager().refreshHotbar(profile);
                }
            }

            roundPlayerB = null;
        }

        roundPlayerA = findRoundPlayer();
        roundPlayerB = findRoundPlayer();

        Player playerA = roundPlayerA.getPlayer();
        Player playerB = roundPlayerB.getPlayer();

        PlayerUtil.reset(playerA);
        PlayerUtil.reset(playerB);

        PlayerUtil.denyMovement(playerA);
        PlayerUtil.denyMovement(playerB);

        playerA.teleport(EventHelperUtil.getSpawn1(this));
        playerB.teleport(EventHelperUtil.getSpawn2(this));

        this.setEventTask(new EventRoundStartTask(this.plugin, this));
    }

    private EventPlayer findRoundPlayer() {
        EventPlayer eventPlayer = null;

        for (EventPlayer check : getEventPlayers().values()) {
            if (!this.isFighting(check.getUuid()) && check.getState() == EventPlayerState.WAITING) {
                if (eventPlayer == null) {
                    eventPlayer = check;
                    continue;
                }

                if (check.getRoundWins() == 0) {
                    eventPlayer = check;
                    continue;
                }

                if (check.getRoundWins() <= eventPlayer.getRoundWins()) {
                    eventPlayer = check;
                }
            }
        }

        if (eventPlayer == null) {
            throw new RuntimeException("Could not find a new round player");
        }

        return eventPlayer;
    }

    @Override
    public void onDeath(Player player) {
        EventPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
        winner.setState(EventPlayerState.WAITING);
        winner.incrementRoundWins();
        winner.getPlayer().teleport(EventHelperUtil.getSpectator(this));

        broadcastMessage(Locale.EVENT_ELIMINATED.toString()
                .replace("<eliminated_name>", player.getName())
                .replace("<eliminator_name>", winner.getPlayer().getName()));

        this.setState(EventState.ROUND_ENDING);
        this.setEventTask(new EventRoundEndTask(this.plugin, this));
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
        return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
    }

    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            if (!this.isFighting()) {
                return this.plugin.getConfigHandler().getEventColor();
            }
            return ChatColor.GREEN;
        }

        boolean[] booleans = new boolean[]{
                roundPlayerA.getUuid().equals(viewer.getUniqueId()),
                roundPlayerB.getUuid().equals(viewer.getUniqueId()),
                roundPlayerA.getUuid().equals(target.getUniqueId()),
                roundPlayerB.getUuid().equals(target.getUniqueId())
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer.getUniqueId())) {
            return roundPlayerA.getUuid().equals(target.getUniqueId()) ? org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.AQUA;
        }
    }

    @Override
    public EventPlayer getRoundPlayerA() {
        return this.roundPlayerA;
    }

    @Override
    public EventPlayer getRoundPlayerB() {
        return this.roundPlayerB;
    }

    @Override
    public EventGroup getRoundTeamA() {
        throw new IllegalArgumentException("You can't get a team from a solo event");
    }

    @Override
    public EventGroup getRoundTeamB() {
        throw new IllegalArgumentException("You can't get a team from a solo event");
    }

    @Override
    public EventGroup getWinningTeam() {
        throw new IllegalArgumentException("You can't get a team from a solo event");
    }

    @Override
    public List<EventGroup> getTeams() {
        throw new IllegalArgumentException("You can't get a team from a solo event");
    }

    @Override
    public boolean isFighting(EventGroup group) {
        throw new IllegalArgumentException("You can't get a team from a solo event");
    }

}

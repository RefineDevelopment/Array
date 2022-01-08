package xyz.refinedev.practice.event.impl.lms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.task.EventRoundEndTask;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;

import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/23/2021
 * Project: Array
 */

public class LMS extends Event {

    private final Array plugin;

    private Player winner;
    private final Kit kit;

    public LMS(Array plugin, Player host, Kit kit) {
        super(plugin, host, EventType.LMS, EventTeamSize.SOLO);

        this.kit = kit;
        this.plugin = plugin;
    }

    @Override
    public boolean isFreeForAll() {
        return true;
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
        Profile profile = this.getPlugin().getProfileManager().getProfileByUUID(player.getUniqueId());

        if (player.getKiller() != null) {
            this.broadcastMessage(Locale.EVENT_ELIMINATED.toString()
                    .replace("<eliminated_name>", player.getName())
                    .replace("<eliminator_name>", player.getKiller().getName()));
        }

        if (this.canEnd()) {
            this.setState(EventState.ROUND_ENDING);
            this.setEventTask(new EventRoundEndTask(this.getPlugin(), this));
        }

        for (Player otherPlayer : getPlayers()) {
            Profile otherProfile = this.getPlugin().getProfileManager().getProfileByUUID(otherPlayer.getUniqueId());
            this.getPlugin().getProfileManager().handleVisibility(otherProfile, player);
            this.getPlugin().getProfileManager().handleVisibility(profile, otherPlayer);
        }

        this.getPlugin().getProfileManager().refreshHotbar(profile);
    }

    @Override
    public Player getWinner() {
        return this.winner;
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
    public boolean isFighting(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        if (this.isFighting()) {
            return getRemainingPlayers().contains(player);
        }
        return false;
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

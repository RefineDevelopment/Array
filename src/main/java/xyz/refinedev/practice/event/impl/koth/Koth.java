package xyz.refinedev.practice.event.impl.koth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.koth.task.KoTHDetectTask;
import xyz.refinedev.practice.event.impl.koth.task.KothRoundStartTask;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.group.EventGroupColor;
import xyz.refinedev.practice.event.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.other.PlayerSnapshot;

import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 11/4/2021
 * Project: Array
 */

//TODO: A completely new take on the gamemode itself (from dani)
//TODO: Just Basically, have a 5 minute timer, players need to get to the top of the hill through parkour
//TODO: They get punched off on the way and have to restart (each player has knockback 2 or 3 sticks)
public class Koth extends Event {

    private BukkitRunnable detectTask;

    private EventGroup eventGroupA;
    private EventGroup eventGroupB;

    public Koth(Array plugin, Player player, EventTeamSize size) {
        super(plugin, plugin.getEventManager(), "KoTH", new PlayerSnapshot(player.getUniqueId(), player.getName()), size.getMaxParticipants(), EventType.KOTH, size);

        eventGroupA = new EventGroup(size.getMaxTeamPlayers(), EventGroupColor.RED);
        eventGroupB = new EventGroup(size.getMaxTeamPlayers(), EventGroupColor.BLUE);

        this.setEvent_Prefix(Locale.EVENT_PREFIX.toString().replace("<event_name>", this.getName()));
    }

    @Override
    public boolean isFreeForAll() {
        return true;
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

        this.onJoin(player);

        Profile profile = this.getPlugin().getProfileManager().getByUUID(player.getUniqueId());
        profile.setEvent(this);
        profile.setState(ProfileState.IN_EVENT);

        this.getPlugin().getProfileManager().handleVisibility(profile);
        this.getPlugin().getProfileManager().refreshHotbar(profile);

        player.teleport(this.getEventManager().getSpectator(this));
    }


    @Override
    public void onJoin(Player player) {
        this.getPlugin().getSpigotHandler().knockback(player, this.getEventManager().getKothKB());
    }

    @Override
    public void onLeave(Player player) {
        this.getPlugin().getSpigotHandler().resetKnockback(player);
    }

    @Override
    public void onRound() {

    }

    @Override
    public void onDeath(Player player) {
        Profile profile = this.getPlugin().getProfileManager().getByUUID(player.getUniqueId());
        this.getPlugin().getProfileManager().handleVisibility(profile);
        this.getPlugin().getProfileManager().refreshHotbar(profile);

        this.broadcastMessage(Locale.EVENT_DIED.toString().replace("<eliminated_name>", player.getName()));
    }

    @Override
    public void handleStart() {
        this.setEventTask(new KothRoundStartTask(this));

        detectTask = new KoTHDetectTask(this);
        detectTask.runTaskTimer(this.getPlugin(), 20L, 20L);
    }

    @Override
    public void end() {
        if (detectTask != null) detectTask.cancel();
        super.end();
    }

    @Override
    public boolean isFighting(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;

        if (this.isFighting()) {
            return this.getRemainingPlayers().contains(player);
        }
        return false;
    }

    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        EventPlayer eventPlayer = this.getEventPlayer(target.getUniqueId());
        if (eventPlayer == null) return ChatColor.WHITE;

        if (eventPlayer.isInKoth()) {
            return ChatColor.GOLD;
        }

        if (viewer == target) return ChatColor.GREEN;

        return ChatColor.RED;
    }

    @Override
    public EventGroup getWinningTeam() {
        throw new IllegalArgumentException("Can not get a team from a KoTH Event");
    }

    @Override
    public List<EventGroup> getTeams() {
        throw new IllegalArgumentException("Can not get a team from a KoTH Event");
    }

    @Override
    public EventPlayer getRoundPlayerA() {
        throw new IllegalArgumentException("Can not get a Player from a KoTH Event");
    }

    @Override
    public EventPlayer getRoundPlayerB() {
        throw new IllegalArgumentException("Can not get a Player from a KoTH Event");
    }

    @Override
    public EventGroup getRoundTeamA() {
        return eventGroupA;
    }

    @Override
    public EventGroup getRoundTeamB() {
        return eventGroupB;
    }

    @Override
    public boolean isFighting(EventGroup group) {
        throw new IllegalArgumentException("Can not get a team from an FFA Event");
    }

}

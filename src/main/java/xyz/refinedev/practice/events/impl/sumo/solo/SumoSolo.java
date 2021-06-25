package xyz.refinedev.practice.events.impl.sumo.solo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.impl.sumo.solo.task.SumoSoloRoundEndTask;
import xyz.refinedev.practice.events.impl.sumo.solo.task.SumoSoloRoundStartTask;
import xyz.refinedev.practice.events.impl.sumo.solo.task.SumoSoloStartTask;
import xyz.refinedev.practice.events.meta.player.EventPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayerState;
import xyz.refinedev.practice.hook.SpigotHook;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.PlayerSnapshot;
import xyz.refinedev.practice.util.other.PlayerUtil;

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

    private EventPlayer roundPlayerA;
    private EventPlayer roundPlayerB;

    public SumoSolo(PlayerSnapshot host, int maxPlayers) {
        super("Sumo", host, maxPlayers, EventType.SUMO_SOLO);
    }

    @Override
    public boolean isSumo() {
        return true;
    }

    @Override
    public boolean isBrackets() {
        return false;
    }

    @Override
    public boolean isLMS() {
        return false;
    }

    @Override
    public boolean isGulag() {
        return false;
    }

    @Override
    public boolean isSpleef() {
        return false;
    }

    @Override
    public boolean isParkour() {
        return false;
    }

    @Override
    public boolean isFreeForAll() {
        return false;
    }

    @Override
    public void onJoin(Player player) {
        SpigotHook.getKnockbackType().applyKnockback(player, this.getEventManager().getSumoKB());
    }

    @Override
    public void onLeave(Player player) {
        SpigotHook.getKnockbackType().applyDefaultKnockback(player);
    }

    @Override
    public void onRound() {
        setState(EventState.ROUND_STARTING);

        if (roundPlayerA != null) {
            Player player = roundPlayerA.getPlayer();

            if (player != null) {
                player.teleport(this.getEventManager().getSpectator(this));

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInEvent() && profile.getEvent().isSumo()) {
                    profile.refreshHotbar();
                }
            }

            roundPlayerA = null;
        }

        if (roundPlayerB != null) {
            Player player = roundPlayerB.getPlayer();

            if (player != null) {
                player.teleport(this.getEventManager().getSpectator(this));

                Profile profile = Profile.getByUuid(player.getUniqueId());

                if (profile.isInEvent() && profile.getEvent().isSumo()) {
                    profile.refreshHotbar();
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

        playerA.teleport(getEventManager().getSpawn1(this));
        playerB.teleport(getEventManager().getSpawn2(this));

        this.setEventTask(new SumoSoloRoundStartTask(this));
    }

    private EventPlayer findRoundPlayer() {
        EventPlayer eventPlayer = null;

        for (EventPlayer check : getEventPlayers().values()) {
            if (!isFighting(check.getUuid()) && check.getState() == EventPlayerState.WAITING) {
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
        winner.getPlayer().teleport(getEventManager().getSpectator(this));


        broadcastMessage(Locale.EVENT_ELIMINATED.toString()
                .replace("<eliminated_name>", player.getName())
                .replace("<eliminator_name>", winner.getPlayer().getName()));

        setState(EventState.ROUND_ENDING);
        setEventTask(new SumoSoloRoundEndTask(this));
    }

    @Override
    public void handleStart() {
        this.setEventTask(new SumoSoloStartTask(this));
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
    public boolean isFighting(UUID uuid) {
        return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
    }
}

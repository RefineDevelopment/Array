package xyz.refinedev.practice.events.impl;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TimeUtil;
import me.joeleoli.praxi.Praxi;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventPlayer;
import xyz.refinedev.practice.events.EventPlayerState;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.task.EventRoundEndTask;
import xyz.refinedev.practice.events.task.EventRoundStartTask;
import me.joeleoli.praxi.player.PraxiPlayer;
import org.bukkit.entity.Player;

@Getter
public class SumoEvent extends Event {

	private EventPlayer roundPlayerA;
	private EventPlayer roundPlayerB;
	@Setter
	private long roundStart;

	public SumoEvent(Player player) {
		super("Sumo", new PlayerInfo(player), 100);
	}

	@Override
	public boolean isSumo() {
		return true;
	}

	@Override
	public boolean isCorners() {
		return false;
	}

	@Override
	public void onJoin(Player player) {
		this.getPlayers().forEach(otherPlayer -> {
			player.showPlayer(otherPlayer);
			otherPlayer.showPlayer(player);
		});
	}

	@Override
	public void onLeave(Player player) {
		player.setKnockbackProfile(null);
	}

	@Override
	public void onRound() {
		this.setState(EventState.ROUND_STARTING);

		if (this.roundPlayerA != null) {
			final Player player = this.roundPlayerA.toPlayer();

			if (player != null) {
				player.teleport(Praxi.getInstance().getEventManager().getSumoSpectator());

				final PraxiPlayer praxiPlayer = PraxiPlayer.getByUuid(player.getUniqueId());

				if (praxiPlayer.isInEvent()) {
					praxiPlayer.loadHotbar();
				}
			}

			this.roundPlayerA = null;
		}

		if (this.roundPlayerB != null) {
			final Player player = this.roundPlayerB.toPlayer();

			if (player != null) {
				player.teleport(Praxi.getInstance().getEventManager().getSumoSpectator());

				final PraxiPlayer praxiPlayer = PraxiPlayer.getByUuid(player.getUniqueId());

				if (praxiPlayer.isInEvent()) {
					praxiPlayer.loadHotbar();
				}
			}

			this.roundPlayerB = null;
		}

		this.roundPlayerA = this.findRoundPlayer();
		this.roundPlayerB = this.findRoundPlayer();

		final Player playerA = this.roundPlayerA.toPlayer();
		final Player playerB = this.roundPlayerB.toPlayer();

		PlayerUtil.reset(playerA);
		PlayerUtil.reset(playerB);

		PlayerUtil.denyMovement(playerA);
		PlayerUtil.denyMovement(playerB);

		playerA.teleport(Praxi.getInstance().getEventManager().getSumoSpawn1());
		playerB.teleport(Praxi.getInstance().getEventManager().getSumoSpawn2());

		this.setEventTask(new EventRoundStartTask(this));
	}

	@Override
	public void onDeath(Player player) {
		final EventPlayer winner = this.roundPlayerA.getUuid().equals(player.getUniqueId()) ? this.roundPlayerB : this.roundPlayerA;

		winner.setState(EventPlayerState.WAITING);
		winner.incrementRoundWins();

		this.broadcastMessage(Style.PINK + player.getName() + Style.YELLOW + " was eliminated by " + Style.PINK + winner.getName() + Style.YELLOW + "!");
		this.setState(EventState.ROUND_ENDING);
		this.setEventTask(new EventRoundEndTask(this));
	}

	@Override
	public String getRoundDuration() {
		if (this.getState() == EventState.ROUND_STARTING) {
			return "00:00";
		} else if (this.getState() == EventState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - this.roundStart);
		} else {
			return "Ending";
		}
	}

	@Override
	public boolean isFighting(UUID uuid) {
		return (this.roundPlayerA != null && this.roundPlayerA.getUuid().equals(uuid)) || (this.roundPlayerB != null && this.roundPlayerB.getUuid().equals(uuid));
	}

	private EventPlayer findRoundPlayer() {
		EventPlayer eventPlayer = null;

		for (EventPlayer check : this.getEventPlayers().values()) {
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

}

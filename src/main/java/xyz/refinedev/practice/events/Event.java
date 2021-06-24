package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.task.EventStartTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.nametags.NameTagHandler;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.other.PlayerSnapshot;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;

@Getter @Setter
@RequiredArgsConstructor
public abstract class Event {

	protected final String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", this.getName());

	private final Map<UUID, EventPlayer> eventPlayers = new HashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	private final Array plugin = Array.getInstance();
	private final EventState state = EventState.WAITING;

	private final String name;
	private final PlayerSnapshot host;
	private final int maxPlayers;
	private final EventType type;

	private EventTask eventTask;
	private Cooldown cooldown;

	public void setEventTask(EventTask task) {
		if (this.eventTask != null) {
			this.eventTask.cancel();
		}

		this.eventTask = task;

		if (this.eventTask != null) {
			this.eventTask.runTaskTimer(plugin, 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return this.state == EventState.WAITING;
	}

	public boolean isFighting() {
		return this.state == EventState.ROUND_FIGHTING;
	}

	public EventPlayer getEventPlayer(UUID uuid) {
		return this.eventPlayers.get(uuid);
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (EventPlayer eventPlayer : this.eventPlayers.values()) {
			final Player player = eventPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for ( EventPlayer eventPlayer : eventPlayers.values()) {
			if (eventPlayer.getState() == EventPlayerState.WAITING) {
				Player player = eventPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleStart() {
		this.setEventTask(new EventStartTask(this));
	}

	public void handleJoin(Player player) {
		this.eventPlayers.put(player.getUniqueId(), new EventPlayer(player));

		this.broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", getName())
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		this.onJoin(player);

		final Profile profile = Profile.getByUuid(player.getUniqueId());

		profile.setEvent(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getEventManager().getSpawn1(this));

		TaskUtil.runAsync(() -> {
			for (Player otherPlayer : getPlayers()) {
				Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
				otherProfile.handleVisibility(otherPlayer, player);
				profile.handleVisibility(player, otherPlayer);

				NameTagHandler.reloadPlayer(player);
				NameTagHandler.reloadOthersFor(player);
			}
		});
	}

	public void handleDeath(Player player) {
		final EventPlayer loser = this.getEventPlayer(player.getUniqueId());
		loser.setState(EventPlayerState.ELIMINATED);
		this.onDeath(player);
	}

	public void handleLeave(Player player) {
		if (this.isFighting(player.getUniqueId())) {
			this.handleDeath(player);
		}

		this.eventPlayers.remove(player.getUniqueId());
		this.onLeave(player);

		Profile profile = Profile.getByPlayer(player);

		TaskUtil.runAsync(() -> {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);

					NameTagHandler.reloadPlayer(player);
					NameTagHandler.reloadOthersFor(player);
				}
		});

		if (state == EventState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", getName())
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", getName()));

		profile.setState(ProfileState.IN_LOBBY);
		profile.setEvent(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	public void end() {
		// Remove active event and set cooldown
		plugin.getEventManager().setActiveEvent(null);
		plugin.getEventManager().setEventCooldown(new Cooldown(60_000L * 3));

		// Cancel any active task
		this.setEventTask(null);

		final Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", getName()));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", getName())
					.replace("<event_prefix>", EVENT_PREFIX)));
		}

		for (EventPlayer eventPlayer : this.eventPlayers.values()) {
			final Player player = eventPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSumo(null);
				profile.refreshHotbar();
				profile.teleportToSpawn();
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);
		getPlayers().stream().map(Profile::getByPlayer).forEach(Profile::handleVisibility);
	}

	public boolean canEnd() {
		int remaining = 0;

		for (EventPlayer eventPlayer : this.eventPlayers.values()) {
			if (eventPlayer.getState() == EventPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for (EventPlayer eventPlayer : this.eventPlayers.values()) {
			if (eventPlayer.getState() != EventPlayerState.ELIMINATED) {
				return eventPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", this.getName())
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", this.getName()), "/event join");

			for ( Player player : Bukkit.getOnlinePlayers() ) {
				if (!eventPlayers.containsKey(player.getUniqueId())) {
					message.sendToPlayer(player);
				}
			}
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : this.getPlayers()) {
			player.sendMessage(EVENT_PREFIX + message);
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public abstract boolean isSumo();

	public abstract boolean isBrackets();

	public abstract boolean isLMS();

	public abstract boolean isGulag();

	public abstract boolean isSpleef();

	public abstract boolean isParkour();

	public abstract boolean isFreeForAll();

	public abstract boolean isEnabled();

	public abstract void onJoin(Player player);

	public abstract void onLeave(Player player);

	public abstract void onRound();

	public abstract void onDeath(Player player);

	public abstract String getRoundDuration();

	public abstract EventPlayer getRoundPlayerA();

	public abstract EventPlayer getRoundPlayerB();

	public abstract boolean isFighting(UUID uuid);

	public abstract void addSpectator(Player player);

	public abstract void removeSpectator(Player player);

}

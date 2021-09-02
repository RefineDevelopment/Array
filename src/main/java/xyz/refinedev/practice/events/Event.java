package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.meta.EventTask;
import xyz.refinedev.practice.events.meta.group.EventGroup;
import xyz.refinedev.practice.events.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayer;
import xyz.refinedev.practice.events.meta.player.EventPlayerState;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.*;

import java.util.*;

@Getter @Setter
@RequiredArgsConstructor
public abstract class Event {

	private String Event_Prefix;

	private final Array plugin = Array.getInstance();
	private final EventManager eventManager = plugin.getEventManager();

	private final Map<UUID, EventPlayer> eventPlayers = new HashMap<>();
	private final Map<UUID, EventTeamPlayer> eventTeamPlayers = new HashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	private final List<Entity> entities = new ArrayList<>();

	private final String name;
	private final PlayerSnapshot host;
	private final int maxPlayers;
	private final EventType type;

	private EventTask eventTask;
	private Cooldown cooldown;
	private EventState state = EventState.WAITING;

	private int totalPlayers;
	private long roundStart;

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
		if (isTeam()) return eventTeamPlayers.get(uuid);
		return this.eventPlayers.get(uuid);
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			final Player player = eventPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for ( EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			if (eventPlayer.getState() == EventPlayerState.WAITING) {
				Player player = eventPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		if (isTeam()) {
			this.eventTeamPlayers.put(player.getUniqueId(), new EventTeamPlayer(player));
		} else {
			this.eventPlayers.put(player.getUniqueId(), new EventPlayer(player));
		}

		this.broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", this.getName())
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		this.onJoin(player);

		final Profile profile = Profile.getByUuid(player.getUniqueId());

		profile.setEvent(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		if (isFreeForAll()) {
			player.teleport(eventManager.getSpawn(this));
		} else {
			player.teleport(eventManager.getSpectator(this));
		}

		TaskUtil.runAsync(() -> {
			for (Player otherPlayer : getPlayers()) {
				Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
				otherProfile.handleVisibility(otherPlayer, player);
				profile.handleVisibility(player, otherPlayer);

				plugin.getNameTagHandler().reloadPlayer(player);
				plugin.getNameTagHandler().reloadOthersFor(player);
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

		if (this.isTeam()) {
			this.eventTeamPlayers.remove(player.getUniqueId());
		} else {
			this.eventPlayers.remove(player.getUniqueId());
		}

		this.onLeave(player);

		Profile profile = Profile.getByPlayer(player);

		TaskUtil.runAsync(() -> {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);

					plugin.getNameTagHandler().reloadPlayer(player);
					plugin.getNameTagHandler().reloadOthersFor(player);
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
					.replace("<event_prefix>", Event_Prefix)));
		}

		for (EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			final Player player = eventPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setEvent(null);
				profile.refreshHotbar();
				profile.teleportToSpawn();
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);
		getPlayers().stream().map(Profile::getByPlayer).forEach(Profile::handleVisibility);
	}

	public boolean canEnd() {
		int remaining = 0;

		for (EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			if (eventPlayer.getState() == EventPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public void refreshNametag() {
		this.getEventPlayers().values().forEach(eventPlayer -> {
			this.getPlugin().getNameTagHandler().reloadPlayer(eventPlayer.getPlayer());
			this.getPlugin().getNameTagHandler().reloadOthersFor(eventPlayer.getPlayer());
		});
	}

	public Player getWinner() {
		if (isTeam()) throw new IllegalArgumentException("You can't get a single winner from a Team Event!");

		for (EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
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
					.replace("<event_prefix>", Event_Prefix);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", this.getName()), "/event join");

			for ( Player player : Bukkit.getOnlinePlayers() ) {
				if ((isTeam() && !eventTeamPlayers.containsKey(player.getUniqueId())) || (!isTeam() && !eventPlayers.containsKey(player.getUniqueId()))) {
					message.sendToPlayer(player);
				}
			}
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : this.getPlayers()) {
			player.sendMessage(Event_Prefix + message);
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public String getDuration() {
		switch (getState()) {
			case ROUND_STARTING:
				return "00:00";
			case ROUND_FIGHTING:
				return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
			default:
				return "Ending";
		}
	}

	public void addSpectator(Player player) {
		this.getSpectators().add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setEvent(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		if (isFreeForAll()) {
			player.teleport(getEventManager().getSpawn(this));
		} else {
			player.teleport(getEventManager().getSpectator(this));
		}
	}

	public void removeSpectator(Player player) {
		this.getSpectators().remove(player.getUniqueId());
		if (isTeam()) {
			this.getEventTeamPlayers().remove(player.getUniqueId());
		} else {
			this.getEventPlayers().remove(player.getUniqueId());
		}

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setEvent(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}

	public EventGroup getWinningTeam() {
		for (EventGroup eventGroup : this.getTeams()) {
			if (eventGroup.getState() != EventPlayerState.ELIMINATED) {
				return eventGroup;
			}
		}
		return null;
	}

	public List<EventGroup> getTeams() {
		throw new IllegalArgumentException("You can't get a list of event groups from a solo event");
	}

	public EventPlayer getRoundPlayerA() {
		throw new IllegalArgumentException("Unable to get a EventPlayer from a Team Event");
	}

	public EventPlayer getRoundPlayerB() {
		throw new IllegalArgumentException("Unable to get a EventPlayer from a Team Event");
	}

	public EventGroup getRoundTeamA() {
		throw new IllegalArgumentException("You can't get a team from a solo event");
	}

	public EventGroup getRoundTeamB() {
		throw new IllegalArgumentException("You can't get a team from a solo event");
	}

	public boolean isFighting(EventGroup group) {
		return this.getRoundTeamA() != null && this.getRoundTeamA().equals(group) || this.getRoundTeamB() != null && this.getRoundTeamB().equals(group);
	}

	public void onJoin(Player player) {
		plugin.getKnockbackManager().knockback(player, this.getEventManager().getSumoKB());
	}

	public boolean isSumoSolo() {
		return this.getType().equals(EventType.SUMO_SOLO);
	}

	public boolean isSumoTeam() {
		return this.getType().equals(EventType.SUMO_TEAM);
	}

	public boolean isBracketsSolo() {
		return this.getType().equals(EventType.BRACKETS_SOLO);
	}

	public boolean isBracketsTeam() {
		return this.getType().equals(EventType.BRACKETS_TEAM);
	}

	public boolean isLMS() {
		return this.getType().equals(EventType.LMS);
	}

	public boolean isGulagSolo() {
		return this.getType().equals(EventType.GULAG_SOLO);
	}

	public boolean isGulagTeam() {
		return this.getType().equals(EventType.GULAG_TEAM);
	}

	public boolean isSpleef() {
		return this.getType().equals(EventType.SPLEEF);
	}

	public boolean isParkour() {
		return this.getType().equals(EventType.PARKOUR);
	}

	public abstract boolean isFreeForAll();

	public abstract boolean isTeam();

	public abstract void onLeave(Player player);

	public abstract void onRound();

	public abstract void onDeath(Player player);

	public abstract void handleStart();

	public abstract boolean isFighting(UUID uuid);

	public abstract ChatColor getRelationColor(Player viewer, Player target);

}

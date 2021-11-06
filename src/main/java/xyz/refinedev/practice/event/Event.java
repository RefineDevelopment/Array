package xyz.refinedev.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.meta.EventTask;
import xyz.refinedev.practice.event.meta.group.EventGroup;
import xyz.refinedev.practice.event.meta.group.EventTeamPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.event.meta.player.EventPlayerState;
import xyz.refinedev.practice.managers.EventManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.*;

import java.util.*;

@Getter @Setter
@RequiredArgsConstructor
public abstract class Event {

	private String Event_Prefix;

	private final Array plugin;
	private final EventManager eventManager;

	private final Map<UUID, EventPlayer> eventPlayers = new HashMap<>();
	private final Map<UUID, EventTeamPlayer> eventTeamPlayers = new HashMap<>();
	private final List<Entity> entities = new ArrayList<>();;
	private final List<UUID> spectators = new ArrayList<>();
	private final List<Item> droppedItems = new ArrayList<>();
	private final List<Location> placedBlocks = new ArrayList<>();
	private final List<BlockState> changedBlocks = new ArrayList<>();

	private final String name;
	private final PlayerSnapshot host;
	private final int maxPlayers;
	private final EventType type;
	private final EventTeamSize size;

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
			Player player = eventPlayer.getPlayer();
			if (player == null) continue;

			players.add(player);
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for ( EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			if (eventPlayer.getState() == EventPlayerState.WAITING) {
				Player player = eventPlayer.getPlayer();
				if (player == null) continue;

				players.add(player);
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		if (this.isTeam()) {
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

		Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
		profile.setEvent(this);
		profile.setState(ProfileState.IN_EVENT);

		this.plugin.getProfileManager().handleVisibility(profile);
		this.plugin.getProfileManager().refreshHotbar(profile);

		if (this.isFreeForAll()) {
			player.teleport(eventManager.getSpawn(this));
		} else {
			player.teleport(eventManager.getSpectator(this));
		}
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

		Profile profile = plugin.getProfileManager().getByPlayer(player);
		profile.setState(ProfileState.IN_LOBBY);
		profile.setEvent(null);

		this.plugin.getProfileManager().teleportToSpawn(profile);

		if (this.state == EventState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", getName())
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", getName()));

	}

	public void end() {
		this.plugin.getEventManager().setActiveEvent(null);
		this.plugin.getEventManager().setCooldown(new Cooldown(60_000L * 3));

		this.setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", getName()));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", getName())
					.replace("<event_prefix>", Event_Prefix)));
		}

		for (EventPlayer eventPlayer : this.isTeam() ? this.eventTeamPlayers.values() : this.isTeam() ? this.eventTeamPlayers.values() : this.eventPlayers.values()) {
			Player player = eventPlayer.getPlayer();
			if (player == null) return;

			Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
			profile.setState(ProfileState.IN_LOBBY);
			profile.setEvent(null);

			this.plugin.getProfileManager().teleportToSpawn(profile);
		}

		TaskUtil.run(this::cleanup);

		this.getSpectatorsList().forEach(this::removeSpectator);
		this.getPlayers().stream().map(plugin.getProfileManager()::getByPlayer).forEach(plugin.getProfileManager()::handleVisibility);
	}

	/**
	 * Clear up the {@link Event} leftovers and remnants
	 * and rollback the event arena to its original state
	 */
	public void cleanup() {
		this.placedBlocks.forEach(l -> l.getBlock().setType(Material.AIR));
		this.placedBlocks.clear();

		this.changedBlocks.forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
		this.changedBlocks.clear();

		this.entities.forEach(Entity::remove);
		this.droppedItems.forEach(Item::remove);
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

		Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
		profile.setEvent(this);
		profile.setState(ProfileState.SPECTATING);

		plugin.getProfileManager().refreshHotbar(profile);
		plugin.getProfileManager().handleVisibility(profile);

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

		Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
		profile.setEvent(null);
		profile.setState(ProfileState.IN_LOBBY);
		plugin.getProfileManager().teleportToSpawn(profile);
	}

	public boolean isSpectating(UUID uuid) {
		return this.spectators.contains(uuid);
	}

	public boolean isSumoSolo() {
		return this.getType().equals(EventType.SUMO) && this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isSumoTeam() {
		return this.getType().equals(EventType.SUMO) && !this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isBracketsSolo() {
		return this.getType().equals(EventType.BRACKETS) && this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isBracketsTeam() {
		return this.getType().equals(EventType.BRACKETS) && !this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isLMS() {
		return this.getType().equals(EventType.LMS);
	}

	public boolean isGulagSolo() {
		return this.getType().equals(EventType.GULAG) && this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isGulagTeam() {
		return this.getType().equals(EventType.GULAG) && !this.size.equals(EventTeamSize.SOLO);
	}

	public boolean isSpleef() {
		return this.getType().equals(EventType.SPLEEF);
	}

	public boolean isParkour() {
		return this.getType().equals(EventType.PARKOUR);
	}

	public boolean isOITC() {
		return this.getType().equals(EventType.OITC);
	}

	public boolean isFreeForAll() {
		return false;
	}

	public boolean isTeam() {
		return false;
	}

	public abstract void onJoin(Player player);

	public abstract void onLeave(Player player);

	public abstract void onRound();

	public abstract void onDeath(Player player);

	public abstract void handleStart();

	public abstract EventGroup getWinningTeam();

	public abstract List<EventGroup> getTeams();

	public abstract EventPlayer getRoundPlayerA();

	public abstract EventPlayer getRoundPlayerB();

	public abstract EventGroup getRoundTeamA();

	public abstract EventGroup getRoundTeamB();

	public abstract boolean isFighting(UUID uuid);

	public abstract boolean isFighting(EventGroup group);

	public abstract ChatColor getRelationColor(Player viewer, Player target);

}

package me.drizzy.practice.events.types.parkour;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.parkour.player.ParkourPlayer;
import me.drizzy.practice.events.types.parkour.player.ParkourPlayerState;
import me.drizzy.practice.events.types.parkour.task.ParkourRoundEndTask;
import me.drizzy.practice.events.types.parkour.task.ParkourRoundStartTask;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.other.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Parkour {

	@Getter	@Setter	private static boolean enabled = true;
	protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "Parkour");
	private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();
	
	private static Array plugin = Array.getInstance();
	
	private final LinkedHashMap<UUID, ParkourPlayer> eventPlayers = new LinkedHashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	
	private final String name;
	private final PlayerSnapshot host;
	private Cooldown cooldown;
	private ParkourTask eventTask;
	private ParkourState state = ParkourState.WAITING;

	@Getter @Setter public static int maxPlayers;
	private int totalPlayers;
	private long roundStart;


	public Parkour(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Parkour parkour = plugin.getParkourManager().getActiveParkour();

		toReturn.add(CC.MENU_BAR);
		if (parkour.isWaiting()) {

			String status;
			if (parkour.getCooldown() == null) {

				status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_WAITING")
						.replace("<parkour_host_name>", parkour.getName())
						.replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
						.replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			} else {
				String remaining=TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());
				if (remaining.startsWith("-")) {
					remaining="0.0";
				}
				String finalRemaining = remaining;

				status = CC.translate(config.getString("SCOREBOARD.EVENT.PARKOUR.STATUS_COUNTING")
						.replace("<parkour_host_name>", parkour.getName())
						.replace("<remaining>", finalRemaining)
						.replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
						.replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			}

			config.getStringList("SCOREBOARD.EVENT.PARKOUR.WAITING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<parkour_host_name>", parkour.getName())
					.replace("<status>", status)
					.replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
					.replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		} else {

			config.getStringList("SCOREBOARD.EVENT.PARKOUR.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<parkour_host_name>", parkour.getName())
					.replace("<parkour_duration>", parkour.getRoundDuration())
					.replace("<parkour_players_alive>", String.valueOf(parkour.getRemainingPlayers().size()))
					.replace("<parkour_player_count>", String.valueOf(parkour.getEventPlayers().size()))
					.replace("<parkour_max_players>", String.valueOf(Parkour.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(ParkourTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(plugin, 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == ParkourState.WAITING;
	}

	public boolean isFighting(Player player) {
		if (this.getState().equals(ParkourState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public ParkourPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
			Player player = parkourPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
			if (parkourPlayer.getState() == ParkourPlayerState.WAITING) {
				Player player = parkourPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		if (this.eventPlayers.size() >= maxPlayers) {
			player.sendMessage(Locale.EVENT_FULL.toString());
			return;
		}

		eventPlayers.put(player.getUniqueId(), new ParkourPlayer(player));

		broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", "Parkour")
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "Parkour"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(plugin.getParkourManager().getParkourSpawn());

		PlayerUtil.denyMovement(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
					NameTags.color(player, otherPlayer, plugin.getEssentials().getNametagMeta().getEventColor(), false);
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public void handleLeave(Player player) {
		eventPlayers.remove(player.getUniqueId());

		if (state == ParkourState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", "Parkour")
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", "Parkour"));

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
					NameTags.reset(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(plugin);

		profile.setState(ProfileState.IN_LOBBY);
		profile.setParkour(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();

		if (getRemainingPlayers().size() == 1) {
			handleWin(getRemainingPlayers().get(0));
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void end(Player winner) {
		plugin.getParkourManager().setActiveParkour(null);
		plugin.getParkourManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "Parkour"));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", "Parkour")
					.replace("<event_prefix>", EVENT_PREFIX)));
		}

		for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
			Player player = parkourPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setParkour(null);
				profile.refreshHotbar();
				profile.teleportToSpawn();
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
		for ( Player player : getPlayers() ) {
			Profile profile = Profile.getByPlayer(player);
			profile.getPlates().clear();
		}
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", "Parkour")
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "Parkour"), "/parkour join");

			for ( Player player : Bukkit.getOnlinePlayers() ) {
				if (!eventPlayers.containsKey(player.getUniqueId())) {
					message.sendToPlayer(player);
				}
			}
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : getPlayers()) {
			player.sendMessage(EVENT_PREFIX + CC.translate(message));
		}
	}

	@SuppressWarnings("unused")
	public void onJoin(Player player) {
	}

	@SuppressWarnings("unused")
	public void onLeave(Player player) {
	}

	public void onRound() {
		setState(ParkourState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(plugin.getParkourManager().getParkourSpawn());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInParkour()) {
					profile.refreshHotbar();
				}
			}
		}
		setEventTask(new ParkourRoundStartTask(this));
	}

	public void handleWin(Player player) {
		state = ParkourState.ROUND_ENDING;
		setEventTask(new ParkourRoundEndTask(this, player));
	}

	public String getRoundDuration() {
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
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(plugin.getParkourManager().getParkourSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
        eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}
}

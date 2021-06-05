package me.drizzy.practice.events.types.gulag;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Locale;
import me.drizzy.practice.hook.SpigotHook;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.gulag.player.GulagPlayer;
import me.drizzy.practice.events.types.gulag.player.GulagPlayerState;
import me.drizzy.practice.events.types.gulag.task.GulagRoundEndTask;
import me.drizzy.practice.events.types.gulag.task.GulagRoundStartTask;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.nametags.NameTagHandler;
import me.drizzy.practice.util.other.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Gulag {

	@Getter	@Setter	private static boolean enabled = true;
	protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "Gulag");
	private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

	private static Array plugin = Array.getInstance();
	
	private final LinkedHashMap<UUID, GulagPlayer> eventPlayers = new LinkedHashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	private final List<Entity> entities = new ArrayList<>();

	private final String name;
	private final PlayerSnapshot host;
	private Cooldown cooldown;
	private GulagPlayer roundPlayerA;
	private GulagPlayer roundPlayerB;
	private GulagTask eventTask;
	private GulagState state = GulagState.WAITING;

	@Getter @Setter public static int maxPlayers;
	private long roundStart;
	private int totalPlayers;


	public Gulag(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Gulag gulag = plugin.getGulagManager().getActiveGulag();

		toReturn.add(CC.MENU_BAR);
		if (gulag.isWaiting()) {

			String status;
			if (gulag.getCooldown() == null) {

				status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_WAITING")
						.replace("<gulag_host_name>", gulag.getName())
						.replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
						.replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			} else {
				String remaining=TimeUtil.millisToSeconds(gulag.getCooldown().getRemaining());
				if (remaining.startsWith("-")) {
					remaining="0.0";
				}
				String finalRemaining = remaining;

				status = CC.translate(config.getString("SCOREBOARD.EVENT.GULAG.STATUS_COUNTING")
						.replace("<gulag_host_name>", gulag.getName())
						.replace("<remaining>", finalRemaining)
						.replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
						.replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			}

			config.getStringList("SCOREBOARD.EVENT.GULAG.WAITING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<gulag_host_name>", gulag.getName())
					.replace("<status>", status)
					.replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
					.replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		} else {

			config.getStringList("SCOREBOARD.EVENT.GULAG.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<gulag_host_name>", gulag.getName())
					.replace("<gulag_duration>", gulag.getRoundDuration())
					.replace("<gulag_players_alive>", String.valueOf(gulag.getRemainingPlayers().size()))
					.replace("<gulag_playerA_name>", gulag.getRoundPlayerA().getUsername())
					.replace("<gulag_playerA_ping>", String.valueOf(gulag.getRoundPlayerA().getPing()))
					.replace("<gulag_playerB_name>", gulag.getRoundPlayerB().getUsername())
					.replace("<gulag_playerB_ping>", String.valueOf(gulag.getRoundPlayerB().getPing()))
					.replace("<gulag_player_count>", String.valueOf(gulag.getEventPlayers().size()))
					.replace("<gulag_max_players>", String.valueOf(Gulag.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(GulagTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(plugin, 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == GulagState.WAITING;
	}

	public boolean isFighting() {
		return state == GulagState.ROUND_FIGHTING;
	}

	public GulagPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			Player player = gulagPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			if (gulagPlayer.getState() == GulagPlayerState.WAITING) {
				Player player = gulagPlayer.getPlayer();
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

		eventPlayers.put(player.getUniqueId(), new GulagPlayer(player));

		broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", "Gulag")
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "Gulag"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setGulag(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(plugin.getGulagManager().getGulagSpectator());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);

					NameTagHandler.reloadPlayer(player);
					NameTagHandler.reloadOthersFor(player);
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public void handleLeave(Player player) {
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == GulagState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", "Gulag")
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", "Gulag"));

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);

					NameTagHandler.reloadPlayer(player);
					NameTagHandler.reloadOthersFor(player);
				}
			}
		}.runTaskAsynchronously(plugin);

		profile.setState(ProfileState.IN_LOBBY);
		profile.setGulag(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		GulagPlayer loser = getEventPlayer(player);
		loser.setState(GulagPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		plugin.getGulagManager().setActiveGulag(null);
		plugin.getGulagManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "Gulag"));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", "Gulag")
					.replace("<event_prefix>", EVENT_PREFIX)));
		}

		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			Player player = gulagPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setGulag(null);
				profile.refreshHotbar();
				profile.teleportToSpawn();
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public boolean canEnd() {
		int remaining = 0;

		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			if (gulagPlayer.getState() == GulagPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			if (gulagPlayer.getState() != GulagPlayerState.ELIMINATED) {
				return gulagPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", "Gulag")
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "Gulag"), "/gulag join");

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

	public void onJoin(Player player) {
		SpigotHook.getKnockbackType().applyKnockback(player, plugin.getGulagManager().getGulagKnockbackProfile());
	}
	public void onLeave(Player player) {
		SpigotHook.getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(GulagState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(plugin.getGulagManager().getGulagSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInGulag()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();

			if (player != null) {
				player.teleport(plugin.getGulagManager().getGulagSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInGulag()) {
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

		playerA.teleport(plugin.getGulagManager().getGulagSpawn1());
		playerA.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.GULAG_GUN));

		playerB.teleport(plugin.getGulagManager().getGulagSpawn2());
		playerB.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.GULAG_GUN));

		setEventTask(new GulagRoundStartTask(this));
	}

	public void onDeath(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		GulagPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(GulagPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage(Locale.EVENT_ELIMINATED.toString()
				.replace("<eliminated_name>", player.getName())
				.replace("<eliminator_name>", winner.getPlayer().getName()));

		setState(GulagState.ROUND_ENDING);
		setEventTask(new GulagRoundEndTask(this));

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(plugin);
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

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
	}

	private GulagPlayer findRoundPlayer() {
		GulagPlayer gulagPlayer= null;

		for ( GulagPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == GulagPlayerState.WAITING) {
				if (gulagPlayer == null) {
					gulagPlayer= check;
					continue;
				}

				if (check.getRoundWins() == 0) {
					gulagPlayer= check;
					continue;
				}

				if (check.getRoundWins() <= gulagPlayer.getRoundWins()) {
					gulagPlayer= check;
				}
			}
		}

		if (gulagPlayer == null) {
			throw new RuntimeException("Could not find a new round player");
		}

		return gulagPlayer;
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setGulag(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(plugin.getGulagManager().getGulagSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setGulag(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}
}

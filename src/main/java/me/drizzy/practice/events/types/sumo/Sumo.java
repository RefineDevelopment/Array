package me.drizzy.practice.events.types.sumo;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.sumo.task.SumoRoundEndTask;
import me.drizzy.practice.events.types.sumo.task.SumoRoundStartTask;
import me.drizzy.practice.events.types.sumo.task.SumoWaterTask;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.other.*;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.events.types.sumo.player.SumoPlayer;
import me.drizzy.practice.events.types.sumo.player.SumoPlayerState;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class Sumo {

	@Getter	@Setter	private static boolean enabled = true;
	protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "Sumo");
	private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

	private static Array plugin = Array.getInstance();

	private final LinkedHashMap<UUID, SumoPlayer> eventPlayers = new LinkedHashMap<>();
	private final List<UUID> spectators = new ArrayList<>();

	private final String name;
	private final PlayerSnapshot host;
	private Cooldown cooldown;
	private SumoPlayer roundPlayerA;
	private SumoPlayer roundPlayerB;
	private SumoTask eventTask;
	private BukkitRunnable waterTask;
	private SumoState state = SumoState.WAITING;
	
	@Getter @Setter public static int maxPlayers;
	private int totalPlayers;
	private long roundStart;

	public Sumo(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Sumo sumo = plugin.getSumoManager().getActiveSumo();

		toReturn.add(CC.MENU_BAR);
		if (sumo.isWaiting()) {

			String status;
			if (sumo.getCooldown() == null) {

				status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_WAITING")
						.replace("<sumo_host_name>", sumo.getName())
						.replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
						.replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			} else {
				String remaining=TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());
				if (remaining.startsWith("-")) {
					remaining="0.0";
				}
				String finalRemaining = remaining;

				status = CC.translate(config.getString("SCOREBOARD.EVENT.SUMO.STATUS_COUNTING")
						.replace("<sumo_host_name>", sumo.getName())
						.replace("<remaining>", finalRemaining)
						.replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
						.replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			}

			config.getStringList("SCOREBOARD.EVENT.SUMO.WAITING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<sumo_host_name>", sumo.getName())
					.replace("<status>", status)
					.replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
					.replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		} else {

			config.getStringList("SCOREBOARD.EVENT.SUMO.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<sumo_host_name>", sumo.getName())
					.replace("<sumo_duration>", sumo.getRoundDuration())
					.replace("<sumo_players_alive>", String.valueOf(sumo.getRemainingPlayers().size()))
					.replace("<sumo_playerA_name>", sumo.getRoundPlayerA().getUsername())
					.replace("<sumo_playerA_ping>", String.valueOf(sumo.getRoundPlayerA().getPing()))
					.replace("<sumo_playerB_name>", sumo.getRoundPlayerB().getUsername())
					.replace("<sumo_playerB_ping>", String.valueOf(sumo.getRoundPlayerB().getPing()))
					.replace("<sumo_player_count>", String.valueOf(sumo.getEventPlayers().size()))
					.replace("<sumo_max_players>", String.valueOf(Sumo.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(SumoTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(plugin, 0L, 20L);
		}
		waterTask = new SumoWaterTask(this);
		waterTask.runTaskTimer(plugin, 20L, 20L);
	}

	public boolean isWaiting() {
		return state == SumoState.WAITING;
	}

	public boolean isFighting() {
		return state == SumoState.ROUND_FIGHTING;
	}

	public SumoPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				Player player = sumoPlayer.getPlayer();
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

		eventPlayers.put(player.getUniqueId(), new SumoPlayer(player));

		broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", "Sumo")
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "Sumo"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(plugin.getSumoManager().getSumoSpectator());

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
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SumoState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", "Sumo")
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", "Sumo"));

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
		profile.setSpleef(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		SumoPlayer loser = getEventPlayer(player);
		loser.setState(SumoPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		plugin.getSumoManager().setActiveSumo(null);
		plugin.getSumoManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);
		waterTask.cancel();

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "Sumo"));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", "Sumo")
					.replace("<event_prefix>", EVENT_PREFIX)));
		}

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSumo(null);
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

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() != SumoPlayerState.ELIMINATED) {
				return sumoPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", "Sumo")
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "Sumo"), "/sumo join");

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
		plugin.getNMSManager().getKnockbackType().applyKnockback(player, plugin.getSumoManager().getSumoKnockbackProfile());
	}

	public void onLeave(Player player) {
		plugin.getNMSManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(SumoState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(plugin.getSumoManager().getSumoSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInSumo()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();

			if (player != null) {
				player.teleport(plugin.getSumoManager().getSumoSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInSumo()) {
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

		playerA.teleport(plugin.getSumoManager().getSumoSpawn1());
		playerB.teleport(plugin.getSumoManager().getSumoSpawn2());

		setEventTask(new SumoRoundStartTask(this));
	}

	public void onDeath(Player player) {
		SumoPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(SumoPlayerState.WAITING);
		winner.incrementRoundWins();
		winner.getPlayer().teleport(plugin.getSumoManager().getSumoSpectator());


		broadcastMessage(Locale.EVENT_ELIMINATED.toString()
				.replace("<eliminated_name>", player.getName())
				.replace("<eliminator_name>", winner.getPlayer().getName()));

		setState(SumoState.ROUND_ENDING);
		setEventTask(new SumoRoundEndTask(this));
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

	private SumoPlayer findRoundPlayer() {
		SumoPlayer sumoPlayer = null;

		for (SumoPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == SumoPlayerState.WAITING) {
				if (sumoPlayer == null) {
					sumoPlayer = check;
					continue;
				}

				if (check.getRoundWins() == 0) {
					sumoPlayer = check;
					continue;
				}

				if (check.getRoundWins() <= sumoPlayer.getRoundWins()) {
					sumoPlayer = check;
				}
			}
		}

		if (sumoPlayer == null) {
			throw new RuntimeException("Could not find a new round player");
		}

		return sumoPlayer;
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(plugin.getSumoManager().getSumoSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}

}

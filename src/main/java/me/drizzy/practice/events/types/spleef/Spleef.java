package me.drizzy.practice.events.types.spleef;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayer;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayerState;
import me.drizzy.practice.events.types.spleef.task.SpleefRoundEndTask;
import me.drizzy.practice.events.types.spleef.task.SpleefRoundStartTask;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.location.Circle;
import me.drizzy.practice.util.other.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Spleef {

	@Getter	@Setter	private static boolean enabled = true;
	protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "Spleef");
	private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

	private static Array plugin = Array.getInstance();
	
	private final LinkedHashMap<UUID, SpleefPlayer> eventPlayers = new LinkedHashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	private final List<Location> placedBlocks = new ArrayList<>();
	private final List<BlockState> changedBlocks = new ArrayList<>();
	private final List<Player> catcher = new ArrayList<>();
	
	private final String name;
	private final PlayerSnapshot host;
	private Cooldown cooldown;
	private SpleefTask eventTask;
	private SpleefState state = SpleefState.WAITING;
	
	@Getter @Setter public static int maxPlayers;
	private int totalPlayers;
	private long roundStart;


	public Spleef(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Spleef spleef = plugin.getSpleefManager().getActiveSpleef();

		toReturn.add(CC.MENU_BAR);
		if (spleef.isWaiting()) {

			String status;
			if (spleef.getCooldown() == null) {

				status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_WAITING")
						.replace("<spleef_host_name>", spleef.getName())
						.replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
						.replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			} else {
				String remaining=TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());
				if (remaining.startsWith("-")) {
					remaining="0.0";
				}
				String finalRemaining = remaining;

				status = CC.translate(config.getString("SCOREBOARD.EVENT.SPLEEF.STATUS_COUNTING")
						.replace("<spleef_host_name>", spleef.getName())
						.replace("<remaining>", finalRemaining)
						.replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
						.replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			}

			config.getStringList("SCOREBOARD.EVENT.SPLEEF.WAITING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<spleef_host_name>", spleef.getName())
					.replace("<status>", status)
					.replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
					.replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		} else {
			config.getStringList("SCOREBOARD.EVENT.SPLEEF.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<spleef_host_name>", spleef.getName())
					.replace("<spleef_duration>", spleef.getRoundDuration())
					.replace("<spleef_players_alive>", String.valueOf(spleef.getRemainingPlayers().size()))
					.replace("<spleef_player_count>", String.valueOf(spleef.getEventPlayers().size()))
					.replace("<spleef_max_players>", String.valueOf(Spleef.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(SpleefTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(plugin, 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == SpleefState.WAITING;
	}

	public boolean isFighting() {
		return state == SpleefState.ROUND_FIGHTING;
	}

	public boolean isFighting(Player player) {
		if (state.equals(SpleefState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public SpleefPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			Player player = spleefPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			if (spleefPlayer.getState() == SpleefPlayerState.WAITING) {
				Player player = spleefPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new SpleefPlayer(player));

		broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", "Spleef")
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "Spleef"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(plugin.getSpleefManager().getSpleefSpawn());

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
		if (state != SpleefState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SpleefState.WAITING) {
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
		profile.setSpleef(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		SpleefPlayer loser = getEventPlayer(player);
		loser.setState(SpleefPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		plugin.getSpleefManager().setActiveSpleef(null);
		plugin.getSpleefManager().setCooldown(new Cooldown(60_000L * 10));

		catcher.clear();

		setEventTask(null);

		new SpleefResetTask(this).runTask(plugin);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "Spleef"));
		} else {
			String win = Locale.EVENT_WON.toString().replace("<winner_name>", winner.getName())
					.replace("<event_name>", "Spleef")
					.replace("<event_prefix>", EVENT_PREFIX);

			Bukkit.broadcastMessage(win);
			Bukkit.broadcastMessage(win);
			Bukkit.broadcastMessage(win);
		}

		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			Player player = spleefPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSpleef(null);
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

		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			if (spleefPlayer.getState() == SpleefPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining <= 1;
	}

	public Player getWinner() {
		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			if (spleefPlayer.getState() != SpleefPlayerState.ELIMINATED) {
				return spleefPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", "Spleef")
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "Spleef"), "/spleef join");

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
		plugin.getNMSManager().getKnockbackType().applyKnockback(player, plugin.getSpleefManager().getSpleefKnockbackProfile());
	}
	public void onLeave(Player player) {
		plugin.getNMSManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(SpleefState.ROUND_STARTING);

		int i = 0;
		for (Player player : this.getRemainingPlayers()) {

			Location midSpawn = plugin.getSpleefManager().getSpleefSpawn();

			List<Location> circleLocations = Circle.getCircle(midSpawn, plugin.getEssentials().getMeta().getFfaSpawnRadius(), this.getPlayers().size());

			Location center = midSpawn.clone();
			Location loc = circleLocations.get(i);
			Location target = loc.setDirection(center.subtract(loc).toVector());

			player.teleport(target.add(0, 0.5, 0));
			circleLocations.remove(i);
			i++;

			player.getInventory().addItem(Hotbar.getItems().get(HotbarType.SPLEEF_MATCH));
		}
		setEventTask(new SpleefRoundStartTask(this));
	}

	public void onDeath(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		broadcastMessage(Locale.EVENT_DIED.toString()
				.replace("<eliminated_name>", player.getName()));


		if (canEnd()) {
			setState(SpleefState.ROUND_ENDING);
			setEventTask(new SpleefRoundEndTask(this));
		}

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
		profile.refreshHotbar();
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
		profile.setSpleef(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(plugin.getSpleefManager().getSpleefSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}
}

package me.drizzy.practice.events.types.brackets;

import me.drizzy.practice.Locale;
import me.drizzy.practice.events.types.brackets.player.BracketsPlayer;
import me.drizzy.practice.events.types.brackets.player.BracketsPlayerState;
import me.drizzy.practice.events.types.brackets.task.BracketsRoundEndTask;
import me.drizzy.practice.events.types.brackets.task.BracketsRoundStartTask;
import me.drizzy.practice.hook.SpigotHook;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.Array;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.nametags.NameTagHandler;
import me.drizzy.practice.util.other.*;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter @Setter
public class Brackets {

	@Getter @Setter private static boolean enabled = true;
	protected static String EVENT_PREFIX = Locale.EVENT_PREFIX.toString().replace("<event_name>", "Brackets");
	private static BasicConfigurationFile config = Array.getInstance().getScoreboardConfig();

	private static Array plugin = Array.getInstance();
	
	private final LinkedHashMap<UUID, BracketsPlayer> eventPlayers = new LinkedHashMap<>();
	private final List<UUID> spectators = new ArrayList<>();
	private final List<Location> placedBlocks = new ArrayList<>();
	private final List<Entity> entities = new ArrayList<>();
	
	private final String name;
	private final PlayerSnapshot host;
	private Kit kit;
	private Cooldown cooldown;
	private BracketsPlayer roundPlayerA;
	private BracketsPlayer roundPlayerB;
	private BracketsTask eventTask;
	private BracketsState state = BracketsState.WAITING;

	@Getter public static int maxPlayers;
	private int totalPlayers;
	private long roundStart;

	public Brackets(Player player, Kit kit) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.kit = kit;
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Brackets brackets = plugin.getBracketsManager().getActiveBrackets();

		toReturn.add(CC.MENU_BAR);
		if (brackets.isWaiting()) {

			String status;
			if (brackets.getCooldown() == null) {

				status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_WAITING")
						.replace("<brackets_host_name>", brackets.getName())
						.replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
						.replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			} else {
				String remaining=TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());
				if (remaining.startsWith("-")) {
					remaining="0.0";
				}
				String finalRemaining = remaining;

				status = CC.translate(config.getString("SCOREBOARD.EVENT.BRACKETS.STATUS_COUNTING")
						.replace("<brackets_host_name>", brackets.getName())
						.replace("<remaining>", finalRemaining)
						.replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
						.replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃");

			}

			config.getStringList("SCOREBOARD.EVENT.BRACKETS.WAITING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<brackets_host_name>", brackets.getName())
					.replace("<status>", status)
					.replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
					.replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));

		} else {

			config.getStringList("SCOREBOARD.EVENT.BRACKETS.FIGHTING").forEach(line -> toReturn.add(CC.translate(line
					.replace("<brackets_host_name>", brackets.getName())
					.replace("<brackets_duration>", brackets.getRoundDuration())
					.replace("<brackets_players_alive>", String.valueOf(brackets.getRemainingPlayers().size()))
					.replace("<brackets_playerA_name>", brackets.getRoundPlayerA().getUsername())
					.replace("<brackets_playerA_ping>", String.valueOf(brackets.getRoundPlayerA().getPing()))
					.replace("<brackets_playerB_name>", brackets.getRoundPlayerB().getUsername())
					.replace("<brackets_playerB_ping>", String.valueOf(brackets.getRoundPlayerB().getPing()))
					.replace("<brackets_player_count>", String.valueOf(brackets.getEventPlayers().size()))
					.replace("<brackets_max_players>", String.valueOf(Brackets.getMaxPlayers()))).replace("%splitter%", "┃").replace("|", "┃")));
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(BracketsTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(plugin, 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == BracketsState.WAITING;
	}

	public boolean isFighting() {
		return state == BracketsState.ROUND_FIGHTING;
	}

	public BracketsPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			Player player = bracketsPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			if (bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
				Player player = bracketsPlayer.getPlayer();
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

		eventPlayers.put(player.getUniqueId(), new BracketsPlayer(player));

		broadcastMessage(Locale.EVENT_JOIN.toString()
				.replace("<event_name>", "Brackets")
				.replace("<joined>", player.getName())
				.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
				.replace("<event_max_players>", String.valueOf(getMaxPlayers())));

		player.sendMessage(Locale.EVENT_PLAYER_JOIN.toString().replace("<event_name>", "Brackets"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();
		player.teleport(plugin.getBracketsManager().getBracketsSpectator());

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

	public void handleLeave(Player player) {
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == BracketsState.WAITING) {
			broadcastMessage(Locale.EVENT_LEAVE.toString()
					.replace("<event_name>", "Brackets")
					.replace("<left>", player.getName())
					.replace("<event_participants_size>", String.valueOf(getRemainingPlayers().size()))
					.replace("<event_max_players>", String.valueOf(getMaxPlayers())));
		}
		player.sendMessage(Locale.EVENT_PLAYER_LEAVE.toString().replace("<event_name>", "Brackets"));


		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());

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

		profile.setState(ProfileState.IN_LOBBY);
		profile.setBrackets(null);
		profile.refreshHotbar();
		profile.teleportToSpawn();
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		BracketsPlayer loser = getEventPlayer(player);
		loser.setState(BracketsPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		plugin.getBracketsManager().setActiveBrackets(null);
		plugin.getBracketsManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(Locale.EVENT_CANCELLED.toString().replace("<event_name>", "Brackets"));
		} else {
			Locale.EVENT_WON.toList().forEach(line -> Bukkit.broadcastMessage(line
					.replace("<winner>", winner.getName())
					.replace("<event_name>", "Brackets")
					.replace("<event_prefix>", EVENT_PREFIX)));

		}

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			Player player = bracketsPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setBrackets(null);
				profile.refreshHotbar();
				profile.teleportToSpawn();

				NameTagHandler.reloadPlayer(player);
				NameTagHandler.reloadOthersFor(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public boolean canEnd() {
		int remaining = 0;

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			if (bracketsPlayer.getState() == BracketsPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			if (bracketsPlayer.getState() != BracketsPlayerState.ELIMINATED) {
				return bracketsPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		for ( String string : Locale.EVENT_ANNOUNCE.toList() ) {
			String main = string
					.replace("<event_name>", "Brackets")
					.replace("<event_host>", this.getHost().getUsername())
					.replace("<event_prefix>", EVENT_PREFIX);

			Clickable message = new Clickable(main, Locale.EVENT_HOVER.toString().replace("<event_name>", "Brackets"), "/brackets join");

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
		SpigotHook.getKnockbackType().applyKnockback(player, plugin.getBracketsManager().getBracketsKnockbackProfile());
	}
	public void onLeave(Player player) {
		SpigotHook.getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(BracketsState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(plugin.getBracketsManager().getBracketsSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInBrackets()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();

			if (player != null) {
				player.teleport(plugin.getBracketsManager().getBracketsSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInBrackets()) {
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

		playerA.teleport(plugin.getBracketsManager().getBracketsSpawn1());
		playerA.getInventory().setContents(kit.getKitInventory().getContents());
		playerA.getInventory().setArmorContents(kit.getKitInventory().getArmor());

		playerB.teleport(plugin.getBracketsManager().getBracketsSpawn2());
		playerB.getInventory().setContents(kit.getKitInventory().getContents());
		playerB.getInventory().setArmorContents(kit.getKitInventory().getArmor());

		setEventTask(new BracketsRoundStartTask(this));
	}

	public void onDeath(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		BracketsPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(BracketsPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage(Locale.EVENT_ELIMINATED.toString()
				        .replace("<eliminated_name>", player.getName())
				        .replace("<eliminator_name>", winner.getPlayer().getName()));

		player.setFireTicks(0);
		setState(BracketsState.ROUND_ENDING);
		setEventTask(new BracketsRoundEndTask(this));

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

	public int getMaxBuildHeight() {
		int highest = (int) (Math.max(plugin.getBracketsManager().getBracketsSpawn1().getY(), plugin.getBracketsManager().getBracketsSpawn2().getY()));
		return highest + 5;
	}

	private BracketsPlayer findRoundPlayer() {
		BracketsPlayer bracketsPlayer = null;

		for (BracketsPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == BracketsPlayerState.WAITING) {
				if (bracketsPlayer == null) {
					bracketsPlayer = check;
					continue;
				}

				if (check.getRoundWins() == 0) {
					bracketsPlayer = check;
					continue;
				}

				if (check.getRoundWins() <= bracketsPlayer.getRoundWins()) {
					bracketsPlayer = check;
				}
			}
		}

		if (bracketsPlayer == null) {
			throw new RuntimeException("Could not find a new round player");
		}

		return bracketsPlayer;
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(this);
		profile.setState(ProfileState.SPECTATING);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(plugin.getBracketsManager().getBracketsSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		eventPlayers.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();
		profile.teleportToSpawn();
	}
}

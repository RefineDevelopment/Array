package me.drizzy.practice.event.types.parkour;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.util.chat.Clickable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.parkour.player.ParkourPlayer;
import me.drizzy.practice.event.types.parkour.player.ParkourPlayerState;
import me.drizzy.practice.event.types.parkour.task.ParkourRoundEndTask;
import me.drizzy.practice.event.types.parkour.task.ParkourRoundStartTask;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Parkour {

	protected static String EVENT_PREFIX =CC.translate("&8[&bParkour&8] &r");

	private final String name;
	@Setter private ParkourState state = ParkourState.WAITING;
	private ParkourTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, ParkourPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private final List<UUID> spectators = new ArrayList<>();
	@Getter	@Setter	public static int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;
	@Getter	@Setter	private static boolean enabled = true;


	public Parkour(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Parkour parkour = Array.getInstance().getParkourManager().getActiveParkour();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + parkour.getName()));

		if (parkour.isWaiting()) {
			toReturn.add("&bPlayers: &r" + parkour.getEventPlayers().size() + "/" + getMaxPlayers());
			toReturn.add("");

			if (parkour.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(parkour.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bPlayers: &r" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
			toReturn.add("&bDuration: &r" + parkour.getRoundDuration());
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
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
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
			player.sendMessage(CC.RED + "The event is full");
			return;
		}

		eventPlayers.put(player.getUniqueId(), new ParkourPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bParkour Event! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bParkour Event&8!"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());

		PlayerUtil.denyMovement(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(Array.getInstance());
	}

	public void handleLeave(Player player) {
		eventPlayers.remove(player.getUniqueId());

		if (state == ParkourState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bParkour Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bParkour Event&8!"));
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setParkour(null);
		profile.refreshHotbar();

		Essentials.teleportToSpawn(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(Array.getInstance());

		if (getRemainingPlayers().size() == 1) {
			handleWin(getRemainingPlayers().get(0));
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void end(Player winner) {
		Array.getInstance().getParkourManager().setActiveParkour(null);
		Array.getInstance().getParkourManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The parkour event has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Parkour Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Parkour Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Parkour Event" + CC.GRAY + "!");
		}

		for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
			Player player = parkourPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setParkour(null);
				profile.refreshHotbar();

				Essentials.teleportToSpawn(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
		for ( Player player : getPlayers() ) {
			Profile profile = Profile.getByUuid(player);
			profile.getPlates().clear();
		}
	}

	public void announce() {
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&b&l[Parkour Event]"));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&fA &bParkour &fevent is being hosted by &b" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Parkour event", "/parkour join");
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
	}

	public void onLeave(Player player) {
		//player.setKnockbackProfile(null);
	}

	public void onRound() {
		setState(ParkourState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInParkour()) {
					profile.refreshHotbar();
				}
				player.setHealth(20.0D);
				player.setSaturation(20.0F);
				player.setFallDistance(0.0F);
				player.setFoodLevel(20);
				player.setFireTicks(0);
				player.setMaximumNoDamageTicks(20);
				player.setExp(0.0F);
				player.setLevel(0);
				player.setAllowFlight(false);
				player.setFlying(false);
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
		setEventTask(new ParkourRoundStartTask(this));
	}

	public void handleWin(Player player) {
		setState(ParkourState.ROUND_ENDING);
		setEventTask(new ParkourRoundEndTask(this, player));
	}

	public String getRoundDuration() {
		if (getState() == ParkourState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == ParkourState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		player.setFlying(true);

		player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Essentials.teleportToSpawn(player);
	}
}

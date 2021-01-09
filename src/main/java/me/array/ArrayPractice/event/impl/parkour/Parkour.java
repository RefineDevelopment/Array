package me.array.ArrayPractice.event.impl.parkour;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayer;
import me.array.ArrayPractice.event.impl.parkour.player.ParkourPlayerState;
import me.array.ArrayPractice.event.impl.parkour.task.ParkourRoundEndTask;
import me.array.ArrayPractice.event.impl.parkour.task.ParkourRoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Parkour {

	protected static String EVENT_PREFIX = CC.AQUA + CC.BOLD + "(Parkour) " + CC.RESET;

	private String name;
	@Setter private ParkourState state = ParkourState.WAITING;
	private ParkourTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, ParkourPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;


	public Parkour(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Parkour parkour = Practice.getInstance().getParkourManager().getActiveParkour();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + parkour.getName()));

		if (parkour.isWaiting()) {
			toReturn.add("&bPlayers: &r" + parkour.getEventPlayers().size() + "/" + parkour.getMaxPlayers());
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
			toReturn.add("&bRemaining: &r" + parkour.getRemainingPlayers().size() + "/" + parkour.getTotalPlayers());
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
			eventTask.runTaskTimer(Practice.getInstance(), 0L, 20L);
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
		eventPlayers.put(player.getUniqueId(), new ParkourPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the parkour " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Practice.getInstance().getParkourManager().getParkourSpawn());

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
		}.runTaskAsynchronously(Practice.getInstance());
	}

	public void handleLeave(Player player) {
		eventPlayers.remove(player.getUniqueId());

		if (state == ParkourState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the parkour " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setParkour(null);
		profile.refreshHotbar();

		Practice.getInstance().getEssentials().teleportToSpawn(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(Practice.getInstance());

		if (getRemainingPlayers().size() == 1) {
			handleWin(getRemainingPlayers().get(0));
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void end(Player winner) {
		Practice.getInstance().getParkourManager().setActiveParkour(null);
		Practice.getInstance().getParkourManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The parkour has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the parkour!");
		}

		for (ParkourPlayer parkourPlayer : eventPlayers.values()) {
			Player player = parkourPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setParkour(null);
				profile.refreshHotbar();

				Practice.getInstance().getEssentials().teleportToSpawn(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public void announce() {
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Parkour " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the parkour.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/parkour join"))
				.create();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!eventPlayers.containsKey(player.getUniqueId())) {
				player.sendMessage("");
				player.spigot().sendMessage(components);
				player.sendMessage("");
			}
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : getPlayers()) {
			player.sendMessage(EVENT_PREFIX + CC.translate(message));
		}
	}

	public void onJoin(Player player) {}

	public void onLeave(Player player) {
		//player.setKnockbackProfile(null);
	}

	public void onRound() {
		setState(ParkourState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Practice.getInstance().getParkourManager().getParkourSpawn());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInParkour()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);
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

		player.teleport(Practice.getInstance().getParkourManager().getParkourSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setParkour(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Practice.getInstance().getEssentials().teleportToSpawn(player);
	}
}

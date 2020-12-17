package me.array.ArrayPractice.event.impl.wipeout;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.wipeout.player.WipeoutPlayer;
import me.array.ArrayPractice.event.impl.wipeout.player.WipeoutPlayerState;
import me.array.ArrayPractice.event.impl.wipeout.task.WipeoutRoundEndTask;
import me.array.ArrayPractice.event.impl.wipeout.task.WipeoutRoundStartTask;
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
public class Wipeout {

	protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Wipeout) " + CC.RESET;

	private String name;
	@Setter private WipeoutState state = WipeoutState.WAITING;
	private WipeoutTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, WipeoutPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;


	public Wipeout(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Wipeout wipeout = Array.get().getWipeoutManager().getActiveWipeout();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + wipeout.getName()));

		if (wipeout.isWaiting()) {
			toReturn.add("&bPlayers: &r" + wipeout.getEventPlayers().size() + "/" + wipeout.getMaxPlayers());
			toReturn.add("");

			if (wipeout.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(wipeout.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bRemaining: &r" + wipeout.getRemainingPlayers().size() + "/" + wipeout.getTotalPlayers());
			toReturn.add("&bDuration: &r" + wipeout.getRoundDuration());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(WipeoutTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == WipeoutState.WAITING;
	}

	public boolean isFighting(Player player) {
		if (state.equals(WipeoutState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public WipeoutPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
			Player player = wipeoutPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
			if (wipeoutPlayer.getState() == WipeoutPlayerState.WAITING) {
				Player player = wipeoutPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new WipeoutPlayer(player));
		getRemainingPlayers().remove(player);

		broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the wipeout " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWipeout(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getWipeoutManager().getWipeoutSpawn());

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
		}.runTaskAsynchronously(Array.get());
	}

	public void handleLeave(Player player) {
		eventPlayers.remove(player.getUniqueId());

		if (state == WipeoutState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the wipeout " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setWipeout(null);
		profile.refreshHotbar();

		Array.get().getEssentials().teleportToSpawn(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTaskAsynchronously(Array.get());

		if (getRemainingPlayers().size() == 1) {
			handleWin(getRemainingPlayers().get(0));
		}
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void end(Player winner) {
		Array.get().getWipeoutManager().setActiveWipeout(null);
		Array.get().getWipeoutManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The wipeout has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the wipeout!");
		}

		for (WipeoutPlayer wipeoutPlayer : eventPlayers.values()) {
			Player player = wipeoutPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setWipeout(null);
				profile.refreshHotbar();

				Array.get().getEssentials().teleportToSpawn(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public void announce() {
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Wipeout " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the wipeout.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wipeout join"))
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

	public void onLeave(Player player) {}

	public void onRound() {
		setState(WipeoutState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Array.get().getWipeoutManager().getWipeoutSpawn());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInWipeout()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);
			}
		}
		setEventTask(new WipeoutRoundStartTask(this));
	}

	public void handleWin(Player player) {
		setState(WipeoutState.ROUND_ENDING);
		setEventTask(new WipeoutRoundEndTask(this, player));
	}

	public String getRoundDuration() {
		if (getState() == WipeoutState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == WipeoutState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWipeout(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(Array.get().getWipeoutManager().getWipeoutSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWipeout(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

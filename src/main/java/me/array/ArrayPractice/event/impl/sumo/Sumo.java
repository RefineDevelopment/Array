package me.array.ArrayPractice.event.impl.sumo;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayer;
import me.array.ArrayPractice.event.impl.sumo.player.SumoPlayerState;
import me.array.ArrayPractice.event.impl.sumo.task.SumoRoundEndTask;
import me.array.ArrayPractice.event.impl.sumo.task.SumoRoundStartTask;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

@Getter
public class Sumo {

	protected static String EVENT_PREFIX = CC.AQUA + CC.BOLD + "(Sumo) " + CC.RESET;

	private final String name;
	@Setter private SumoState state = SumoState.WAITING;
	private SumoTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, SumoPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private final List<UUID> spectators = new ArrayList<>();
	private final int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	private SumoPlayer roundPlayerA;
	private SumoPlayer roundPlayerB;
	@Setter
	private long roundStart;


	public Sumo(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Sumo sumo = Practice.getInstance().getSumoManager().getActiveSumo();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&e&lHost: &r" + sumo.getName()));

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + sumo.getName()));
		toReturn.add(CC.translate("&bKit: &f" + "Sumo"));

		if (sumo.isWaiting()) {
			toReturn.add("&bPlayers: &r" + sumo.getEventPlayers().size() + "/" + sumo.getMaxPlayers());
			toReturn.add("");

			if (sumo.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(sumo.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bRemaining: &r" + sumo.getRemainingPlayers().size() + "/" + sumo.getTotalPlayers());
			toReturn.add("&bDuration: &r" + sumo.getRoundDuration());
			toReturn.add("");
			toReturn.add("&b" + sumo.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&b" + sumo.getRoundPlayerB().getUsername());
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
			eventTask.runTaskTimer(Practice.getInstance(), 0L, 20L);
		}
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
		eventPlayers.put(player.getUniqueId(), new SumoPlayer(player));

		broadcastMessage(CC.GREEN + Practice.getInstance().getCoreHook().getPlayerPrefix(player) + player.getName() + CC.YELLOW + " joined the sumo " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Practice.getInstance().getSumoManager().getSumoSpectator());

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
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SumoState.WAITING) {
			broadcastMessage(CC.RED + player.getName() + CC.YELLOW + " left the sumo " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setSumo(null);
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
		Practice.getInstance().getSumoManager().setActiveSumo(null);
		Practice.getInstance().getSumoManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The sumo has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + CC.BOLD + winner.getName() + CC.WHITE + " has won the sumo event!");
		}

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSumo(null);
				profile.refreshHotbar();

				Practice.getInstance().getEssentials().teleportToSpawn(player);
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
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting a Sumo Event" + CC.GREEN + " (Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the sumo.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sumo join"))
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

	public void onJoin(Player player) {
	KnockbackProfile knockbackProfile =KnockbackModule.INSTANCE.profiles.get("Sumo");
	((CraftPlayer)player).getHandle().setKnockback(knockbackProfile);
	}

	public void onLeave(Player player) {
		KnockbackProfile knockbackProfile =KnockbackModule.INSTANCE.profiles.get("strafe");
		((CraftPlayer)player).getHandle().setKnockback(knockbackProfile);
	}

	public void onRound() {
		setState(SumoState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Practice.getInstance().getSumoManager().getSumoSpectator());

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
				player.teleport(Practice.getInstance().getSumoManager().getSumoSpectator());

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

		playerA.teleport(Practice.getInstance().getSumoManager().getSumoSpawn1());
		playerB.teleport(Practice.getInstance().getSumoManager().getSumoSpawn2());

		setEventTask(new SumoRoundStartTask(this));
	}

	public void onDeath(Player player) {
		SumoPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(SumoPlayerState.WAITING);
		winner.incrementRoundWins();
		winner.getPlayer().teleport(Practice.getInstance().getSumoManager().getSumoSpectator());

		broadcastMessage("&c" + player.getName() + "&e was eliminated by &a" + winner.getUsername() + "&e!");

		setState(SumoState.ROUND_ENDING);
		setEventTask(new SumoRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == SumoState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == SumoState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
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
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		player.setFlying(true);
		player.teleport(Practice.getInstance().getSumoManager().getSumoSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSumo(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Practice.getInstance().getEssentials().teleportToSpawn(player);
	}

}

package me.drizzy.practice.events.types.gulag;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.Array;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.events.types.gulag.player.GulagPlayer;
import me.drizzy.practice.events.types.gulag.player.GulagPlayerState;
import me.drizzy.practice.events.types.gulag.task.GulagRoundEndTask;
import me.drizzy.practice.events.types.gulag.task.GulagRoundStartTask;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Gulag {

	protected static String EVENT_PREFIX=CC.translate("&8[&bGulag&8] &r");

	private final String name;
	@Setter private GulagState state=GulagState.WAITING;
	private GulagTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, GulagPlayer> eventPlayers=new LinkedHashMap<>();
	@Getter private final List<UUID> spectators=new ArrayList<>();
	@Getter @Setter	public static int maxPlayers;
	@Getter	@Setter	private int totalPlayers;
	@Setter	private Cooldown cooldown;
	private final List<Entity> entities=new ArrayList<>();
	private GulagPlayer roundPlayerA;
	private GulagPlayer roundPlayerB;
	@Setter	private long roundStart;
	@Getter	@Setter	private static boolean enabled = true;


	public Gulag(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		Gulag.maxPlayers=100;

	}
	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Gulag gulag= Array.getInstance().getGulagManager().getActiveGulag();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + gulag.getName()));
		if (gulag.isWaiting()) {
			toReturn.add("&bPlayers: &r" + gulag.getEventPlayers().size() + "/" + Gulag.getMaxPlayers());
			toReturn.add("");

			if (gulag.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(gulag.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bPlayers: &r" + gulag.getRemainingPlayers().size() + "/" + gulag.getTotalPlayers());
			toReturn.add("&bDuration: &r" + gulag.getRoundDuration());
			toReturn.add("");
			toReturn.add("&a" + gulag.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&c" + gulag.getRoundPlayerB().getUsername());
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
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
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
			player.sendMessage(CC.RED + "The events is full");
			return;
		}

		eventPlayers.put(player.getUniqueId(), new GulagPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bGulag Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bGulag Event&8!"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setGulag(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getGulagManager().getGulagSpectator());

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
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == GulagState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bGulag Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bGulag Event&8!"));
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setGulag(null);
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
		Array.getInstance().getGulagManager().setActiveGulag(null);
		Array.getInstance().getGulagManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The Gulag events has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Gulag Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Gulag Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Gulag Event" + CC.GRAY + "!");
		}

		for ( GulagPlayer gulagPlayer : eventPlayers.values()) {
			Player player = gulagPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setGulag(null);
				profile.refreshHotbar();

				Essentials.teleportToSpawn(player);
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
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&b&l[Gulag Event]"));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&fA &bGulag &fevent is being hosted by &b" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Gulag events", "/gulag join");
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
		Profile.setKb(player, Array.getInstance().getGulagManager().getGulagKnockbackProfile());
	}
	public void onLeave(Player player) {
		Array.getInstance().getNMSManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(GulagState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getGulagManager().getGulagSpectator());

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
				player.teleport(Array.getInstance().getGulagManager().getGulagSpectator());

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

		playerA.teleport(Array.getInstance().getGulagManager().getGulagSpawn1());
		playerA.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.GULAG_GUN));
		playerB.teleport(Array.getInstance().getGulagManager().getGulagSpawn2());
		playerB.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.GULAG_GUN));
		setEventTask(new GulagRoundStartTask(this));
	}

	public void onDeath(Player player) {
		GulagPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(GulagPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + winner.getUsername() + "&7!");
		player.setFireTicks(0);
		winner.getPlayer().hidePlayer(player);
		setState(GulagState.ROUND_ENDING);
		setEventTask(new GulagRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == GulagState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == GulagState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
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
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		PlayerUtil.spectator(player);
		player.setFlying(true);

		player.teleport(Array.getInstance().getGulagManager().getGulagSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
        PlayerUtil.reset(player);
		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setGulag(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Essentials.teleportToSpawn(player);
	}
}

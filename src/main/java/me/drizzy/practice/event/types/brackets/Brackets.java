package me.drizzy.practice.event.types.brackets;

import me.drizzy.practice.event.types.brackets.player.BracketsPlayer;
import me.drizzy.practice.event.types.brackets.player.BracketsPlayerState;
import me.drizzy.practice.event.types.brackets.task.BracketsRoundEndTask;
import me.drizzy.practice.event.types.brackets.task.BracketsRoundStartTask;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.Array;
import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.PlayerSnapshot;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Brackets {

	protected static String EVENT_PREFIX=CC.translate("&8[&bBrackets&8] &r");
	private final String name;
	@Setter	private BracketsState state=BracketsState.WAITING;
	@Getter	@Setter	private Kit kit;
	private BracketsTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, BracketsPlayer> eventPlayers=new LinkedHashMap<>();
	@Getter	private final List<UUID> spectators=new ArrayList<>();
	@Getter	@Setter	public static int maxPlayers;
	@Getter	@Setter	private int totalPlayers;
	@Setter private Cooldown cooldown;
	private final List<Entity> entities=new ArrayList<>();
	private BracketsPlayer roundPlayerA;
	private BracketsPlayer roundPlayerB;
	@Setter private long roundStart;
	@Getter @Setter private static boolean enabled = true;


	public Brackets(Player player, Kit kit) {
		this.name=player.getName();
		this.host=new PlayerSnapshot(player.getUniqueId(), player.getName());
		Brackets.maxPlayers=100;
		this.kit=kit;
}
	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Brackets brackets = Array.getInstance().getBracketsManager().getActiveBrackets();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + brackets.getName()));
		toReturn.add(CC.translate("&bKit: &r" + kit.getName()));

		if (brackets.isWaiting()) {
			toReturn.add("&bPlayers: &r" + brackets.getEventPlayers().size() + "/" + Brackets.getMaxPlayers());
			toReturn.add("");

			if (brackets.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(brackets.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bPlayers: &r" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
			toReturn.add("&bDuration: &r" + brackets.getRoundDuration());
			toReturn.add("");
			toReturn.add("&a" + brackets.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&c" + brackets.getRoundPlayerB().getUsername());
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
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
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
			player.sendMessage(CC.RED + "The event is full");
			return;
		}

		eventPlayers.put(player.getUniqueId(), new BracketsPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bBrackets Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bBrackets Event&8!"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();
        Array.getInstance().getKnockbackManager().getKnockbackType().appleKitKnockback(player, getKit());
		player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());

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

		if (state == BracketsState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bBrackets Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bBrackets Event&8!"));
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setBrackets(null);
		profile.refreshHotbar();
		Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);
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
		BracketsPlayer loser = getEventPlayer(player);
		loser.setState(BracketsPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		Array.getInstance().getBracketsManager().setActiveBrackets(null);
		Array.getInstance().getBracketsManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The brackets event has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Brackets Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Brackets Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Brackets Event" + CC.GRAY + "!");
		}

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			Player player = bracketsPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setBrackets(null);
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
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&b&l[Brackets Event]"));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&fA &bBrackets &fevent is being hosted by &b" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Brackets Event", "/brackets join");
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
		Array.getInstance().getKnockbackManager().getKnockbackType().applyKnockback(player, Array.getInstance().getBracketsManager().getBracketsKnockbackProfile());
	}
	public void onLeave(Player player) {
		Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(BracketsState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());

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
				player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());

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

		playerA.teleport(Array.getInstance().getBracketsManager().getBracketsSpawn1());
		playerA.getInventory().setContents(getKit().getKitInventory().getContents());
		playerA.getInventory().setArmorContents(getKit().getKitInventory().getArmor());
		playerB.teleport(Array.getInstance().getBracketsManager().getBracketsSpawn2());
		playerB.getInventory().setContents(getKit().getKitInventory().getContents());
		playerB.getInventory().setArmorContents(getKit().getKitInventory().getArmor());
		setEventTask(new BracketsRoundStartTask(this));
	}

	public void onDeath(Player player) {
		BracketsPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(BracketsPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + winner.getUsername() + "&7!");
		player.setFireTicks(0);
		winner.getPlayer().hidePlayer(player);
		setState(BracketsState.ROUND_ENDING);
		setEventTask(new BracketsRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == BracketsState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == BracketsState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
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
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		PlayerUtil.spectator(player);
		player.setFlying(true);

		player.teleport(Array.getInstance().getBracketsManager().getBracketsSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
        PlayerUtil.reset(player);
		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Essentials.teleportToSpawn(player);
	}
}

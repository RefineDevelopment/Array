package me.drizzy.practice.events.types.spleef;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayer;
import me.drizzy.practice.events.types.spleef.player.SpleefPlayerState;
import me.drizzy.practice.events.types.spleef.task.SpleefRoundEndTask;
import me.drizzy.practice.events.types.spleef.task.SpleefRoundStartTask;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.util.location.Circle;
import me.drizzy.practice.util.other.*;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
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
public class Spleef {

	protected static String EVENT_PREFIX=CC.translate("&8[&cSpleef&8] &r");
    public final List<Player> catcher = new ArrayList<>();
	private final String name;
	@Setter private SpleefState state = SpleefState.WAITING;
	@Getter @Setter static private Kit kit = Kit.getByName("Spleef");
	private SpleefTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, SpleefPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private final List<UUID> spectators = new ArrayList<>();
	private final List<Location> placedBlocks = new ArrayList<>();
	private final List<BlockState> changedBlocks = new ArrayList<>();
	@Getter @Setter	public static int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;
	@Getter	@Setter	private static boolean enabled = true;


	public Spleef(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Spleef spleef = Array.getInstance().getSpleefManager().getActiveSpleef();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&cHost: &r" + spleef.getName()));

		if (spleef.isWaiting()) {
			toReturn.add("&cPlayers: &r" + spleef.getEventPlayers().size() + "/" + Spleef.getMaxPlayers());
			toReturn.add("");

			if (spleef.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(spleef.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&cPlayers: &r" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
			toReturn.add("&cDuration: &r" + spleef.getRoundDuration());
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
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
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

		broadcastMessage(CC.RED + player.getName() + CC.GRAY + " has joined the &cSpleef Event&8! &8(&c" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &cSpleef Event&8!"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getSpleefManager().getSpleefSpawn());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
					NameTags.color(player, otherPlayer, Array.getInstance().getEssentials().getNametagMeta().getEventColor(), false);
				}
			}
		}.runTaskAsynchronously(Array.getInstance());
	}

	public void handleLeave(Player player) {
		if (state != SpleefState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SpleefState.WAITING) {
			broadcastMessage(CC.RED + player.getName() + CC.GRAY + " left the &cSpleef Event&8! &8(&c" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &cSpleef Event&8!"));
		}

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
		}.runTaskAsynchronously(Array.getInstance());

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
		Array.getInstance().getSpleefManager().setActiveSpleef(null);
		Array.getInstance().getSpleefManager().setCooldown(new Cooldown(60_000L * 10));
        this.getCatcher().clear();
		setEventTask(null);

		new SpleefResetTask(this).runTask(Array.getInstance());

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The spleef events has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Spleef Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Spleef Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.RED + "Spleef Event" + CC.GRAY + "!");
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
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&c&l[Spleef Event]"));
		strings.add(CC.translate("&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&fA &cSpleef &fevent is being hosted by &c" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Spleef events", "/spleef join");
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
		TaskUtil.runAsync(() -> Array.getInstance().getNMSManager().getKnockbackType().applyKnockback(player, Array.getInstance().getBracketsManager().getBracketsKnockbackProfile()));
	}
	public void onLeave(Player player) {
		Array.getInstance().getNMSManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(SpleefState.ROUND_STARTING);

		int i = 0;
		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				Location midSpawn = Array.getInstance().getSpleefManager().getSpleefSpawn();
				List<Location> circleLocations = Circle.getCircle(midSpawn, 7, this.getPlayers().size());
				Location center = midSpawn.clone();
				Location loc = circleLocations.get(i);
				Location target = loc.setDirection(center.subtract(loc).toVector());
				player.teleport(target.add(0, 0.5, 0));
				circleLocations.remove(i);				i++;
				Profile profile = Profile.getByUuid(player.getUniqueId());
				if (profile.isInSpleef()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);
				player.getInventory().addItem(Hotbar.getItems().get(HotbarType.SPLEEF_MATCH));
			}
		}
		setEventTask(new SpleefRoundStartTask(this));
	}

	public void onDeath(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		broadcastMessage("&c" + player.getName() + "&7 died!");


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
		}.runTaskAsynchronously(Array.getInstance());

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.refreshHotbar();
			}
		}.runTask(Array.getInstance());
	}

	public String getRoundDuration() {
		if (getState() == SpleefState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == SpleefState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(this);
		PlayerUtil.spectator(player);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		player.setFlying(true);


		player.teleport(Array.getInstance().getSpleefManager().getSpleefSpawn());
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

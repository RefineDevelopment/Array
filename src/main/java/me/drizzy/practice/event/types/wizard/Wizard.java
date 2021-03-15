package me.drizzy.practice.event.types.wizard;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.Array;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.event.types.wizard.player.WizardPlayer;
import me.drizzy.practice.event.types.wizard.player.WizardPlayerState;
import me.drizzy.practice.event.types.wizard.task.WizardRoundEndTask;
import me.drizzy.practice.event.types.wizard.task.WizardRoundStartTask;
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
public class Wizard {

	protected static String EVENT_PREFIX=CC.translate("&8[&bWizard&8] &r");

	private final String name;
	@Setter private WizardState state=WizardState.WAITING;
	private WizardTask eventTask;
	private final PlayerSnapshot host;
	private final LinkedHashMap<UUID, WizardPlayer> eventPlayers=new LinkedHashMap<>();
	@Getter private final List<UUID> spectators=new ArrayList<>();
	@Getter @Setter	public static int maxPlayers;
	@Getter	@Setter	private int totalPlayers;
	@Setter	private Cooldown cooldown;
	private final List<Entity> entities=new ArrayList<>();
	private WizardPlayer roundPlayerA;
	private WizardPlayer roundPlayerB;
	@Setter	private long roundStart;
	@Getter	@Setter	private static boolean enabled = true;


	public Wizard(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		Wizard.maxPlayers=100;

	}
	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Wizard wizard= Array.getInstance().getWizardManager().getActiveWizard();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + wizard.getName()));
		if (wizard.isWaiting()) {
			toReturn.add("&bPlayers: &r" + wizard.getEventPlayers().size() + "/" + Wizard.getMaxPlayers());
			toReturn.add("");

			if (wizard.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(wizard.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bPlayers: &r" + wizard.getRemainingPlayers().size() + "/" + wizard.getTotalPlayers());
			toReturn.add("&bDuration: &r" + wizard.getRoundDuration());
			toReturn.add("");
			toReturn.add("&a" + wizard.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&c" + wizard.getRoundPlayerB().getUsername());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(WizardTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.getInstance(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == WizardState.WAITING;
	}

	public boolean isFighting() {
		return state == WizardState.ROUND_FIGHTING;
	}

	public WizardPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for ( WizardPlayer wizardPlayer : eventPlayers.values()) {
			Player player = wizardPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for ( WizardPlayer wizardPlayer : eventPlayers.values()) {
			if (wizardPlayer.getState() == WizardPlayerState.WAITING) {
				Player player = wizardPlayer.getPlayer();
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

		eventPlayers.put(player.getUniqueId(), new WizardPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " has joined the &bWizard Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
		player.sendMessage(CC.translate("&8[&a+&8] &7You have successfully joined the &bWizard Event&8!"));

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWizard(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.getInstance().getWizardManager().getWizardSpectator());

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

		if (state == WizardState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.GRAY + " left the &bWizard Event&8! &8(&b" + getRemainingPlayers().size() + "/" + getMaxPlayers() + "&8)");
			player.sendMessage(CC.translate("&8[&c-&8] &7You have successfully left the &bWizard Event&8!"));
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setWizard(null);
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
		WizardPlayer loser = getEventPlayer(player);
		loser.setState(WizardPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		Array.getInstance().getWizardManager().setActiveWizard(null);
		Array.getInstance().getWizardManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The Wizard event has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Wizard Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Wizard Event" + CC.GRAY + "!");
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.GRAY + " has won the " + CC.AQUA + "Wizard Event" + CC.GRAY + "!");
		}

		for ( WizardPlayer wizardPlayer : eventPlayers.values()) {
			Player player = wizardPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setWizard(null);
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

		for ( WizardPlayer wizardPlayer : eventPlayers.values()) {
			if (wizardPlayer.getState() == WizardPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for ( WizardPlayer wizardPlayer : eventPlayers.values()) {
			if (wizardPlayer.getState() != WizardPlayerState.ELIMINATED) {
				return wizardPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		List<String> strings=new ArrayList<>();
		strings.add(CC.translate(" "));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&b&l[Wizard Event]"));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + ""));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&fA &bWizard &fevent is being hosted by &b" + this.host.getUsername()));
		strings.add(CC.translate("&7⬛⬛&b⬛&7⬛⬛⬛⬛⬛ " + "&fEvent is starting in 60 seconds!"));
		strings.add(CC.translate("&7⬛⬛&b⬛⬛⬛⬛&7⬛⬛ " + "&a&l[Click to Join]"));
		strings.add(CC.translate("&7⬛⬛⬛⬛⬛⬛⬛⬛"));
		strings.add(CC.translate(" "));
		for ( String string : strings ) {
			Clickable message = new Clickable(string, "Click to join Wizard event", "/wizard join");
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
		Profile.setKb(player, Array.getInstance().getWizardManager().getWizardKnockbackProfile());
	}
	public void onLeave(Player player) {
		Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);
	}

	public void onRound() {
		setState(WizardState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getWizardManager().getWizardSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInWizard()) {
					profile.refreshHotbar();
				}
			}

			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();

			if (player != null) {
				player.teleport(Array.getInstance().getWizardManager().getWizardSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInWizard()) {
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

		playerA.teleport(Array.getInstance().getWizardManager().getWizardSpawn1());
		playerA.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.WIZARD_WAND));
		playerB.teleport(Array.getInstance().getWizardManager().getWizardSpawn2());
		playerB.getInventory().setItem(0, Hotbar.getItems().get(HotbarType.WIZARD_WAND));
		setEventTask(new WizardRoundStartTask(this));
	}

	public void onDeath(Player player) {
		WizardPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(WizardPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage("&b" + player.getName() + "&7 was eliminated by &b" + winner.getUsername() + "&7!");
		player.setFireTicks(0);
		winner.getPlayer().hidePlayer(player);
		setState(WizardState.ROUND_ENDING);
		setEventTask(new WizardRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == WizardState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == WizardState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
	}

	private WizardPlayer findRoundPlayer() {
		WizardPlayer wizardPlayer= null;

		for ( WizardPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == WizardPlayerState.WAITING) {
				if (wizardPlayer == null) {
					wizardPlayer= check;
					continue;
				}

				if (check.getRoundWins() == 0) {
					wizardPlayer= check;
					continue;
				}

				if (check.getRoundWins() <= wizardPlayer.getRoundWins()) {
					wizardPlayer= check;
				}
			}
		}

		if (wizardPlayer == null) {
			throw new RuntimeException("Could not find a new round player");
		}

		return wizardPlayer;
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWizard(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		PlayerUtil.spectator(player);
		player.setFlying(true);

		player.teleport(Array.getInstance().getWizardManager().getWizardSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
        PlayerUtil.reset(player);
		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setWizard(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Essentials.teleportToSpawn(player);
	}
}

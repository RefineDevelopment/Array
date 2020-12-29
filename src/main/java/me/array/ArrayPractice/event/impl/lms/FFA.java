package me.array.ArrayPractice.event.impl.lms;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.event.impl.lms.player.FFAPlayer;
import me.array.ArrayPractice.event.impl.lms.player.FFAPlayerState;
import me.array.ArrayPractice.event.impl.lms.task.FFARoundEndTask;
import me.array.ArrayPractice.event.impl.lms.task.FFARoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class FFA {

	protected static String EVENT_PREFIX = CC.GOLD + CC.BOLD + "(FFA) " + CC.RESET;

	private String name;
	@Setter private FFAState state = FFAState.WAITING;
	@Getter @Setter static private Kit kit;
	private FFATask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, FFAPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;


	public FFA(Player player, Kit kit) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
		this.kit = kit;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		FFA ffa = Array.get().getFfaManager().getActiveFFA();
		
		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&6Host: &r" + ffa.getName()));
		toReturn.add(CC.translate("&eKit: &r" + kit.getName()));

		if (ffa.isWaiting()) {
			toReturn.add("&ePlayers: &r" + ffa.getEventPlayers().size() + "/" + ffa.getMaxPlayers());
			toReturn.add("");

			if (ffa.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(ffa.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&eRemaining: &r" + ffa.getRemainingPlayers().size() + "/" + ffa.getTotalPlayers());
			toReturn.add("&eDuration: &r" + ffa.getRoundDuration());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(FFATask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == FFAState.WAITING;
	}

	public boolean isFighting() {
		return state == FFAState.ROUND_FIGHTING;
	}

	public FFAPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (FFAPlayer ffaPlayer : eventPlayers.values()) {
			Player player = ffaPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (FFAPlayer ffaPlayer : eventPlayers.values()) {
			if (ffaPlayer.getState() == FFAPlayerState.WAITING) {
				Player player = ffaPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new FFAPlayer(player));

		broadcastMessage(CC.GOLD + player.getName() + CC.YELLOW + " joined the ffa " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setFfa(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getFfaManager().getFfaSpectator());

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
		if (state != FFAState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player, null);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == FFAState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the ffa " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setFfa(null);
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
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player, Player killer) {
		FFAPlayer loser = getEventPlayer(player);
		loser.setState(FFAPlayerState.ELIMINATED);

		onDeath(player, killer);
	}

	public void end() {
		Array.get().getFfaManager().setActiveFFA(null);
		Array.get().getFfaManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The ffa has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the ffa!");
		}

		for (FFAPlayer ffaPlayer : eventPlayers.values()) {
			Player player = ffaPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setFfa(null);
				profile.refreshHotbar();

				Array.get().getEssentials().teleportToSpawn(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public boolean canEnd() {
		int remaining = 0;

		for (FFAPlayer ffaPlayer : eventPlayers.values()) {
			if (ffaPlayer.getState() == FFAPlayerState.WAITING) {
				remaining++;
			}
		}

		return remaining == 1;
	}

	public Player getWinner() {
		for (FFAPlayer ffaPlayer : eventPlayers.values()) {
			if (ffaPlayer.getState() != FFAPlayerState.ELIMINATED) {
				return ffaPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting FFA " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the ffa.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ffa join"))
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
		setState(FFAState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Array.get().getFfaManager().getFfaSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInFfa()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);
			}

			for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
				player.getInventory().addItem(itemStack);
			}
		}
		setEventTask(new FFARoundStartTask(this));
	}

	public void onDeath(Player player, Player killer) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (killer != null) {
			broadcastMessage("&c" + player.getName() + "&e was eliminated by &c" + killer.getName() + "&e!");
		}


		if (canEnd()) {
			setState(FFAState.ROUND_ENDING);
			setEventTask(new FFARoundEndTask(this));
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
		}.runTaskAsynchronously(Array.get());

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.refreshHotbar();
			}
		}.runTask(Array.get());
	}

	public String getRoundDuration() {
		if (getState() == FFAState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == FFAState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(Player player) {
		if (this.getState().equals(FFAState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setFfa(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();
		player.setFlying(true);

		player.teleport(Array.get().getFfaManager().getFfaSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setFfa(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

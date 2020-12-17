package me.array.ArrayPractice.event.impl.spleef;

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
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayer;
import me.array.ArrayPractice.event.impl.spleef.player.SpleefPlayerState;
import me.array.ArrayPractice.event.impl.spleef.task.SpleefRoundEndTask;
import me.array.ArrayPractice.event.impl.spleef.task.SpleefRoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Spleef {

	protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Spleef) " + CC.RESET;

	private String name;
	@Setter private SpleefState state = SpleefState.WAITING;
	@Getter @Setter static private Kit kit = Kit.getByName("Spleef");
	private SpleefTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, SpleefPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private List<Location> placedBlocks = new ArrayList<>();
	private List<BlockState> changedBlocks = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;


	public Spleef(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Spleef spleef = Array.get().getSpleefManager().getActiveSpleef();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + spleef.getName()));

		if (spleef.isWaiting()) {
			toReturn.add("&bPlayers: &r" + spleef.getEventPlayers().size() + "/" + spleef.getMaxPlayers());
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
			toReturn.add("&bRemaining: &r" + spleef.getRemainingPlayers().size() + "/" + spleef.getTotalPlayers());
			toReturn.add("&bDuration: &r" + spleef.getRoundDuration());
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
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
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

		broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the spleef " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getSpleefManager().getSpleefSpectator());

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
		if (state != SpleefState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == SpleefState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the spleef " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setSpleef(null);
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

	public void handleDeath(Player player) {
		SpleefPlayer loser = getEventPlayer(player);
		loser.setState(SpleefPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		Array.get().getSpleefManager().setActiveSpleef(null);
		Array.get().getSpleefManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		new SpleefResetTask(this).runTask(Array.get());

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The spleef has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the spleef!");
		}

		for (SpleefPlayer spleefPlayer : eventPlayers.values()) {
			Player player = spleefPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setSpleef(null);
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
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Spleef " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the spleef.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spleef join"))
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
		setState(SpleefState.ROUND_STARTING);

		int i = 0;
		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Array.get().getSpleefManager().getSpleefSpectator());
				i++;

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInSpleef()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);
			}

			for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
				player.getInventory().addItem(itemStack);
			}
		}
		setEventTask(new SpleefRoundStartTask(this));
	}

	public void onDeath(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		broadcastMessage("&c" + player.getName() + "&f died!");


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
		}.runTaskAsynchronously(Array.get());

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.refreshHotbar();
			}
		}.runTask(Array.get());
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
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(Array.get().getSpleefManager().getSpleefSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setSpleef(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

package me.array.ArrayPractice.event.impl.brackets;

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
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayer;
import me.array.ArrayPractice.event.impl.brackets.player.BracketsPlayerState;
import me.array.ArrayPractice.event.impl.brackets.task.BracketsRoundEndTask;
import me.array.ArrayPractice.event.impl.brackets.task.BracketsRoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Brackets {

	protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Brackets) " + CC.RESET;

	private String name;
	@Setter private BracketsState state = BracketsState.WAITING;
	@Getter @Setter static private Kit kit;
	private BracketsTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, BracketsPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	private List<Entity> entities = new ArrayList<>();
	private BracketsPlayer roundPlayerA;
	private BracketsPlayer roundPlayerB;
	@Setter
	private long roundStart;


	public Brackets(Player player, Kit kit) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
		this.kit = kit;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Brackets brackets = Array.get().getBracketsManager().getActiveBrackets();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + brackets.getName()));
		toReturn.add(CC.translate("&bKit: &r" + kit.getName()));

		if (brackets.isWaiting()) {
			toReturn.add("&bPlayers: &r" + brackets.getEventPlayers().size() + "/" + brackets.getMaxPlayers());
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
			toReturn.add("&bRemaining: &r" + brackets.getRemainingPlayers().size() + "/" + brackets.getTotalPlayers());
			toReturn.add("&bDuration: &r" + brackets.getRoundDuration());
			toReturn.add("");
			toReturn.add("&b" + brackets.getRoundPlayerA().getUsername());
			toReturn.add("vs");
			toReturn.add("&b" + brackets.getRoundPlayerB().getUsername());
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
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
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
		eventPlayers.put(player.getUniqueId(), new BracketsPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the brackets " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getBracketsManager().getBracketsSpectator());

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
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == BracketsState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the brackets " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setBrackets(null);
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
		BracketsPlayer loser = getEventPlayer(player);
		loser.setState(BracketsPlayerState.ELIMINATED);

		onDeath(player);
	}

	public void end() {
		Array.get().getBracketsManager().setActiveBrackets(null);
		Array.get().getBracketsManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		Player winner = this.getWinner();

		if (winner == null) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + "The brackets has been canceled.");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + winner.getName() + CC.WHITE + " has won the brackets!");
		}

		for (BracketsPlayer bracketsPlayer : eventPlayers.values()) {
			Player player = bracketsPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setBrackets(null);
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
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Brackets " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GRAY + "Click to join the brackets.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/brackets join"))
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
		setState(BracketsState.ROUND_STARTING);

		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();

			if (player != null) {
				player.teleport(Array.get().getBracketsManager().getBracketsSpectator());

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
				player.teleport(Array.get().getBracketsManager().getBracketsSpectator());

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

		PlayerUtil.denyMovement(playerA);
		PlayerUtil.denyMovement(playerB);

		playerA.teleport(Array.get().getBracketsManager().getBracketsSpawn1());

		for (ItemStack itemStack : Profile.getByUuid(playerA.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
			playerA.getInventory().addItem(itemStack);
		}
		playerB.teleport(Array.get().getBracketsManager().getBracketsSpawn2());
		for (ItemStack itemStack : Profile.getByUuid(playerA.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
			playerB.getInventory().addItem(itemStack);
		}
		setEventTask(new BracketsRoundStartTask(this));
	}

	public void onDeath(Player player) {
		BracketsPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(BracketsPlayerState.WAITING);
		winner.incrementRoundWins();

		broadcastMessage("&c" + player.getName() + "&f was eliminated by &c" + winner.getUsername() + "&f!");

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

		player.teleport(Array.get().getBracketsManager().getBracketsSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setBrackets(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

package me.array.ArrayPractice.event.impl.juggernaut;

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
import me.array.ArrayPractice.util.nametag.NameTags;
import me.array.ArrayPractice.event.impl.juggernaut.player.JuggernautPlayer;
import me.array.ArrayPractice.event.impl.juggernaut.player.JuggernautPlayerState;
import me.array.ArrayPractice.event.impl.juggernaut.task.JuggernautRoundEndTask;
import me.array.ArrayPractice.event.impl.juggernaut.task.JuggernautRoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Juggernaut {

	protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Juggernaut) " + CC.RESET;

	private String name;
	@Setter private JuggernautState state = JuggernautState.WAITING;
	private JuggernautTask eventTask;
	private PlayerSnapshot host;
	@Getter @Setter private JuggernautPlayer juggernaut;
	private LinkedHashMap<UUID, JuggernautPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;



	public Juggernaut(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Juggernaut juggernaut = Array.get().getJuggernautManager().getActiveJuggernaut();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + juggernaut.getName()));

		if (juggernaut.isWaiting()) {
			toReturn.add("&bPlayers: &r" + juggernaut.getEventPlayers().size() + "/" + juggernaut.getMaxPlayers());
			toReturn.add("");

			if (juggernaut.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(juggernaut.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bJuggernaut: &r" + juggernaut.getName());
			toReturn.add("&bRemaining: &r" + juggernaut.getRemainingPlayers().size() + "/" + juggernaut.getTotalPlayers());
			toReturn.add("&bDuration: &r" + juggernaut.getRoundDuration());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(JuggernautTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == JuggernautState.WAITING;
	}

	public boolean isFighting(Player player) {
		if (state.equals(JuggernautState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public JuggernautPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
			Player player = juggernautPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public int getRandomPlayer() { //this works fine this is a shit :V
		int min = 0;
		int max = this.getRemainingPlayers().size() - 1;
		int r = (int) (Math.random() * (max - min)) + min;

		// new Random().nextInt( this.getRemainingPlayers().size()); :vvvvv

		return r;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
			if (juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
				Player player = juggernautPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new JuggernautPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " joined the juggernaut " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setJuggernaut(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getJuggernautManager().getJuggernautSpectator());

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
		if (state != JuggernautState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player, null);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == JuggernautState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the juggernaut " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setJuggernaut(null);
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
		JuggernautPlayer loser = getEventPlayer(player);
		loser.setState(JuggernautPlayerState.ELIMINATED);

		onDeath(player, killer);
	}

	public void end(String whowins) {
		Array.get().getJuggernautManager().setActiveJuggernaut(null);
		Array.get().getJuggernautManager().setCooldown(new Cooldown(60_000L * 10));

		setEventTask(null);

		if (whowins.equalsIgnoreCase("None")) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + " the event is cancelled");
		} else {
			if (whowins.equalsIgnoreCase("The Juggernaut")) {
				Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + whowins + CC.GRAY + " (" + juggernaut.getPlayer().getName() + ")"+ CC.WHITE + " has won the juggernaut!");
			} else {
				Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + whowins + CC.WHITE + " has won the juggernaut event!");
			}
		}

		for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
			Player player = juggernautPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setJuggernaut(null);
				profile.refreshHotbar();

				Array.get().getEssentials().teleportToSpawn(player);
			}
		}

		getSpectatorsList().forEach(this::removeSpectator);

		for (Player player : getPlayers()) {
			Profile.getByUuid(player.getUniqueId()).handleVisibility();
		}
	}

	public String canEnd() {
		List<JuggernautPlayer> p = new ArrayList<>();

		for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
			if (juggernautPlayer.getState() == JuggernautPlayerState.WAITING) {
				p.add(juggernautPlayer);
			}
		}

		boolean canEnd = true;

		if (p.size() == 1 && p.get(0).isJuggernaut()) {
			return "The Juggernaut";
		} else if (p.size() > 0) {
			for (JuggernautPlayer pl : p) {
				if (pl.isJuggernaut()) {
					canEnd = false;
				}
			}
			if (canEnd) {
				return "Players";
			}
		}

		return "None";
	}

	public Player getWinner() {
		for (JuggernautPlayer juggernautPlayer : eventPlayers.values()) {
			if (juggernautPlayer.getState() != JuggernautPlayerState.ELIMINATED) {
				return juggernautPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Juggernaut " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GREEN + "Click to join the juggernaut.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/juggernaut join"))
				.create();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!eventPlayers.containsKey(player.getUniqueId())) {
				player.sendMessage(CC.translate(""));
				player.spigot().sendMessage(components);
				player.sendMessage(CC.translate(""));
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
		setState(JuggernautState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				player.teleport(Array.get().getJuggernautManager().getJuggernautSpectator());

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInJuggernaut()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);

				if (this.getEventPlayer(player).isJuggernaut()) {
					for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(Kit.getByName("NoDebuff")).getKitItems()) {
						player.getInventory().addItem(itemStack);
					}
				} else {
					for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(Kit.getByName("Soup")).getKitItems()) {
						player.getInventory().addItem(itemStack);
					}
				}

				if (this.getEventPlayer(player).isJuggernaut()) {
					for (Player pl : this.getRemainingPlayers()) {
						NameTags.color(player, pl, org.bukkit.ChatColor.RED, true);
					}
				} else {
					for (Player pl : this.getRemainingPlayers()) {
						if (getEventPlayer(pl).isJuggernaut()) {
							NameTags.color(player, pl, org.bukkit.ChatColor.RED, true);
						} else {
							NameTags.color(player, pl, org.bukkit.ChatColor.GREEN, true);
						}
					}
				}
			}
		}
		setEventTask(new JuggernautRoundStartTask(this));
	}

	public void onDeath(Player player, Player killer) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (killer != null) {
			broadcastMessage("&c" + player.getName() + "&f was eliminated by &c" + killer.getName() + "&f!");
		}


		if (!canEnd().equalsIgnoreCase("None")) {
			setState(JuggernautState.ROUND_ENDING);
			setEventTask(new JuggernautRoundEndTask(this));
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
		if (getState() == JuggernautState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == JuggernautState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setJuggernaut(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(Array.get().getJuggernautManager().getJuggernautSpectator());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setJuggernaut(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

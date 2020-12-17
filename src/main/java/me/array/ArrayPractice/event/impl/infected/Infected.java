package me.array.ArrayPractice.event.impl.infected;

import lombok.Getter;
import lombok.Setter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.util.PlayerSnapshot;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.util.nametag.NameTags;
import me.array.ArrayPractice.event.impl.infected.player.InfectedPlayer;
import me.array.ArrayPractice.event.impl.infected.player.InfectedPlayerState;
import me.array.ArrayPractice.event.impl.infected.task.InfectedRoundEndTask;
import me.array.ArrayPractice.event.impl.infected.task.InfectedRoundStartTask;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class Infected {

	protected static String EVENT_PREFIX = CC.DARK_AQUA + CC.BOLD + "(Infected) " + CC.RESET;

	private String name;
	@Setter private InfectedState state = InfectedState.WAITING;
	private InfectedTask eventTask;
	private PlayerSnapshot host;
	@Getter @Setter private InfectedPlayer infected;
	private LinkedHashMap<UUID, InfectedPlayer> eventPlayers = new LinkedHashMap<>();
	@Getter private List<UUID> spectators = new ArrayList<>();
	private int maxPlayers;
	@Getter @Setter private int totalPlayers;
	@Setter private Cooldown cooldown;
	@Setter private long roundStart;
	@Getter @Setter BukkitTask task;



	public Infected(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public List<String> getLore() {
		List<String> toReturn = new ArrayList<>();

		Infected infected = Array.get().getInfectedManager().getActiveInfected();

		toReturn.add(CC.MENU_BAR);
		toReturn.add(CC.translate("&bHost: &r" + infected.getName()));

		if (infected.isWaiting()) {
			toReturn.add("&bPlayers: &r" + infected.getEventPlayers().size() + "/" + infected.getMaxPlayers());
			toReturn.add("");

			if (infected.getCooldown() == null) {
				toReturn.add(CC.translate("&fWaiting for players..."));
			} else {
				String remaining = TimeUtil.millisToSeconds(infected.getCooldown().getRemaining());

				if (remaining.startsWith("-")) {
					remaining = "0.0";
				}

				toReturn.add(CC.translate("&fStarting in " + remaining + "s"));
			}
		} else {
			toReturn.add("&bRemaining: &r" + infected.getRemainingPlayers().size() + "/" + infected.getTotalPlayers());
			toReturn.add("&bDuration: &r" + infected.getRoundDuration());
		}
		toReturn.add(CC.MENU_BAR);

		return toReturn;
	}

	public void setEventTask(InfectedTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(Array.get(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == InfectedState.WAITING;
	}

	public boolean isFighting(Player player) {
		if (state.equals(InfectedState.ROUND_FIGHTING)) {
			return getRemainingPlayers().contains(player);
		} else {
			return false;
		}
	}

	public InfectedPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			Player player = infectedPlayer.getPlayer();

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

	public List<Player> getInfectedPlayers() {
		List<Player> players = new ArrayList<>();

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			if (infectedPlayer.getState() == InfectedPlayerState.INFECTED) {
				Player player = infectedPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public List<Player> getSurvivorPlayers() {
		List<Player> players = new ArrayList<>();

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			if (infectedPlayer.getState() == InfectedPlayerState.WAITING) {
				Player player = infectedPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			if (infectedPlayer.getState() == InfectedPlayerState.WAITING || infectedPlayer.getState() == InfectedPlayerState.INFECTED) {
				Player player = infectedPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new InfectedPlayer(player));

		broadcastMessage(CC.AQUA + player.getName() + CC.YELLOW + " joined the infected " + CC.GRAY + "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");

		onJoin(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setInfected(this);
		profile.setState(ProfileState.IN_EVENT);
		profile.refreshHotbar();

		player.teleport(Array.get().getInfectedManager().getInfectedSpawn1());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player otherPlayer : getPlayers()) {
					Profile otherProfile = Profile.getByUuid(otherPlayer.getUniqueId());
					otherProfile.handleVisibility(otherPlayer, player);
					profile.handleVisibility(player, otherPlayer);
				}
			}
		}.runTask(Array.get());
	}

	public void handleLeave(Player player) {
		if (state != InfectedState.WAITING) {
			if (isFighting(player)) {
				handleDeath(player, null, true);
			}
		}

		eventPlayers.remove(player.getUniqueId());

		if (state == InfectedState.WAITING) {
			broadcastMessage(CC.AQUA + player.getName() + CC.WHITE + " left the infected " + CC.GRAY +
			                 "(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")");
		}

		onLeave(player);

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setInfected(null);
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
		}.runTask(Array.get());
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player, Player killer, boolean disconnected) {
		InfectedPlayer loser = getEventPlayer(player);
		if (!disconnected) {
			loser.setState(InfectedPlayerState.INFECTED);
			loser.setInfected(true);
		} else {
			loser.setState(InfectedPlayerState.ELIMINATED);
		}

		onDeath(player, killer, !disconnected);
	}

	public void end(String whowins) {
		Array.get().getInfectedManager().setActiveInfected(null);
		Array.get().getInfectedManager().setCooldown(new Cooldown(60_000L * 10));


		setEventTask(null);
		this.task.cancel();
		this.task = null;

		if (whowins.equalsIgnoreCase("None")) {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.RED + " The event is cancelled");
		} else {
			Bukkit.broadcastMessage(EVENT_PREFIX + CC.GREEN + whowins + CC.WHITE + " have won the infected event!");
		}

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			Player player = infectedPlayer.getPlayer();

			if (player != null) {
				Profile profile = Profile.getByUuid(player.getUniqueId());
				profile.setState(ProfileState.IN_LOBBY);
				profile.setInfected(null);
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
		List<InfectedPlayer> p = new ArrayList<>();

		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			if (infectedPlayer.getState() == InfectedPlayerState.WAITING) {
				p.add(infectedPlayer);
			}
		}

		if (p.size() == 0) {
			return "The Infected";
		} else if ((System.currentTimeMillis() - roundStart) > 600000) {
			return "The Survivors";
		}

		return "None";
	}

	public Player getWinner() {
		for (InfectedPlayer infectedPlayer : eventPlayers.values()) {
			if (infectedPlayer.getState() != InfectedPlayerState.ELIMINATED) {
				return infectedPlayer.getPlayer();
			}
		}

		return null;
	}

	public void announce() {
		BaseComponent[] components = new ChatComponentBuilder("")
				.parse(EVENT_PREFIX + CC.AQUA + getHost().getUsername() + CC.YELLOW + " is hosting Infected " + CC.GRAY + "(Click to join)")
				.attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(CC.GREEN + "Click to join the infected.").create()))
				.attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/infected join"))
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
		setState(InfectedState.ROUND_STARTING);

		for (Player player : this.getRemainingPlayers()) {
			if (player != null) {
				if (this.getEventPlayer(player).isInfected()) {
					player.teleport(Array.get().getInfectedManager().getInfectedSpawn1());
				} else {
					player.teleport(Array.get().getInfectedManager().getInfectedSpawn2());
				}

				Profile profile = Profile.getByUuid(player.getUniqueId());

				if (profile.isInInfected()) {
					profile.refreshHotbar();
				}
				PlayerUtil.reset(player);

				if (this.getEventPlayer(player).isInfected()) {
					KitLoadout kitLoadout = Kit.getByName("infected").getKitLoadout();
					player.getInventory().setArmorContents(kitLoadout.getArmor());
					player.getInventory().setContents(kitLoadout.getContents());
					player.updateInventory();
				} else {
					KitLoadout kitLoadout = Kit.getByName("infectedsurvivors").getKitLoadout();
					player.getInventory().setArmorContents(kitLoadout.getArmor());
					player.getInventory().setContents(kitLoadout.getContents());
					player.updateInventory();
				}

				if (this.getEventPlayer(player).isInfected()) {
					for (Player pl : this.getRemainingPlayers()) {
						NameTags.color(player, pl, org.bukkit.ChatColor.RED, true);
					}
				} else {
					for (Player pl : this.getRemainingPlayers()) {
						if (getEventPlayer(pl).isInfected()) {
							NameTags.color(player, pl, org.bukkit.ChatColor.RED, true);
						} else {
							NameTags.color(player, pl, org.bukkit.ChatColor.GREEN, true);
						}
					}
				}
			}
		}
		setEventTask(new InfectedRoundStartTask(this));
	}

	public void onDeath(Player player, Player killer, boolean putInfected) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (killer != null) {
			broadcastMessage("&c" + player.getName() + "&f was eliminated by &c" + killer.getName() + "&f!");
		}


		if (!canEnd().equalsIgnoreCase("None")) {
			setState(InfectedState.ROUND_ENDING);
			setEventTask(new InfectedRoundEndTask(this));
		}

		if (putInfected) {
			PlayerUtil.reset(player);

			getEventPlayer(player).setInfected(true);
			getEventPlayer(player).setState(InfectedPlayerState.INFECTED);


			for (Player pl : getRemainingPlayers()) {
				if (getEventPlayer(pl).isInfected()) {
					NameTags.color(player, pl, org.bukkit.ChatColor.GREEN, true);
					NameTags.color(pl, player, org.bukkit.ChatColor.GREEN, true);
				} else {
					NameTags.color(player, pl, org.bukkit.ChatColor.RED, true);
					NameTags.color(pl, player, org.bukkit.ChatColor.RED, true);
				}
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(Array.get().getInfectedManager().getInfectedSpawn1());

					PlayerUtil.reset(player);
					KitLoadout kitLoadout = Kit.getByName("infected").getKitLoadout();
					player.getInventory().setArmorContents(kitLoadout.getArmor());
					player.getInventory().setContents(kitLoadout.getContents());
					player.updateInventory();
				}
			}.runTaskLater(Array.get(), 20L);
		}
	}

	public String getRoundDuration() {
		if (getState() == InfectedState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == InfectedState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public void addSpectator(Player player) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setInfected(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(Array.get().getInfectedManager().getInfectedSpawn1());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setInfected(null);
		profile.setState(ProfileState.IN_LOBBY);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}
}

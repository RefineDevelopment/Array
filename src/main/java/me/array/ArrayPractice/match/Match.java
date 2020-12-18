package me.array.ArrayPractice.match;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import org.bukkit.entity.Player.*;
import me.array.ArrayPractice.match.task.MatchPearlCooldownTask;
import me.array.ArrayPractice.match.task.MatchResetTask;
import me.array.ArrayPractice.match.task.MatchSnapshotCleanupTask;
import me.array.ArrayPractice.match.task.MatchStartTask;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.TimeUtil;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Getter
public abstract class Match {

	@Getter protected static List<Match> matches = new ArrayList<>();

	private final UUID matchId = UUID.randomUUID();
	private final Queue queue;
	private final Kit kit;
	private final Arena arena;
	private final QueueType queueType;
	@Setter private MatchState state = MatchState.STARTING;
	private List<MatchSnapshot> snapshots = new ArrayList<>();
	private List<UUID> spectators = new ArrayList<>();
	private List<Entity> entities = new ArrayList<>();
	private List<Location> placedBlocks = new ArrayList<>();
	private List<BlockState> changedBlocks = new ArrayList<>();
	@Setter private long startTimestamp;
	public Map<UUID, EnderPearl> pearlMap = new HashMap<>();

	public Match(Queue queue, Kit kit, Arena arena, QueueType queueType) {
		this.queue = queue;
		this.kit = kit;
		this.arena = arena;
		this.queueType = queueType;

		matches.add(this);
	}

	public boolean isStarting() {
		return state == MatchState.STARTING;
	}

	public boolean isFighting() {
		return state == MatchState.FIGHTING;
	}

	public boolean isEnding() {
		return state == MatchState.ENDING;
	}

	public void start() {
		for (Player player : getPlayers()) {

			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.setState(ProfileState.IN_FIGHT);
			profile.setMatch(this);
			for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
				profile.handleVisibility(player, otherPlayer);
			}
			setupPlayer(player);
		}

		onStart();

		for (Player player : this.getPlayers()) {
			if (!Profile.getByUuid(player.getUniqueId()).getSentDuelRequests().isEmpty()) {
				Profile.getByUuid(player.getUniqueId()).getSentDuelRequests().clear();
			}
		}

		state = MatchState.STARTING;
		startTimestamp = -1;
		arena.setActive(true);

		new MatchStartTask(this).runTaskTimer(Array.get(), 20L, 20L);
		getPlayers().forEach(player -> player.sendMessage(CC.translate("&b● &fArena: &b" + arena.getName())));
		getPlayers().forEach(player -> player.sendMessage(CC.translate("&b● &fKit: &b" + kit.getName())));
		getPlayers().forEach(player -> player.sendMessage(CC.translate("")));
	}
	private void end() {
		state = MatchState.ENDING;

		onEnd();

		if (!isKoTHMatch()) {
			snapshots.forEach(matchInventory -> {
				matchInventory.setCreated(System.currentTimeMillis());
				MatchSnapshot.getSnapshots().put(matchInventory.getTeamPlayer().getUuid(), matchInventory);
			});
		}

		getPlayers().forEach(this::removePearl);

		getSpectators().forEach(this::removeSpectator);
		entities.forEach(Entity::remove);

		new MatchResetTask(this).runTask(Array.get());

		getArena().setActive(false);
		matches.remove(this);
	}

	public void handleRespawn(Player player) {
		player.setVelocity(new Vector());

		onRespawn(player);
	}

	public void onPearl(Player player, EnderPearl pearl) {
		this.pearlMap.put(player.getUniqueId(), pearl);
	}

	public void removePearl(Player player) {
		final EnderPearl pearl;
		if (player != null) {
			if ((pearl = (EnderPearl) this.pearlMap.remove(player.getUniqueId())) != null) {
				pearl.remove();
			}
		}
	}

	public void handleDeath(Player deadPlayer, Player killerPlayer, boolean disconnected) {
		TeamPlayer teamPlayer = this.getTeamPlayer(deadPlayer);

		if (teamPlayer == null) return;

		teamPlayer.setDisconnected(disconnected);

		if (!teamPlayer.isAlive() && !isKoTHMatch()) {
			return;
		}

		teamPlayer.setAlive(false);

		EntityLightning lightning = new EntityLightning(((CraftWorld) deadPlayer.getWorld()).getHandle(),
				deadPlayer.getLocation().getX(),
				deadPlayer.getLocation().getY(),
				deadPlayer.getLocation().getZ());

		List<Player> playersAndSpectators = getPlayersAndSpectators();

		playersAndSpectators.forEach(other -> {
			EntityPlayer entityPlayer = ((CraftPlayer) other).getHandle();
			entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityWeather(lightning));
			other.playSound(deadPlayer.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);

			if (!entityPlayer.getUniqueID().equals(deadPlayer.getUniqueId())) {
				entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(((CraftPlayer) deadPlayer).getHandle(), (byte) 3));
			}
		});

		for (Player player : playersAndSpectators) {
			if (teamPlayer.isDisconnected()) {
				player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
				                   CC.YELLOW + " has disconnected.");
				continue;
			}
			if ((!isHCFMatch() && !isKoTHMatch()) && getKit().getGameRules().isParkour()) {
				player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
						CC.YELLOW + " has won.");
			} else if (killerPlayer == null) {
				player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
				                   CC.YELLOW + " has died.");
			} else {
				player.sendMessage(getRelationColor(player, deadPlayer) + deadPlayer.getName() +
				                   CC.YELLOW + " was killed by " + getRelationColor(player, killerPlayer) +
				                   killerPlayer.getName() + CC.YELLOW + ".");
			}
		}

		onDeath(deadPlayer, killerPlayer);

		if (!isKoTHMatch()) {
			if (canEnd()) {
				end();
			} else {
				PlayerUtil.spectator(deadPlayer);

				new BukkitRunnable() {
					@Override
					public void run() {
						for (Player player : getPlayersAndSpectators()) {
							Profile.getByUuid(player.getUniqueId()).handleVisibility(player, deadPlayer);
						}
					}
				}.runTaskLaterAsynchronously(Array.get(), 5000L);
			}
		} else {
			if (canEnd()) {
				end();
			}
		}
	}

	public String getDuration() {
		if (isStarting()) {
			return "00:00";
		} else if (isEnding()) {
			return "Ending";
		} else {
			return TimeUtil.millisToTimer(getElapsedDuration());
		}
	}

	public long getElapsedDuration() {
		return System.currentTimeMillis() - startTimestamp;
	}

	public void broadcastMessage(String message) {
		getPlayers().forEach(player -> player.sendMessage(message));
		getSpectators().forEach(player -> player.sendMessage(message));
	}

	public void broadcastSound(Sound sound) {
		getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
		getSpectators().forEach(player -> player.playSound(player.getLocation(), sound, 1.0F, 1.0F));
	}

	protected List<Player> getSpectators() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void addSpectator(Player player, Player target) {
		spectators.add(player.getUniqueId());

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setMatch(this);
		profile.setState(ProfileState.SPECTATE_MATCH);
		profile.refreshHotbar();
		profile.handleVisibility();

		player.teleport(target.getLocation().clone().add(0, 2, 0));

		if (!player.hasPermission("practice.staff")) {
			for (Player otherPlayer : getPlayers()) {
				otherPlayer.sendMessage(CC.AQUA + player.getName() + CC.YELLOW + " is now spectating your match.");
			}
		}
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());

		if (state != MatchState.ENDING) {
			if (!player.hasPermission("practice.staff")) {
				for (Player otherPlayer : getPlayers()) {
					otherPlayer.sendMessage(CC.RED + player.getName() + CC.YELLOW + " is no longer spectating your match.");
				}
			}
		}

		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.setState(ProfileState.IN_LOBBY);
		profile.setMatch(null);
		profile.refreshHotbar();
		profile.handleVisibility();

		Array.get().getEssentials().teleportToSpawn(player);
	}

	public List<Player> getPlayersAndSpectators() {
		List<Player> allPlayers = new ArrayList<>();
		allPlayers.addAll(getPlayers());
		allPlayers.addAll(getSpectators());
		return allPlayers;
	}

	protected HoverEvent getHoverEvent(TeamPlayer teamPlayer) {
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
				.parse("&bClick to view " + teamPlayer.getUsername() + "'s inventory.").create());
	}

	protected ClickEvent getClickEvent(TeamPlayer teamPlayer) {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewinv " + teamPlayer.getUuid().toString());
	}

	public abstract boolean isSoloMatch();

	public abstract boolean isTeamMatch();

	public abstract boolean isFreeForAllMatch();

	public abstract boolean isHCFMatch();

	public abstract boolean isKoTHMatch();

	public abstract void setupPlayer(Player player);

	public abstract void cleanPlayer(Player player);

	public abstract void onStart();

	public abstract void onEnd();

	public abstract boolean canEnd();

	public abstract void onDeath(Player player, Player killer);

	public abstract void onRespawn(Player player);

	public abstract Player getWinningPlayer();

	public abstract Team getWinningTeam();

	public abstract TeamPlayer getTeamPlayerA();

	public abstract TeamPlayer getTeamPlayerB();

	public abstract List<TeamPlayer> getTeamPlayers();

	public abstract List<Player> getPlayers();

	public abstract List<Player> getAlivePlayers();

	public abstract Team getTeamA();

	public abstract Team getTeamB();

	public abstract Team getTeam(Player player);

	public abstract TeamPlayer getTeamPlayer(Player player);

	public abstract Team getOpponentTeam(Team Team);

	public abstract Team getOpponentTeam(Player player);

	public abstract TeamPlayer getOpponentTeamPlayer(Player player);

	public abstract Player getOpponentPlayer(Player player);

	public abstract int getTotalRoundWins();

	public abstract int getRoundsNeeded(TeamPlayer teamPlayer);

	public abstract int getRoundsNeeded(Team Team);

	public abstract int getTeamACapturePoints();

	public abstract void setTeamACapturePoints(int number);

	public abstract int getTeamBCapturePoints();

	public abstract void setTeamBCapturePoints(int number);

	public abstract int getTimer();

	public abstract void setTimer(int number);

	public abstract Player getCapper();

	public abstract void setCapper(Player player);

	public abstract ChatColor getRelationColor(Player viewer, Player target);

	public static void init() {
		new MatchPearlCooldownTask().runTaskTimerAsynchronously(Array.get(), 2L, 2L);
		new MatchSnapshotCleanupTask().runTaskTimerAsynchronously(Array.get(), 20L * 5, 20L * 5);
		new BukkitRunnable(){
			@Override
			public void run() {
				for(World world : Bukkit.getWorlds()){
					world.setStorm(false);
					world.setThundering(false);
				}
			}
		}.runTaskTimer(Array.get() , 20 , 20);
	}

	public static void cleanup() {
		for (Match match : matches) {
			match.getPlacedBlocks().forEach(location -> location.getBlock().setType(Material.AIR));
			match.getChangedBlocks().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
			match.getEntities().forEach(Entity::remove);
		}
	}

	public static int getInFights(Queue queue) {
		int i = 0;

		for (Match match : matches) {
			if (match.getQueue() != null && (match.isFighting() || match.isStarting())) {
				if (match.getQueue() != null && match.getQueue().equals(queue)) {
					i = i + match.getTeamPlayers().size();
				}
			}
		}

		return i;
	}


	public MatchSnapshot getSnapshotOfPlayer(Player player){
		for(MatchSnapshot snapshot : getSnapshots()){
			if(snapshot.getTeamPlayer().getUuid().equals(player.getUniqueId())){
				return snapshot;
			}
		}
		return null;
	}
}

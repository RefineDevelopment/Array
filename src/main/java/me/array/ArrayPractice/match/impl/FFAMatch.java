package me.array.ArrayPractice.match.impl;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.nametag.NameTags;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rip.verse.jupiter.knockback.KnockbackModule;
import rip.verse.jupiter.knockback.KnockbackProfile;

public class FFAMatch extends Match {

	private final Team team;

	public FFAMatch(Team team, Kit kit, Arena arena) {
		super(null, kit, arena, QueueType.UNRANKED);

		this.team = team;
	}

	@Override
	public boolean isSoloMatch() {
		return false;
	}

	@Override
	public boolean isTeamMatch() {
		return false;
	}

	@Override
	public boolean isFreeForAllMatch() {
		return true;
	}

	@Override
	public boolean isHCFMatch() { return false; }

	@Override
	public boolean isKoTHMatch() { return false; }

	@Override
	public void setupPlayer(Player player) {
		TeamPlayer teamPlayer = getTeamPlayer(player);

		// If the player disconnected, skip any operations for them
		if (teamPlayer.isDisconnected()) {
			return;
		}

		teamPlayer.setAlive(true);

		PlayerUtil.reset(player);

		if (getKit().getGameRules().isSumo() || getKit().getGameRules().isParkour()) {
			PlayerUtil.denyMovement(player);
		}

		player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

		if (!getKit().getGameRules().isNoitems()) {
			for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
				player.getInventory().addItem(itemStack);
			}
		}
		if (getKit().getKnockbackProfile() != null) {
			KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get(getKit().getKnockbackProfile());
			((CraftPlayer) player).getHandle().setKnockback(knockbackProfile);
		}

		Team team = getTeam(player);

		Location spawn = getArena().getSpawnA();
		Location spawnB = getArena().getSpawnB();
		Location ffaspawn = getArena().getSpawnA();
		ffaspawn.setX(getAverage(spawn.getX(), spawnB.getX()));
		ffaspawn.setZ(getAverage(spawn.getZ(), spawnB.getZ()));

		if (getKit().getGameRules().isFfacenter()) {
			player.teleport(ffaspawn);
		} else {
			player.teleport(spawn);
		}

		for (Player enemy : team.getPlayers()) {
			NameTags.color(player, enemy, org.bukkit.ChatColor.RED, getKit().getGameRules().isShowHealth());
			Profile.getByUuid(enemy.getUniqueId()).handleVisibility();
		}
	}

	public double getAverage(double one, double two) {
		double three = one + two;
		three = three / 2;
		return three;
	}

	@Override
	public void cleanPlayer(Player player) {

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onEnd() {
		for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
			if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
				Player player = teamPlayer.getPlayer();

				if (player != null) {
					Profile profile = Profile.getByUuid(player.getUniqueId());
					profile.handleVisibility();

					getSnapshots().add(new MatchSnapshot(teamPlayer));
				}
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for (TeamPlayer firstTeamPlayer : team.getTeamPlayers()) {
					if (!firstTeamPlayer.isDisconnected()) {
						Player player = firstTeamPlayer.getPlayer();

						if (player != null) {

							if (firstTeamPlayer.isAlive()) {
								getSnapshots().add(new MatchSnapshot(firstTeamPlayer));
							}

							player.setFireTicks(0);
							player.updateInventory();
//							player.setKnockbackProfile(null);

							Profile profile = Profile.getByUuid(player.getUniqueId());
							profile.setState(ProfileState.IN_LOBBY);
							profile.setMatch(null);
							profile.refreshHotbar();
							profile.handleVisibility();

							Array.get().getEssentials().teleportToSpawn(player);
						}
					}
				}
			}
		}.runTaskLater(Array.get(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

		Player winningTeam = getWinningPlayer();

		ChatComponentBuilder winnerInventories = new ChatComponentBuilder("");
		winnerInventories.append("Winners: ").color(net.md_5.bungee.api.ChatColor.GREEN);

		ChatComponentBuilder loserInventories = new ChatComponentBuilder("");
		loserInventories.append("Losers: ").color(net.md_5.bungee.api.ChatColor.RED);

		winnerInventories.append(winningTeam.getName()).color(net.md_5.bungee.api.ChatColor.WHITE);
		winnerInventories.setCurrentHoverEvent(getHoverEvent(getTeamPlayer(winningTeam)))
				.setCurrentClickEvent(getClickEvent(getTeamPlayer(winningTeam)))
				.color(net.md_5.bungee.api.ChatColor.WHITE);

		for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
			if (teamPlayer.equals(getTeamPlayer(winningTeam))) continue;
			loserInventories.append(teamPlayer.getUsername()).color(net.md_5.bungee.api.ChatColor.WHITE);
			loserInventories.setCurrentHoverEvent(getHoverEvent(teamPlayer))
					.setCurrentClickEvent(getClickEvent(teamPlayer))
					.append(", ")
					.color(net.md_5.bungee.api.ChatColor.WHITE);
		}
		loserInventories.getCurrent().setText(loserInventories.getCurrent().getText().substring(0,
				loserInventories.getCurrent().getText().length() - 2));

		List<BaseComponent[]> components = new ArrayList<>();
		components.add(new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());
		components.add(new ChatComponentBuilder("").parse("&b&lMatch Details &7⎜ &7&o(Click name to view)").create());
		components.add(new ChatComponentBuilder("").parse("").create());
		components.add(winnerInventories.create());
		components.add(loserInventories.create());
		components.add(new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());

		for (Player player : getPlayersAndSpectators()) {
			Player.Spigot spigot = player.spigot();
			components.forEach(spigot::sendMessage);
		}
	}

	@Override
	public boolean canEnd() {
		return team.getAliveTeamPlayers().size() == 1;
	}

	@Override
	public void onDeath(Player player, Player killer) {
		TeamPlayer teamPlayer = getTeamPlayer(player);
		getSnapshots().add(new MatchSnapshot(teamPlayer));

		PlayerUtil.reset(player);

		if (!canEnd() && !teamPlayer.isDisconnected()) {
			player.teleport(getArena().getSpawnA());
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.refreshHotbar();
			player.setAllowFlight(true);
			player.setFlying(true);
			profile.setState(ProfileState.SPECTATE_MATCH);
		}
	}

	@Override
	public void onRespawn(Player player) {

	}

	@Override
	public Player getWinningPlayer() {
		if (getKit().getGameRules().isParkour()) {
			if (team.getDeadCount() == 1) {
				return team.getDeadTeamPlayers().get(0).getPlayer();
			} else {
				return null;
			}
		} else {
			if (team.getAliveTeamPlayers().size() == 1) {
				return team.getAliveTeamPlayers().get(0).getPlayer();
			} else {
				return null;
			}
		}
	}

	@Override
	public Team getWinningTeam() {
		throw new UnsupportedOperationException("Cannot get winning team from a FFA match");
	}

	@Override
	public TeamPlayer getTeamPlayerA() {
		throw new UnsupportedOperationException("Cannot get team player from a FFA match");
	}

	@Override
	public TeamPlayer getTeamPlayerB() {
		throw new UnsupportedOperationException("Cannot get team player from a FFA match");
	}

	@Override
	public List<TeamPlayer> getTeamPlayers() {
		throw new UnsupportedOperationException("Cannot get team player from a FFA match");
	}

	@Override
	public List<Player> getPlayers() {
		return team.getPlayers();
	}

	public List<Player> getAlivePlayers() {
		List<Player> players = new ArrayList<>();
		for (Player player : team.getPlayers()) {
			if (getTeamPlayer(player).isAlive()) {
				players.add(player);
			}
		}
		return players;
	}

	@Override
	public Team getTeamA() {
		throw new UnsupportedOperationException("Cannot get team from a FFA match");
	}

	@Override
	public Team getTeamB() {
		throw new UnsupportedOperationException("Cannot get team from a FFA match");
	}

	@Override
	public Team getTeam(Player player) {
		return team;
	}

	@Override
	public TeamPlayer getTeamPlayer(Player player) {
		for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
			if (teamPlayer.getUuid().equals(player.getUniqueId())) {
				return teamPlayer;
			}
		}

		return null;
	}

	@Override
	public Team getOpponentTeam(Team team) {
		throw new UnsupportedOperationException("Cannot get opponent team from a Juggernaut match");
	}

	@Override
	public Team getOpponentTeam(Player player) {
		throw new UnsupportedOperationException("Cannot get opponent team from a Juggernaut match");
	}

	@Override
	public Player getOpponentPlayer(Player player) {
		throw new IllegalStateException("Cannot get opponent player in Juggernaut match");
	}

	@Override
	public TeamPlayer getOpponentTeamPlayer(Player player) {
		throw new UnsupportedOperationException("Cannot get opponent team from a Juggernaut match");
	}

	@Override
	public int getRoundsNeeded(Team Team) {
		return 0;
	}

	@Override
	public int getRoundsNeeded(TeamPlayer teamPlayer) {
		return 0;
	}

	@Override
	public int getTotalRoundWins() {
		return 0;
	}



	@Override
	public int getTeamACapturePoints() {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public void setTeamACapturePoints(int number) {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public int getTeamBCapturePoints() {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public void setTeamBCapturePoints(int number) {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public int getTimer() {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public void setTimer(int number) {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public Player getCapper() {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public void setCapper(Player player) {
		throw new UnsupportedOperationException("No");
	}

	@Override
	public ChatColor getRelationColor(Player viewer, Player target) {
		return ChatColor.RED;
	}

}

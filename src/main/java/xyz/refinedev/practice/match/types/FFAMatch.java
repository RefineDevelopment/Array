package xyz.refinedev.practice.match.types;

import com.comphenix.protocol.events.PacketContainer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.location.Circle;
import xyz.refinedev.practice.util.other.EffectUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FFAMatch extends Match {

    private final Array plugin = Array.getInstance();

    private final Team team;

    public FFAMatch(Team team, Kit kit, Arena arena) {
        super(null, kit, arena, QueueType.UNRANKED);

        this.team = team;
    }

    @Override
    public boolean isFreeForAllMatch() {
        return true;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) {
            return;
        }

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (getKit().getGameRules().isStickSpawn() || getKit().getGameRules().isSumo() || getKit().getGameRules().isParkour()) PlayerUtil.denyMovement(player);

        if (!getKit().getGameRules().isNoItems() || !getKit().getGameRules().isSumo()) TaskUtil.runLater(() -> plugin.getProfileManager().getByUUID(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack)), 10L);

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));

        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Team team = getTeam(player);

        for (Player enemy : team.getPlayers()) {
            Profile enemyProfile = plugin.getProfileManager().getByPlayer(enemy);
            plugin.getProfileManager().handleVisibility(enemyProfile);
        }

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        int i = 0;
        for ( Player player : getPlayers() ) {
            Location midSpawn = this.getMidSpawn();

            List<Location> circleLocations = Circle.getCircle(midSpawn, plugin.getConfigHandler().getFFA_SPAWN_RADIUS(), this.getPlayers().size());
            Location center = midSpawn.clone();
            Location loc = circleLocations.get(i);
            Location target = loc.setDirection(center.subtract(loc).toVector());

            player.teleport(target.add(0, 0.5, 0));
            circleLocations.remove(i);
            i++;
        }
    }

    @Override
    public boolean onEnd() {
        for ( TeamPlayer teamPlayer : team.getTeamPlayers() ) {
            if (teamPlayer.isDisconnected() || !teamPlayer.isAlive()) continue;
            Player player = teamPlayer.getPlayer();
            if (player == null) continue;

            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            plugin.getProfileManager().handleVisibility(profile);

            this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for ( TeamPlayer firstTeamPlayer : team.getTeamPlayers() ) {
                    if (firstTeamPlayer.isDisconnected()) continue;
                    Player player = firstTeamPlayer.getPlayer();
                    if (player == null) continue;

                    if (firstTeamPlayer.isAlive()) {
                        getSnapshots().add(new MatchSnapshot(firstTeamPlayer));
                    }

                    player.setFireTicks(0);
                    player.updateInventory();

                    plugin.getSpigotHandler().resetKnockback(player);

                    Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
                    profile.setState(ProfileState.IN_LOBBY);
                    profile.setMatch(null);
                    plugin.getProfileManager().handleVisibility(profile);
                    plugin.getProfileManager().refreshHotbar(profile);
                    plugin.getProfileManager().teleportToSpawn(profile);
                }
            }
        }.runTaskLater(plugin, (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : plugin.getConfigHandler().getTELEPORT_DELAY() * 20L);
        return true;
    }

    @Override
    public boolean canEnd() {
        return getAlivePlayers().size() < 1;
    }

    @Override
    public void onDeath(Player deadPlayer, Player killer) {
        TeamPlayer teamPlayer = getTeamPlayer(deadPlayer);

        this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        PlayerUtil.reset(deadPlayer);

        for ( Player otherPlayer : getPlayers() ) {
            Profile profile = plugin.getProfileManager().getByUUID(otherPlayer.getUniqueId());
            TaskUtil.runLater(() -> plugin.getProfileManager().handleVisibility(profile, deadPlayer), 2L);
        }

        if (this.canEnd()) {
            this.end();
        } else {
            PlayerUtil.spectator(deadPlayer);
            if (!teamPlayer.isDisconnected()) {
                deadPlayer.teleport(getMidSpawn());

                Profile profile = plugin.getProfileManager().getByUUID(deadPlayer.getUniqueId());
                profile.setState(ProfileState.SPECTATING);
                plugin.getProfileManager().refreshHotbar(profile);
                plugin.getProfileManager().handleVisibility(profile);
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        player.teleport(player.getLocation().clone().add(0, 3, 0));
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
        throw new UnsupportedOperationException("Cannot getInstance winning team from a FFA match");
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        throw new UnsupportedOperationException("Cannot getInstance team player from a FFA match");
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        throw new UnsupportedOperationException("Cannot getInstance team player from a FFA match");
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        return team.getTeamPlayers();
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
        throw new UnsupportedOperationException("Cannot getInstance team from a FFA match");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot getInstance team from a FFA match");
    }

    @Override
    public Team getTeam(Player player) {
        return team;
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        return null;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a FFA match");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a FFA match");
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        throw new IllegalStateException("Cannot getInstance opponent player in FFA match");
    }

    @Override
    public List<BaseComponent[]> generateEndComponents(Player player) {
        return null;
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a FFA match");
    }

    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return ChatColor.GREEN;
        } else {
            if (team.containsPlayer(target)) {
                return ChatColor.RED;
            }
            return ChatColor.AQUA;
        }
    }

}

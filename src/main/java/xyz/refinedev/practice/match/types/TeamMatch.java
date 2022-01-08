package xyz.refinedev.practice.match.types;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamMatch extends Match {

    private final Array plugin = Array.getInstance();

    private final Team teamA;
    private final Team teamB;

    public TeamMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        super(null, kit, arena, QueueType.UNRANKED);

        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    public boolean isTeamMatch() {
        return true;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (this.getKit().getGameRules().isStickSpawn() || this.getKit().getGameRules().isSumo() || this.getKit().getGameRules().isParkour()) PlayerUtil.denyMovement(player);
        if (this.getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (this.getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        if (!this.getKit().getGameRules().isNoItems() || !this.getKit().getGameRules().isSumo()) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            TaskUtil.runLater(() -> profile.getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack)), 10L);
        }

        plugin.getSpigotHandler().kitKnockback(player, getKit());
        player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

        Team team = getTeam(player);

        Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        if (getPlayers().size() < 1) return;

        if (getKit().getGameRules().isTimed())
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!getState().equals(MatchState.FIGHTING))
                        return;

                    if (getDuration().equalsIgnoreCase("01:00")) {
                        onEnd();
                        cancel();
                    }
                }
            }.runTaskTimer(Array.getInstance(), 20L, 20L);
    }

    @Override
    public boolean onEnd() {
        for ( TeamPlayer teamPlayer : getTeamPlayers() ) {
            if (teamPlayer.isDisconnected() || !teamPlayer.isAlive()) continue;
            Player player = teamPlayer.getPlayer();
            if (player == null) continue;

            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            plugin.getProfileManager().handleVisibility(profile);

            this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer teamPlayer : getTeamPlayers()) {
                    if (teamPlayer.isDisconnected()) continue;

                    Player player = teamPlayer.getPlayer();
                    if (player == null) continue;

                    for ( TeamPlayer secondTeamPlayer : getTeamPlayers() ) {
                        if (secondTeamPlayer.isDisconnected()) continue;

                        if (secondTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                            continue;
                        }

                        Player secondPlayer = secondTeamPlayer.getPlayer();
                        if (secondPlayer == null) continue;

                        player.hidePlayer(secondPlayer);
                    }

                    if (teamPlayer.isAlive()) {
                        getSnapshots().add(new MatchSnapshot(teamPlayer));
                    }

                    player.setFireTicks(0);
                    player.getActivePotionEffects().clear();
                    player.updateInventory();

                    plugin.getSpigotHandler().resetKnockback(player);

                    Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
                    profile.setState(ProfileState.IN_LOBBY);
                    profile.setMatch(null);
                    plugin.getProfileManager().teleportToSpawn(profile);
                }
            }
        }.runTaskLater(plugin, TimeUtil.parseTime(plugin.getConfigHandler().getTELEPORT_DELAY()  + "s"));

        Team winningTeam = getWinningTeam();
        Team losingTeam = getOpponentTeam(winningTeam);

        winningTeam.getPlayers().stream().map(plugin.getProfileManager()::getProfileByPlayer).forEach(profile -> {
            profile.getStatisticsData().get(getKit()).incrementWon();
            plugin.getProfileManager().save(profile);
        });
        losingTeam.getPlayers().stream().map(plugin.getProfileManager()::getProfileByPlayer).forEach(profile -> {
            profile.getStatisticsData().get(getKit()).incrementLost();
            plugin.getProfileManager().save(profile);
        });
        return true;
    }

    @Override
    public boolean canEnd() {
        return teamA.getAliveTeamPlayers().isEmpty() || teamB.getAliveTeamPlayers().isEmpty();
    }

    @Override
    public void onDeath(Player deadPlayer, Player killer) {
        TeamPlayer teamPlayer = getTeamPlayer(deadPlayer);

        this.getSnapshots().add(new MatchSnapshot(teamPlayer));
        PlayerUtil.reset(deadPlayer);

        for ( Player otherPlayer : getPlayers() ) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(otherPlayer.getUniqueId());
            TaskUtil.runLater(() -> plugin.getProfileManager().handleVisibility(profile, deadPlayer), 2L);
        }

        if (this.canEnd()) {
            this.plugin.getMatchManager().end(this);
        } else {
            if (!teamPlayer.isDisconnected()) {
                deadPlayer.teleport(this.getMidSpawn());

                Profile profile = plugin.getProfileManager().getProfileByUUID(deadPlayer.getUniqueId());
                profile.setState(ProfileState.SPECTATING);
                plugin.getProfileManager().refreshHotbar(profile);
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
    }

    @Override
    public Player getWinningPlayer() {
        throw new UnsupportedOperationException("Cannot getInstance solo winning player from a TeamMatch");
    }

    @Override
    public Team getWinningTeam() {
        if (this.teamA.getAliveCount() == 0) {
            return this.teamB;
        }
        if (this.teamB.getAliveCount() == 0) {
            return this.teamA;
        }
        return null;
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        throw new UnsupportedOperationException("Cannot getInstance solo match player from a TeamMatch");
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        throw new UnsupportedOperationException("Cannot getInstance solo match player from a TeamMatch");
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        List<TeamPlayer> TeamPlayers = new ArrayList<>();
        TeamPlayers.addAll(teamA.getTeamPlayers());
        TeamPlayers.addAll(teamB.getTeamPlayers());
        return TeamPlayers;
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        teamA.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        });

        teamB.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                players.add(player);
            }
        });

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        teamA.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                if (TeamPlayer.isAlive()) {
                    players.add(player);
                }
            }
        });

        teamB.getTeamPlayers().forEach(TeamPlayer -> {
            Player player = TeamPlayer.getPlayer();

            if (player != null) {
                if (TeamPlayer.isAlive()) {
                    players.add(player);
                }
            }
        });

        return players;
    }

    @Override
    public Team getTeamA() {
        return teamA;
    }

    @Override
    public Team getTeamB() {
        return teamB;
    }

    @Override
    public Team getTeam(Player player) {
        for (TeamPlayer teamTeamPlayer : teamA.getTeamPlayers()) {
            if (teamTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamA;
            }
        }

        for (TeamPlayer teamTeamPlayer : teamB.getTeamPlayers()) {
            if (teamTeamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamB;
            }
        }

        return null;
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        for (TeamPlayer teamPlayer : teamA.getTeamPlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        for (TeamPlayer teamPlayer : teamB.getTeamPlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        return null;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        if (teamA.equals(team)) {
            return teamB;
        } else if (teamB.equals(team)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public Team getOpponentTeam(Player player) {
        if (teamA.containsPlayer(player)) {
            return teamB;
        } else if (teamB.containsPlayer(player)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance solo opponent player from TeamMatch");
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance solo opponent match player from TeamMatch");
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        boolean[] booleans = new boolean[]{
                getTeamA().containsPlayer(viewer),
                getTeamB().containsPlayer(viewer),
                getTeamA().containsPlayer(target),
                getTeamB().containsPlayer(target)
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer)) {
            return getTeamA().containsPlayer(target) ?  org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return ChatColor.AQUA;
        }
    }

    @Override
    public List<BaseComponent[]> generateEndComponents(Player player) {
        List<BaseComponent[]> componentsList = new ArrayList<>();

        for ( String line : Locale.MATCH_INVENTORY_MESSAGE.toList() ) {
            if (line.equalsIgnoreCase("<inventories>")) {

                BaseComponent[] winners = this.plugin.getMatchManager().generateInventoriesComponents(Locale.MATCH_INVENTORY_WINNERS.toString(), getWinningTeam().getTeamPlayers());
                BaseComponent[] losers = this.plugin.getMatchManager().generateInventoriesComponents(Locale.MATCH_INVENTORY_LOSERS.toString(), getOpponentTeam(getWinningTeam()).getTeamPlayers());

                componentsList.add(winners);
                componentsList.add(losers);

                continue;
            }

            if (line.equalsIgnoreCase("<elo_changes>")) {
                continue;
            }

            componentsList.add(new ChatComponentBuilder("").parse(line).create());
        }

        return componentsList;
    }

}

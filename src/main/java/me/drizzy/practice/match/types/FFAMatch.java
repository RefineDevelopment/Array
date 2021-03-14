package me.drizzy.practice.match.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.array.essentials.Essentials;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.util.Circle;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import me.drizzy.practice.util.nametag.NameTags;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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
    public boolean isSumoTeamMatch() {
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
    public boolean isHCFMatch() {
        return false;
    }

    @Override
    public boolean isSumoMatch() {
        return false;
    }

    @Override
    public boolean isTheBridgeMatch() {
        return false;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        // If the player disconnected, skip any operations for them
        if (teamPlayer.isDisconnected()) {
            return;
        }
        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (!getKit().getGameRules().isCombo()) {
            player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());
        }
        if (getKit().getGameRules().isCombo()) {
            player.setMaximumNoDamageTicks(0);
            player.setNoDamageTicks(3);
        }


        if (getKit().getGameRules().isStickspawn()) {
            PlayerUtil.denyMovement(player);
        }

        if (getKit().getGameRules().isInfinitespeed()) {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 2));
        }
        if (getKit().getGameRules().isInfinitestrength()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 2));
        }

        if (!getKit().getGameRules().isNoitems()) {
            Profile.getByUuid(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }

        Array.getInstance().getKnockbackManager().getKnockbackType().appleKitKnockback(player, getKit());

        Team team = getTeam(player);
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
        int i = 0;
        for ( Player player : getPlayers() ) {
            Location midSpawn = this.getMidSpawn();
            List<Location> circleLocations = Circle.getCircle(midSpawn, 7, this.getPlayers().size());
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
                    //Check if they didn't disconnect
                    if (!firstTeamPlayer.isDisconnected()) {
                        Player player = firstTeamPlayer.getPlayer();

                        //Add Their Snapshot
                        if (player != null) {
                            if (firstTeamPlayer.isAlive()) {
                                getSnapshots().add(new MatchSnapshot(firstTeamPlayer));
                            }

                            //Reset the Player
                            player.setFireTicks(0);
                            player.updateInventory();
                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.handleVisibility();
                            PlayerUtil.reset(player, false);
                            profile.refreshHotbar();
                            //Reset their Knockback Profile and Teleport them to Spawn
                            Array.getInstance().getKnockbackManager().getKnockbackType().appleKitKnockback(player, getKit());
                            Essentials.teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Player winningTeam = getWinningPlayer();

        ChatComponentBuilder winnerInventories = new ChatComponentBuilder("");
        winnerInventories.append("Winners: ").color(net.md_5.bungee.api.ChatColor.GREEN);

        ChatComponentBuilder loserInventories = new ChatComponentBuilder("");
        loserInventories.append("Losers: ").color(net.md_5.bungee.api.ChatColor.RED);

        winnerInventories.append(winningTeam.getName()).color(net.md_5.bungee.api.ChatColor.GREEN);
        winnerInventories.setCurrentHoverEvent(getHoverEvent(getTeamPlayer(winningTeam)))
                .setCurrentClickEvent(getClickEvent(getTeamPlayer(winningTeam))).color(net.md_5.bungee.api.ChatColor.GREEN);

        for (TeamPlayer teamPlayer : team.getTeamPlayers()) {
            if (teamPlayer.equals(getTeamPlayer(winningTeam))) continue;
            loserInventories.append(teamPlayer.getUsername()).color(net.md_5.bungee.api.ChatColor.RED);
            loserInventories.setCurrentHoverEvent(getHoverEvent(teamPlayer))
                    .setCurrentClickEvent(getClickEvent(teamPlayer))
                    .append(", ")
                    .color(net.md_5.bungee.api.ChatColor.GRAY);
        }
        loserInventories.getCurrent().setText(loserInventories.getCurrent().getText().substring(0,
                loserInventories.getCurrent().getText().length() - 2));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());
        components.add(new ChatComponentBuilder("").parse("&b&lMatch Details &7(Click name to view inventory)").create());
        components.add(winnerInventories.create());
        components.add(loserInventories.create());
        components.add(new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());

        for (Player player : getPlayersAndSpectators()) {
            components.forEach(components1 -> player.spigot().sendMessage(components1));
        }
        return true;
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
            player.teleport(getArena().getSpawn1());
            Profile profile = Profile.getByUuid(player.getUniqueId());
            PlayerUtil.reset(player, false);
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
        throw new UnsupportedOperationException("Cannot getInstance winning team from a Juggernaut match");
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        throw new UnsupportedOperationException("Cannot getInstance team player from a Juggernaut match");
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        throw new UnsupportedOperationException("Cannot getInstance team player from a Juggernaut match");
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        throw new UnsupportedOperationException("Cannot getInstance team player from a Juggernaut match");
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
        throw new UnsupportedOperationException("Cannot getInstance team from a Juggernaut match");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot getInstance team from a Juggernaut match");
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
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a Juggernaut match");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a Juggernaut match");
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        throw new IllegalStateException("Cannot getInstance opponent player in Juggernaut match");
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a Juggernaut match");
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
    public ChatColor getRelationColor(Player viewer, Player target) {
        return ChatColor.RED;
    }

}

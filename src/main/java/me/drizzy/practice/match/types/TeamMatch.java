package me.drizzy.practice.match.types;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.MatchState;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.util.other.NameTags;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.ChatComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    public TeamMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        super(null, kit, arena, QueueType.UNRANKED);

        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    public boolean isSoloMatch() {
        return false;
    }

    @Override
    public boolean isTeamMatch() {
        return true;
    }

    @Override
    public boolean isFreeForAllMatch() {
        return false;
    }

    @Override
    public boolean isHCFMatch() {
        return false;
    }

    @Override
    public boolean isTheBridgeMatch() {
        return false;
    }

    @Override
    public boolean isRobotMatch() {
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

        if (getKit().getGameRules().isStickSpawn() || getKit().getGameRules().isSumo()) {
            PlayerUtil.denyMovement(player);
        }

        if (!getKit().getGameRules().isCombo()) {
            player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());
        }

        if (getKit().getGameRules().isCombo()) {
            player.setMaximumNoDamageTicks(0);
            player.setNoDamageTicks(3);
        }

        if (getKit().getGameRules().isInfiniteSpeed()) {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 2));
        }
        if (getKit().getGameRules().isInfiniteStrength()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 2));
        }

        if (!getKit().getGameRules().isNoItems()) {
            Profile profile = Profile.getByPlayer(player);
            profile.getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }

        Array.getInstance().getNMSManager().getKnockbackType().appleKitKnockback(player, getKit());

        Team team = getTeam(player);

        for (Player friendly : team.getPlayers()) {
            NameTags.color(player, friendly, org.bukkit.ChatColor.GREEN, getKit().getGameRules().isShowHealth());
        }

        for (Player enemy : getOpponentTeam(team).getPlayers()) {
            NameTags.color(player, enemy, org.bukkit.ChatColor.RED, getKit().getGameRules().isShowHealth());
        }


        Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }
        teamPlayer.setPlayerSpawn(spawn);
    }

    @Override
    public void onStart() {
        if (getPlayers().size() < 1) {
            return;
        }

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
                if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                    Player player=teamPlayer.getPlayer();

                    if (player != null) {
                        Profile profile=Profile.getByUuid(player.getUniqueId());
                        profile.handleVisibility();

                        getSnapshots().add(new MatchSnapshot(teamPlayer));
                    }
                }
            }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer firstTeamPlayer : getTeamPlayers()) {
                    if (!firstTeamPlayer.isDisconnected()) {
                        Player player = firstTeamPlayer.getPlayer();

                        if (player != null) {
                            for (TeamPlayer secondTeamPlayer : getTeamPlayers()) {
                                if (secondTeamPlayer.isDisconnected()) {
                                    continue;
                                }

                                if (secondTeamPlayer.getUuid().equals(player.getUniqueId())) {
                                    continue;
                                }

                                Player secondPlayer = secondTeamPlayer.getPlayer();

                                if (secondPlayer != null) {
                                    player.hidePlayer(secondPlayer);
                                }
                            }

                            if (firstTeamPlayer.isAlive()) {
                                getSnapshots().add(new MatchSnapshot(firstTeamPlayer));
                            }

                            player.setFireTicks(0);
                            player.updateInventory();

                            NameTags.reset(player, firstTeamPlayer.getPlayer());

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.refreshHotbar();
                            profile.handleVisibility();
                            Array.getInstance().getNMSManager().getKnockbackType().applyDefaultKnockback(player);
                            profile.teleportToSpawn();
                        }
                    }
                }
            }
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : 70L);

        Team winningTeam = getWinningTeam();
        Team losingTeam = getOpponentTeam(winningTeam);

        ChatComponentBuilder winnerInventories = new ChatComponentBuilder("");
        winnerInventories.append("Winners: ").color(ChatColor.GREEN);

        ChatComponentBuilder loserInventories = new ChatComponentBuilder("");
        loserInventories.append("Losers: ").color(ChatColor.RED);

        for (TeamPlayer teamPlayer : winningTeam.getTeamPlayers()) {
            winnerInventories.append(teamPlayer.getUsername()).color(ChatColor.WHITE);
            winnerInventories.setCurrentHoverEvent(getHoverEvent(teamPlayer)).setCurrentClickEvent(getClickEvent(teamPlayer)).append(", ").color(ChatColor.RED);
        }

        for (TeamPlayer teamPlayer : losingTeam.getTeamPlayers()) {
            loserInventories.append(teamPlayer.getUsername()).color(ChatColor.WHITE);
            loserInventories.setCurrentHoverEvent(getHoverEvent(teamPlayer))
                    .setCurrentClickEvent(getClickEvent(teamPlayer))
                    .append(", ")
                    .color(ChatColor.GRAY);
        }

        winnerInventories.getCurrent().setText(winnerInventories.getCurrent().getText().substring(0,
                winnerInventories.getCurrent().getText().length() - 2));
        loserInventories.getCurrent().setText(loserInventories.getCurrent().getText().substring(0,
                loserInventories.getCurrent().getText().length() - 2));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());
        components.add(new ChatComponentBuilder("").parse(Locale.MATCH_INVENTORY_MESSAGE_TITLE.toString()).create());
        components.add(new ChatComponentBuilder("").create());
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
        return teamA.getAliveTeamPlayers().isEmpty() || teamB.getAliveTeamPlayers().isEmpty();
    }

    @Override
    public void onDeath(Player player, Player killer) {

        TeamPlayer teamPlayer = getTeamPlayer(player);

        getSnapshots().add(new MatchSnapshot(teamPlayer));

        PlayerUtil.reset(player);

        if (!canEnd() && !teamPlayer.isDisconnected()) {
            Team team = getTeam(player);
            Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();
            player.teleport(spawn);
            player.setAllowFlight(true);
            player.setFlying(true);
            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.refreshHotbar();
            profile.setState(ProfileState.SPECTATING);
        }
    }

    @Override
    public void onRespawn(Player player) {
        if (getKit().getGameRules().isSumo() && !isEnding()) {
            for (TeamPlayer teamPlayer : teamA.getTeamPlayers()) {
                if (teamPlayer.isDisconnected()) {
                    continue;
                }

                Player toPlayer = teamPlayer.getPlayer();

                if (toPlayer != null && toPlayer.isOnline()) {
                    toPlayer.teleport(getArena().getSpawn1());
                }
            }

            for (TeamPlayer teamPlayer : teamB.getTeamPlayers()) {
                if (teamPlayer.isDisconnected()) {
                    continue;
                }

                Player toPlayer = teamPlayer.getPlayer();

                if (toPlayer != null && toPlayer.isOnline()) {
                    toPlayer.teleport(getArena().getSpawn2());
                }
            }
        } else {
            player.teleport(player.getLocation().clone().add(0, 3, 0));
        }
    }

    @Override
    public Player getWinningPlayer() {
        throw new UnsupportedOperationException("Cannot getInstance solo winning player from a TeamMatch");
    }

    @Override
    public Team getWinningTeam() {
        if (getKit().getGameRules().isTimed()) {
            if (teamA.getAliveTeamPlayers().isEmpty()) {
                return teamB;
            } else if (teamB.getAliveTeamPlayers().isEmpty()) {
                return teamA;
            } else if (teamA.getTotalHits() > teamB.getTotalHits()) {
                return teamA;
            } else {
                return teamB;
            }
        } else if (getKit().getGameRules().isParkour()) {
            if (teamA.getDeadCount() > 0) {
                return teamA;
            } else {
                return teamB;
            }
        } else {
            if (teamA.getAliveTeamPlayers().isEmpty()) {
                return teamB;
            } else if (teamB.getAliveTeamPlayers().isEmpty()) {
                return teamA;
            } else {
                return null;
            }
        }
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
            if (teamTeamPlayer.getUuid().equals(player.getUniqueId())) {
                return teamA;
            }
        }

        for (TeamPlayer teamTeamPlayer : teamB.getTeamPlayers()) {
            if (teamTeamPlayer.getUuid().equals(player.getUniqueId())) {
                return teamB;
            }
        }

        return null;
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        for (TeamPlayer teamPlayer : teamA.getTeamPlayers()) {
            if (teamPlayer.getUuid().equals(player.getUniqueId())) {
                return teamPlayer;
            }
        }

        for (TeamPlayer teamPlayer : teamB.getTeamPlayers()) {
            if (teamPlayer.getUuid().equals(player.getUniqueId())) {
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

        Team team = getTeam(target);
        Team viewerTeam = getTeam(viewer);

        if (team == null || viewerTeam == null) {
            return org.bukkit.ChatColor.RED;
        }

        if (team.equals(viewerTeam)) {
            return org.bukkit.ChatColor.GREEN;
        } else {
            return org.bukkit.ChatColor.RED;
        }
    }

}

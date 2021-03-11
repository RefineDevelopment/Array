package me.drizzy.practice.match.types;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.MatchState;
import me.drizzy.practice.match.task.MatchStartTask;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import me.drizzy.practice.util.nametag.NameTags;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.drizzy.practice.array.essentials.Essentials.teleportToSpawn;

@Getter
public class SumoTeamMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    public SumoTeamMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
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
        return false;
    }

    @Override
    public boolean isFreeForAllMatch() {
        return false;
    }

    @Override
    public boolean isSumoTeamMatch() {
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
        
        Profile.getByUuid(player.getUniqueId()).setState(ProfileState.IN_FIGHT);

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

		player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

        if (!getKit().getGameRules().isNoitems()) {
            Profile.getByUuid(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }

        Array.getInstance().getKnockbackManager().getKnockbackType().appleKitKnockback(player, getKit());

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
    }

    @Override
    public void cleanPlayer(Player player) {

    }

    @Override
    public void onStart() {
    }

    @Override
    public boolean onEnd() {
        for (TeamPlayer teamPlayer : getTeamPlayers()) {
            if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                Player player = teamPlayer.getPlayer();

                if (player != null) {
                    if (teamPlayer.isAlive()) {
                        MatchSnapshot snapshot = new MatchSnapshot(teamPlayer);
                        getSnapshots().add(snapshot);
                    }
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

                                NameTags.reset(player, secondPlayer);
                            }

                            if (firstTeamPlayer.isAlive()) {
                                getSnapshots().add(new MatchSnapshot(firstTeamPlayer));
                            }

                            player.setFireTicks(0);
                            player.updateInventory();

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.handleVisibility();
                            Array.getInstance().getKnockbackManager().getKnockbackType().applyDefaultKnockback(player);
                            teleportToSpawn(player);
                            PlayerUtil.reset(player, false);
                            profile.refreshHotbar();
                        }
                    }
                }
            }
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Team winningTeam = getWinningTeam();
        Team losingTeam = getOpponentTeam(winningTeam);

        ChatComponentBuilder winnerInventories = new ChatComponentBuilder("");
        winnerInventories.append("Winners: ").color(ChatColor.GREEN);

        ChatComponentBuilder loserInventories = new ChatComponentBuilder("");
        loserInventories.append("Losers: ").color(ChatColor.RED);

        for (TeamPlayer teamPlayer : winningTeam.getTeamPlayers()) {
            winnerInventories.append(teamPlayer.getUsername()).color(ChatColor.GREEN);
            winnerInventories.setCurrentHoverEvent(getHoverEvent(teamPlayer)).setCurrentClickEvent(getClickEvent(teamPlayer)).append(", ").color(ChatColor.AQUA);
        }

        for (TeamPlayer teamPlayer : losingTeam.getTeamPlayers()) {
            loserInventories.append(teamPlayer.getUsername()).color(ChatColor.RED);
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
        components.add(new ChatComponentBuilder("").parse("&b&lMatch Details &7(Click name to view inventory)").create());
        components.add(new ChatComponentBuilder("").create());
        components.add(winnerInventories.create());
        components.add(loserInventories.create());
        components.add(new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());

        for (Player player : getPlayersAndSpectators()) {
            components.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        teamA.setSumoRounds(0);
        teamB.setSumoRounds(0);

        return true;
    }

    @Override
    public boolean canEnd() {
        if (getRoundsNeeded(teamA) == 0 || getRoundsNeeded(teamB) == 0)
            return true;
        return teamA.getAliveTeamPlayers().isEmpty() || teamB.getAliveTeamPlayers().isEmpty();
    }

    @Override
    public Player getWinningPlayer() {
        throw new UnsupportedOperationException("Cannot getInstance solo winning player from a TeamMatch");
    }

    @Override
    public Team getWinningTeam() {
        if (getKit().getGameRules().isSumo()) {
            if (teamA.getDisconnectedCount() == teamA.getTeamPlayers().size()) {
                return teamB;
            } else if (teamB.getDisconnectedCount() == teamB.getTeamPlayers().size()) {
                return teamA;
            }

            return teamA.getSumoRounds() == 3 ? teamA : teamB;
        } else {
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
    public int getTotalRoundWins() {
        return teamA.getSumoRounds() + teamB.getSumoRounds();
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        throw new UnsupportedOperationException("Cannot getInstance teamplayer round wins from SumoTeamMatch");
    }

    @Override
    public int getRoundsNeeded(Team team) {
        if (teamA.equals(team)) {
            return 3 - teamA.getSumoRounds();
        } else if (teamB.equals(team)) {
            return 3 - teamB.getSumoRounds();
        } else {
            return -1;
        }
    }

    @Override
    public void onDeath(Player player, Player killer) {

        TeamPlayer dyingTeam = getTeamPlayer(player);
        getSnapshots().add(new MatchSnapshot(dyingTeam));

        PlayerUtil.reset(player);

        if (!canEnd() && !dyingTeam.isDisconnected()) {
            Team team = getTeam(player);
            Location spawn = team.equals(teamA) ? getArena().getSpawn1() : getArena().getSpawn2();
            player.teleport(spawn);
            Profile profile = Profile.getByUuid(player.getUniqueId());
            PlayerUtil.reset(player, false);
            profile.refreshHotbar();
            player.setAllowFlight(true);
            player.setFlying(true);
            profile.setState(ProfileState.SPECTATE_MATCH);
        }


        if (teamA.getAliveTeamPlayers().size() == 0 || teamB.getAliveTeamPlayers().size() == 0) {
            if(teamA.getDisconnectedCount() == teamA.getPlayers().size() || teamB.getDisconnectedCount() == teamB.getPlayers().size())  {
                end();
                return;
            }
            if (getRoundsNeeded(teamA) != 0 || getRoundsNeeded(teamB) != 0) {
                if (getOpponentTeam(player).getLeader().getUuid().toString().equals(teamA.getLeader().getUuid().toString())) {
                    teamA.setSumoRounds(teamA.getSumoRounds() + 1);
                } else if (getOpponentTeam(player).getLeader().getUuid().toString().equals(teamB.getLeader().getUuid().toString())) {
                    teamB.setSumoRounds(teamB.getSumoRounds() + 1);
                }
                getOpponentTeam(player).getTeamPlayers().forEach(teamPlayer -> teamPlayer.getPlayer().sendMessage(CC.translate("&aYou have won this round!")));
                getTeam(player).getTeamPlayers().forEach(teamPlayer -> teamPlayer.getPlayer().sendMessage(CC.translate("&cYou have lost this round!")));


                if (teamA.getSumoRounds() >= 3 || teamB.getSumoRounds() >= 3) {
                    end();
                } else {
                    teamA.broadcast(CC.translate("&fYou need to win &b" + getRoundsNeeded(teamA) + " &fmore rounds!"));
                    teamB.broadcast(CC.translate("&fYou need to win &b" + getRoundsNeeded(teamB) + " &fmore rounds!"));

                    teamA.getPlayers().forEach(this::setupPlayer);
                    teamB.getPlayers().forEach(this::setupPlayer);

                    //Bug where both players couldn't see each other sometimes, fix for that is here
                    this.getPlayers().forEach(p -> this.getPlayers().forEach(otherPlayer -> {
                        if (!p.equals(otherPlayer)) {
                            p.showPlayer(otherPlayer);
                            otherPlayer.showPlayer(p);
                        }
                    }));

                    //Just in case.
                    onStart();
                    setState(MatchState.STARTING);
                    setStartTimestamp(-1);

                    //duh
                    new MatchStartTask(this).runTaskTimer(Array.getInstance(), 20L, 20L);
                }
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        teleportToSpawn(player);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        Team team = getTeam(target);
        Team viewerTeam = getTeam(viewer);

        if (team == null || viewerTeam == null) {
            return org.bukkit.ChatColor.AQUA;
        }

        if (team.equals(viewerTeam)) {
            return org.bukkit.ChatColor.GREEN;
        } else {
            return org.bukkit.ChatColor.RED;
        }
    }

}

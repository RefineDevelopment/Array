package me.array.ArrayPractice.match.impl;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.match.MatchState;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.nametag.NameTags;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KoTHMatch extends Match {

    private final Team teamA;
    private final Team teamB;
    private int teamAKoTHPoints = 0;
    private int teamBKoTHPoints = 0;
    private Player capper = null;
    private int timer = 20;

    public KoTHMatch(Team teamA, Team teamB, Arena arena) {
        super(null, Kit.getByName("NoDebuff"), arena, QueueType.UNRANKED);

        this.teamA = teamA;
        this.teamB = teamB;
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
        return false;
    }

    @Override
    public boolean isHCFMatch() {
        return false;
    }

    @Override
    public boolean isKoTHMatch() {
        return true;
    }

    @Override
    public boolean isSumoMatch() {
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

        Profile.getByUuid(player.getUniqueId()).getKitData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));

        Profile.getByUuid(player.getUniqueId()).setState(ProfileState.IN_FIGHT);

        Team team = getTeam(player);

        for (Player friendly : team.getPlayers()) {
            NameTags.color(player, friendly, org.bukkit.ChatColor.GREEN, false);
        }

        for (Player enemy : getOpponentTeam(team).getPlayers()) {
            NameTags.color(player, enemy, org.bukkit.ChatColor.RED, false);
        }

        for (Player player1 : getPlayers()) {
            Profile profile1 = Profile.getByUuid(player1.getUniqueId());
            profile1.handleVisibility();
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getState().equals(MatchState.FIGHTING))
                    return;

                if (getCapper() == null || !isCapperOnline()) {
                    setCapper(null);
                    setTimer(20);
                    return;
                }

                setTimer(getTimer() - 1);

                if (getTimer() <= 0) {
                    getTeam(getCapper()).setKothPoints(getTeam(getCapper()).getKothPoints() + 1);
                    setTimer(20);
                    if (canEnd()) {
                        onEnd();
                    }
                }
            }
        }.runTaskTimerAsynchronously(Practice.getInstance(), 2L, 20L);
    }

    private boolean isCapperOnline() {
        Player player = capper;

        return player != null && player.isOnline();
    }

    @Override
    public boolean onEnd() {
        for (TeamPlayer teamPlayer : getTeamPlayers()) {
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
                            //player.setKnockbackProfile(null);

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.refreshHotbar();
                            profile.handleVisibility();

                            Practice.getInstance().getEssentials().teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLater(Practice.getInstance(), 40L);

        Team winningTeam = getWinningTeam();
        Team losingTeam = getOpponentTeam(winningTeam);

        ChatComponentBuilder winnerInventories = new ChatComponentBuilder("");
        winnerInventories.append("Winners (Points: " + winningTeam.getKothPoints() + "): ").color(ChatColor.GREEN);

        ChatComponentBuilder loserInventories = new ChatComponentBuilder("");
        loserInventories.append("Losers (Points: " + losingTeam.getKothPoints() + "): ").color(ChatColor.RED);

        for (TeamPlayer teamPlayer : winningTeam.getTeamPlayers()) {
            winnerInventories.append(teamPlayer.getUsername()).color(ChatColor.WHITE).append(", ").color(ChatColor.WHITE);
        }

        for (TeamPlayer teamPlayer : losingTeam.getTeamPlayers()) {
            loserInventories.append(teamPlayer.getUsername()).color(ChatColor.WHITE).append(", ").color(ChatColor.WHITE);
        }

        winnerInventories.getCurrent().setText(winnerInventories.getCurrent().getText().substring(0,
                winnerInventories.getCurrent().getText().length() - 2));
        loserInventories.getCurrent().setText(loserInventories.getCurrent().getText().substring(0,
                loserInventories.getCurrent().getText().length() - 2));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());
        components.add(new ChatComponentBuilder("").parse("&bPost Match Inventories &7(Click to view)").create());
        components.add(new ChatComponentBuilder("").parse("").create());
        components.add(winnerInventories.create());
        components.add(loserInventories.create());
        components.add(new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());

        for (Player player : getPlayersAndSpectators()) {
            components.forEach(components1 -> player.spigot().sendMessage(components1));
        }
        return true;
    }

    @Override
    public boolean canEnd() {
        return (teamA.getKothPoints() == 5 || teamB.getKothPoints() == 5) || (teamA.getNonDisconnectedCount() == 0 || teamB.getNonDisconnectedCount() == 0);
    }

    @Override
    public void onDeath(Player player, Player killer) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (!teamPlayer.isDisconnected()) {

            Profile profile = Profile.getByUuid(player.getUniqueId());
            profile.refreshHotbar();
            for (Player player1 : getPlayers()) {
                Profile profile1 = Profile.getByUuid(player1.getUniqueId());
                profile1.handleVisibility();
            }
            profile.setState(ProfileState.SPECTATE_MATCH);
            PlayerUtil.spectator(player);
            player.sendMessage(CC.translate("&cYou are now spectating for 8 seconds."));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!teamPlayer.isDisconnected()) {
                        setupPlayer(player);
                    } else {
                        canEnd();
                    }
                }
            }.runTaskLater(Practice.getInstance(), 160L);
        } else {
            canEnd();
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
        if (teamA.getKothPoints() == 5) {
            return teamA;
        } else if (teamB.getKothPoints() == 5) {
            return teamB;
        } else if (teamA.getNonDisconnectedCount() == 0) {
            return teamB;
        } else if (teamB.getNonDisconnectedCount() == 0) {
            return teamA;
        } else {
            return null;
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
        throw new UnsupportedOperationException("Cannot getInstance solo rounds needed from TeamMatch");
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        throw new UnsupportedOperationException("Cannot getInstance solo rounds needed from TeamMatch");
    }

    @Override
    public int getRoundsNeeded(Team Team) {
        throw new UnsupportedOperationException("Cannot getInstance solo rounds needed from TeamMatch");
    }

    @Override
    public int getTeamACapturePoints() {
        return this.teamAKoTHPoints;
    }

    @Override
    public void setTeamACapturePoints(int number) {
        this.teamAKoTHPoints = number;
    }

    @Override
    public int getTeamBCapturePoints() {
        return this.teamAKoTHPoints;
    }

    @Override
    public void setTeamBCapturePoints(int number) {
        this.teamBKoTHPoints = number;
    }

    @Override
    public int getTimer() {
        return this.timer;
    }

    @Override
    public void setTimer(int number) {
        this.timer = number;
    }

    @Override
    public Player getCapper() {
        return this.capper;
    }

    @Override
    public void setCapper(Player player) {
        this.capper = player;
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

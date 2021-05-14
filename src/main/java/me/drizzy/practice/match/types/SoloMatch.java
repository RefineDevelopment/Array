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
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.ChatComponentBuilder;
import me.drizzy.practice.util.elo.EloUtil;
import me.drizzy.practice.util.other.NameTags;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.other.TaskUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class SoloMatch extends Match {

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;

    public SoloMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
    }

    @Override
    public boolean isSoloMatch() {
        return true;
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

        if (teamPlayer.isDisconnected()) {
            return;
        }

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (getKit().getGameRules().isStickSpawn() || getKit().getGameRules().isSumo() || getKit().getGameRules().isParkour()) {
            PlayerUtil.denyMovement(player);
        }

        if (!getKit().getGameRules().isNoItems() || !getKit().getGameRules().isSumo()) {
            TaskUtil.runLater(() -> Profile.getByUuid(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack)), 10L);
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
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 1));
        }

        Array.getInstance().getNMSManager().getKnockbackType().appleKitKnockback(player, getKit());

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }
        teamPlayer.setPlayerSpawn(spawn);

        if (getKit().getGameRules().isParkour()) {
            teamPlayer.setParkourCheckpoint(spawn);
        }

        NameTags.color(player, this.getOpponentPlayer(player), org.bukkit.ChatColor.RED, this.getKit().getGameRules().isBuild() || this.getKit().getGameRules().isShowHealth());
    }

    @Override
    public void onStart() {
        if (getKit().getGameRules().isTimed()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!getState().equals(MatchState.FIGHTING))
                        return;

                    if (getDuration().equalsIgnoreCase("01:00") || (getDuration().equalsIgnoreCase("01:01") && getState().equals(MatchState.FIGHTING)) || (getDuration().equalsIgnoreCase("01:02") && getState().equals(MatchState.FIGHTING))) {
                        onEnd();
                        cancel();
                    }
                }
            }.runTaskTimer(Array.getInstance(), 20L, 20L);
        }
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey = UUID.randomUUID();

        for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
            if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                Player player = teamPlayer.getPlayer();

                if (player != null) {
                    if (teamPlayer.isAlive()) {
                        MatchSnapshot snapshot = new MatchSnapshot(teamPlayer);
                        snapshot.setSwitchTo(getOpponentTeamPlayer(player));

                        getSnapshots().add(snapshot);
                    }
                }
            }
        }

        if (getKit().getGameRules().isTimed()) {
            TeamPlayer roundLoser = getTeamPlayer(getWinningPlayer());
            TeamPlayer roundWinner = getOpponentTeamPlayer(getOpponentPlayer(getWinningPlayer()));
            getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
            this.setState(MatchState.ENDING);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
                    if (!teamPlayer.isDisconnected()) {
                        Player player = teamPlayer.getPlayer();
                        Player opponent = getOpponentPlayer(player);

                        if (player != null) {

                            player.setFireTicks(0);
                            player.updateInventory();

                            NameTags.reset(player, opponent);

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.refreshHotbar();
                            profile.handleVisibility();
                            Array.getInstance().getNMSManager().getKnockbackType().applyDefaultKnockback(player);
                            if (opponent != null) {
                                profile.setRematchData(new ProfileRematchData(rematchKey, player.getUniqueId(),
                                opponent.getUniqueId(), getKit(), getArena()));
                            }
                            profile.teleportToSpawn();
                        }
                    }
                }
            }
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : 70L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);


        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");

        inventoriesBuilder.append("Winner: ").color(ChatColor.GREEN).append(winningPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer)).setCurrentClickEvent(getClickEvent(winningTeamPlayer)).append(" - ").color(ChatColor.GRAY).append("Loser: ").color(ChatColor.RED).append(losingPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer)).setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse(Locale.MATCH_INVENTORY_MESSAGE_TITLE.toString()).create());
        components.add(inventoriesBuilder.create());


        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            winningProfile.getStatisticsData().get(getKit()).incrementWon();
            losingProfile.getStatisticsData().get(getKit()).incrementLost();
        }


        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();

            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

            winningProfile.getStatisticsData().get(getKit()).setElo(newWinnerElo);
            losingProfile.getStatisticsData().get(getKit()).setElo(newLoserElo);

            winningProfile.getStatisticsData().get(getKit()).incrementWon();
            losingProfile.getStatisticsData().get(getKit()).incrementLost();

            winningProfile.calculateGlobalElo();
            winningProfile.save();

            losingProfile.calculateGlobalElo();
            losingProfile.save();

            int winnerEloChange=newWinnerElo - oldWinnerElo;
            int loserEloChange=oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&a" + winningPlayer.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &7⎜ &c" + losingPlayer.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());
        }

        StringBuilder builder = new StringBuilder();

        if (!(getSpectators().size() <= 0)) {
            List<Player> specs = new ArrayList<>(getSpectators());
            int i = 0;
            for (Player spectator : getSpectators()) {
                if (getSpectators().size() >= 1) {
                    if (!specs.contains(spectator))
                        specs.add(spectator);

                    if (i != getSpectators().size()) {
                        i++;
                        if (i == getSpectators().size()) {
                            builder.append(CC.GRAY).append(spectator.getName());
                        } else {
                            builder.append(CC.GRAY).append(spectator.getName()).append(CC.GRAY).append(", ");
                        }

                    }
                }
            }
            if (specs.size() >= 1) {
                components.add(new ChatComponentBuilder("").parse("&cSpectators (" + specs.size() + "): &7" + builder.substring(0, builder.length())).create());
            }
        }

        List<BaseComponent[]> CHAT_BAR = new ArrayList<>();
        CHAT_BAR.add(0, new ChatComponentBuilder("").parse(CC.GRAY + CC.STRIKE_THROUGH + "------------------------------------------------").create());

        for (Player player : new Player[]{winningPlayer, losingPlayer}) {
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
            components.forEach(components1 -> player.spigot().sendMessage(components1));
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        for (Player player : this.getSpectators()) {
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
            components.forEach(components1 -> player.spigot().sendMessage(components1));
            CHAT_BAR.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        return true;
    }

    @Override
    public boolean canEnd() {
        return !playerA.isAlive() || !playerB.isAlive() || playerA.isDisconnected() || playerB.isDisconnected();
    }

    @Override
    public Player getWinningPlayer() {
        if (getKit().getGameRules().isTimed()) {
            if (playerA.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerB.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerA.getHits() > playerB.getHits()) {
                return playerA.getPlayer();
            } else {
                return playerB.getPlayer();
            }
        } else if (getKit().getGameRules().isParkour()) {
            if (playerA.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerA.isAlive()) {
                return playerB.getPlayer();
            } else {
                return playerA.getPlayer();
            }
        } else {
            if (playerA.isDisconnected() || !playerA.isAlive()) {
                return playerB.getPlayer();
            } else {
                return playerA.getPlayer();
            }
        }
    }

    @Override
    public Team getWinningTeam() {
        throw new UnsupportedOperationException("Cannot getInstance winning team from a SoloMatch");
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        return playerA;
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        return playerB;
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        return Arrays.asList(playerA, playerB);
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.playerA.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.playerB.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.playerA.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.playerB.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public Team getTeamA() {
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
    }

    @Override
    public Team getTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance team from a SoloMatch");
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerA;
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerB;
        } else {
            return null;
        }
    }

    @Override
    public Team getOpponentTeam(Team team) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a SoloMatch");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a SoloMatch");
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        if (player == null) {
            return null;
        }

        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerB.getPlayer();
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerA.getPlayer();
        } else {
            return null;
        }
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerB;
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerA;
        } else {
            return null;
        }
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
            TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
            TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

            getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

            PlayerUtil.reset(deadPlayer);

            for (Player otherPlayer : getPlayersAndSpectators()) {
                Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
                TaskUtil.runLater(() -> profile.handleVisibility(otherPlayer, deadPlayer), 10L);
            }
    }

    @Override
    public void onRespawn(Player player) {
        Profile.getByPlayer(player).teleportToSpawn();
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        return org.bukkit.ChatColor.RED;
    }
}

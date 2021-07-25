package xyz.refinedev.practice.match.types;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.hook.SpigotHook;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.elo.EloUtil;
import xyz.refinedev.practice.util.nametags.NameTagHandler;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class SoloMatch extends Match {

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;

    private String eloMessage;
    private String specMessage;

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
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (getKit().getGameRules().isStickSpawn() || getKit().getGameRules().isSumo() || getKit().getGameRules().isParkour()) PlayerUtil.denyMovement(player);

        if (!getKit().getGameRules().isNoItems() || !getKit().getGameRules().isSumo()) TaskUtil.runLater(() -> Profile.getByUuid(player.getUniqueId()).getStatisticsData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack)), 10L);

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));

        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        SpigotHook.getKnockbackType().appleKitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        if (getKit().getGameRules().isSumo()) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, Essentials.getMeta().getMatchSpawnLevel(), 0));
        }

        teamPlayer.setPlayerSpawn(spawn);

        if (getKit().getGameRules().isParkour()) teamPlayer.setParkourCheckpoint(spawn);

        NameTagHandler.reloadPlayer(player);
        NameTagHandler.reloadOthersFor(player);
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

                            SpigotHook.getKnockbackType().applyDefaultKnockback(player);


                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.handleVisibility();
                            profile.teleportToSpawn();

                            if (opponent != null && opponent.isOnline()) {
                                profile.setRematchData(new RematchProcedure(rematchKey, player.getUniqueId(), opponent.getUniqueId(), getKit(), getArena()));
                            }

                            getEntities().forEach(Entity::remove);
                            getDroppedItems().forEach(Entity::remove);

                            profile.refreshHotbar();
                        }
                    }
                }
            }
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isSumo() || getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : 4 * 20L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);

        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            winningProfile.getStatisticsData().get(getKit()).incrementWon();
            TaskUtil.runAsync(winningProfile::save);

            losingProfile.getStatisticsData().get(getKit()).incrementLost();
            TaskUtil.runAsync(losingProfile::save);
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
            TaskUtil.runAsync(winningProfile::save);

            losingProfile.calculateGlobalElo();
            TaskUtil.runAsync(losingProfile::save);

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            eloMessage = Locale.MATCH_ELO_CHANGES.toString()
                    .replace("<winner_name>", winningPlayer.getName())
                    .replace("<winner_elo_change>", String.valueOf(winnerEloChange))
                    .replace("<winner_elo>", String.valueOf(newWinnerElo))
                    .replace("<loser_name>", losingPlayer.getName())
                    .replace("<loser_elo_change>", String.valueOf(loserEloChange))
                    .replace("<loser_elo>", String.valueOf(newLoserElo));
        }

        if (getQueueType() == QueueType.CLAN) {
            Clan winningClan = winningProfile.getClan();
            Clan losingClan = losingProfile.getClan();

            int oldWinnerElo = winningClan.getElo();
            int oldLoserElo = losingClan.getElo();

            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

            winningClan.setElo(newWinnerElo);
            losingClan.setElo(newLoserElo);

            winningClan.setWins(winningClan.getWins() + 1);
            winningClan.setWinStreak(winningClan.getWinStreak() + 1);
            losingClan.setLosses(losingClan.getLosses() + 1);
            losingClan.setWinStreak(0);

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            eloMessage = Locale.MATCH_ELO_CHANGES.toString()
                    .replace("<winner_name>", winningPlayer.getName())
                    .replace("<winner_elo_change>", String.valueOf(winnerEloChange))
                    .replace("<winner_elo>", String.valueOf(newWinnerElo))
                    .replace("<loser_name>", losingPlayer.getName())
                    .replace("<loser_elo_change>", String.valueOf(loserEloChange))
                    .replace("<loser_elo>", String.valueOf(newLoserElo));
        }

        StringBuilder builder = new StringBuilder();

        if (!(getSpectators().size() <= 0)) {
            List<Player> specs = new ArrayList<>(getSpectators());
            int i = 0;
            for (Player spectator : getSpectators()) {
                if (getSpectators().size() >= 1) {
                    if (!specs.contains(spectator)) specs.add(spectator);
                    if (i != getSpectators().size()) {
                        i++;
                        if (i == getSpectators().size()) {
                            builder.append(CC.GRAY).append(spectator.getDisplayName());
                        } else {
                            builder.append(CC.GRAY).append(spectator.getDisplayName()).append(CC.GRAY).append(", ");
                        }
                    }
                }
            }

            if (specs.size() >= 1) {
                specMessage = Locale.MATCH_SPEC_MESSAGE.toString()
                        .replace("<spec_size>", String.valueOf(specs.size()))
                        .replace("<spectators>", builder.substring(0, builder.length()));
            }
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
        if (playerA != null) players.add(playerA);


        Player playerB = this.playerB.getPlayer();
        if (playerB != null) players.add(playerB);

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.playerA.getPlayer();
        if (playerA != null)  players.add(playerA);

        Player playerB = this.playerB.getPlayer();
        if (playerB != null) players.add(playerB);

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
        }
        return null;
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
        }
        return null;
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        if (playerA.getUuid().equals(player.getUniqueId())) {
            return playerB;
        } else if (playerB.getUuid().equals(player.getUniqueId())) {
            return playerA;
        }
        return null;
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
        deadPlayer.spigot().respawn();

        PlayerUtil.reset(deadPlayer);

        for ( Player otherPlayer : getPlayersAndSpectators() ) {
            Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
            TaskUtil.runLater(() -> profile.handleVisibility(otherPlayer, deadPlayer), 3L);
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

        boolean[] booleans = new boolean[]{
                getTeamPlayerA().getUuid().equals(viewer.getUniqueId()),
                getTeamPlayerB().getUuid().equals(viewer.getUniqueId()),
                getTeamPlayerA().getUuid().equals(target.getUniqueId()),
                getTeamPlayerB().getUuid().equals(target.getUniqueId())
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer)) {
            return getTeamPlayerA().getUuid().equals(target.getUniqueId()) ? org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.AQUA;
        }
    }

    @Override
    public List<BaseComponent[]> generateEndComponents(Player player) {
        List<BaseComponent[]> componentsList = new ArrayList<>();

        for ( String line : Locale.MATCH_INVENTORY_MESSAGE.toList() ) {
            if (line.equalsIgnoreCase("<inventories>")) {

                BaseComponent[] winners = generateInventoriesComponents(Locale.MATCH_INVENTORY_WINNER.toString(), getTeamPlayer(getWinningPlayer()));
                BaseComponent[] losers = generateInventoriesComponents(Locale.MATCH_INVENTORY_LOSER.toString(), getOpponentTeamPlayer(getWinningPlayer()));

                ChatComponentBuilder builder = new ChatComponentBuilder("");

                for ( BaseComponent component : winners ) {
                    builder.append((TextComponent) component);
                }

                builder.append(new ChatComponentBuilder(Locale.MATCH_INVENTORY_SPLITTER.toString()).create());

                for ( BaseComponent component : losers ) {
                    builder.append((TextComponent) component);
                }

                componentsList.add(builder.create());

                continue;
            }

            if (line.equalsIgnoreCase("<elo_changes>")) {
                if (getQueueType().equals(QueueType.RANKED) || getQueueType().equals(QueueType.CLAN)) {
                    componentsList.add(new ChatComponentBuilder("").parse(eloMessage).create());
                }
                continue;
            }

            if (specMessage != null) {
                componentsList.add(new ChatComponentBuilder("").parse(specMessage).create());
            }

            componentsList.add(new ChatComponentBuilder("").parse(line).create());
        }

        return componentsList;
    }
}

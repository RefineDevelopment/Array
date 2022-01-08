package xyz.refinedev.practice.match.types;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.history.ProfileHistory;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.task.match.MatchTimedTask;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.elo.EloUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.other.TitleAPI;
import xyz.refinedev.practice.util.other.XPUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class SoloMatch extends Match {

    private final Array plugin = this.getPlugin();

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;

    private String eloMessage;
    private String specMessage;

    /**
     * Construct a solo match with the specified details
     *
     * @param playerA   {@link TeamPlayer} first player of the message
     * @param playerB   {@link TeamPlayer} second player of the message
     * @param queue     {@link Queue} if match is started from queue, then we provide it
     * @param kit       {@link Kit} The kit that will be given to all players in the match
     * @param arena     {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public SoloMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
    }

    /**
     * Override the boolean method to identify
     * the correct match without checking instanceOf
     *
     * @return {@link Boolean}
     */
    @Override
    public boolean isSoloMatch() {
        return true;
    }

    /**
     * Setup the player according to {@link Kit},
     * {@link KitGameRules} and {@link Arena}
     * <p>
     * This also teleports the player to the specified arena,
     * set's their parkour checkpoint if kit is parkour and
     * gives special potion effects if specified
     *
     * @param player {@link Player} being setup
     */
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

        plugin.getSpigotHandler().kitKnockback(player, this.getKit());
        player.setMaximumNoDamageTicks(this.getKit().getGameRules().getHitDelay());

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (this.getKit().getGameRules().isSumo()) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));
        }

        teamPlayer.setPlayerSpawn(spawn);

        if (this.getKit().getGameRules().isParkour()) teamPlayer.setParkourCheckpoint(spawn);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    @Override
    public void onStart() {
        if (this.getKit().getGameRules().isTimed()) {
            new MatchTimedTask(plugin, this).runTaskTimer(plugin, 20L, 20L);
        }
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey = UUID.randomUUID();

        for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
            if (teamPlayer.isDisconnected() || !teamPlayer.isAlive()) continue;
            Player player = teamPlayer.getPlayer();
            if (player == null) continue;

            MatchSnapshot matchSnapshot = new MatchSnapshot(teamPlayer);
            matchSnapshot.setSwitchTo(this.getOpponentTeamPlayer(player));

            this.getSnapshots().add(matchSnapshot);
        }

        if (this.getKit().getGameRules().isTimed()) {
            TeamPlayer roundLoser = this.getTeamPlayer(this.getWinningPlayer());
            TeamPlayer roundWinner = this.getOpponentTeamPlayer(this.getOpponentPlayer(this.getWinningPlayer()));
            getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
            this.setState(MatchState.ENDING);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for ( TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()} ) {
                    if (teamPlayer.isDisconnected() || teamPlayer.getPlayer() == null) continue;
                    Player player = teamPlayer.getPlayer();
                    Player opponent = getOpponentPlayer(player);

                    player.setFireTicks(0);
                    player.updateInventory();

                    plugin.getSpigotHandler().resetKnockback(player);

                    Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
                    profile.setState(ProfileState.IN_LOBBY);
                    profile.setMatch(null);

                    if (opponent != null && opponent.isOnline()) {
                        profile.setRematchData(new RematchProcedure(rematchKey, player.getUniqueId(), opponent.getUniqueId(), getKit(), getArena()));
                    }

                    plugin.getProfileManager().teleportToSpawn(profile);
                }
            }
        }.runTaskLater(plugin, (this.getKit().getGameRules().isSumo() || getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : plugin.getConfigHandler().getTELEPORT_DELAY() * 20L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);

        Profile winningProfile = plugin.getProfileManager().getProfileByUUID(winningPlayer.getUniqueId());
        Profile losingProfile = plugin.getProfileManager().getProfileByUUID(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            ProfileHistory winnerProfileMatchHistory = new ProfileHistory(plugin, System.currentTimeMillis(), this.getSnapshotOfPlayer(winningPlayer), this.getSnapshotOfPlayer(losingPlayer), this.getKit());
            ProfileHistory loserProfileMatchHistory = new ProfileHistory(plugin,  System.currentTimeMillis(), this.getSnapshotOfPlayer(losingPlayer), this.getSnapshotOfPlayer(winningPlayer), this.getKit());

            winnerProfileMatchHistory.setWon(true);

            winningProfile.getUnrankedMatchHistory().add(winnerProfileMatchHistory);
            losingProfile.getUnrankedMatchHistory().add(loserProfileMatchHistory);

            if (plugin.getDivisionsManager().isXPBased()) winningProfile.addExperience(XPUtil.handleExperience(winnerProfileMatchHistory));
            if (plugin.getDivisionsManager().isXPBased()) losingProfile.addExperience(XPUtil.handleExperience(loserProfileMatchHistory));

            winningProfile.getStatisticsData().get(this.getKit()).incrementWon();
            plugin.getProfileManager().save(winningProfile);

            losingProfile.getStatisticsData().get(this.getKit()).incrementLost();
            plugin.getProfileManager().save(losingProfile);
        }

        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();

            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            ProfileHistory winnerProfileMatchHistory = new ProfileHistory(plugin, System.currentTimeMillis(), this.getSnapshotOfPlayer(winningPlayer), this.getSnapshotOfPlayer(losingPlayer), this.getKit());
            ProfileHistory loserProfileMatchHistory = new ProfileHistory(plugin,  System.currentTimeMillis(), this.getSnapshotOfPlayer(losingPlayer), this.getSnapshotOfPlayer(winningPlayer), this.getKit());

            winnerProfileMatchHistory.setWon(true);
            winnerProfileMatchHistory.setRanked(true);
            loserProfileMatchHistory.setRanked(true);

            winnerProfileMatchHistory.setWinnerChangedELO(winnerEloChange);
            loserProfileMatchHistory.setLooserChangedELO(loserEloChange);

            winningProfile.getRankedMatchHistory().add(winnerProfileMatchHistory);
            losingProfile.getRankedMatchHistory().add(loserProfileMatchHistory);

            if (plugin.getDivisionsManager().isXPBased()) winningProfile.addExperience(XPUtil.handleExperience(winnerProfileMatchHistory));
            if (plugin.getDivisionsManager().isXPBased()) losingProfile.addExperience(XPUtil.handleExperience(loserProfileMatchHistory));

            winningProfile.getStatisticsData().get(this.getKit()).setElo(newWinnerElo);
            losingProfile.getStatisticsData().get(this.getKit()).setElo(newLoserElo);

            winningProfile.getStatisticsData().get(this.getKit()).incrementWon();
            losingProfile.getStatisticsData().get(this.getKit()).incrementLost();

            plugin.getProfileManager().calculateGlobalElo(winningProfile);
            plugin.getProfileManager().save(winningProfile);

            plugin.getProfileManager().calculateGlobalElo(losingProfile);
            plugin.getProfileManager().save(losingProfile);

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
            for ( Player spectator : getSpectators() ) {
                if (getSpectators().size() >= 1) {
                    if (!specs.contains(spectator)) specs.add(spectator);
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
        if (this.getKit().getGameRules().isTimed()) {
            if (playerA.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerB.isDisconnected()) {
                return playerB.getPlayer();
            } else if (playerA.getHits() > playerB.getHits()) {
                return playerA.getPlayer();
            } else {
                return playerB.getPlayer();
            }
        } else if (this.getKit().getGameRules().isParkour()) {
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
        if (playerA.getUniqueId().equals(player.getUniqueId())) {
            return playerA;
        } else if (playerB.getUniqueId().equals(player.getUniqueId())) {
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

        if (playerA.getUniqueId().equals(player.getUniqueId())) {
            return playerB.getPlayer();
        } else if (playerB.getUniqueId().equals(player.getUniqueId())) {
            return playerA.getPlayer();
        }
        return null;
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        if (playerA.getUniqueId().equals(player.getUniqueId())) {
            return playerB;
        } else if (playerB.getUniqueId().equals(player.getUniqueId())) {
            return playerA;
        }
        return null;
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        this.getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
        PlayerUtil.reset(deadPlayer);

        TitleAPI.sendMatchWinner(killerPlayer);
        TitleAPI.sendMatchLoser(deadPlayer);

        for ( Player otherPlayer : getPlayers() ) {
            Profile profile = plugin.getProfileManager().getProfileByUUID(otherPlayer.getUniqueId());
            TaskUtil.runLater(() -> plugin.getProfileManager().handleVisibility(profile, deadPlayer), 4L);
        }

        if (this.canEnd()) {
            this.plugin.getMatchManager().end(this);
        } else {
            if (!roundLoser.isDisconnected()) {
                deadPlayer.teleport(getMidSpawn());

                Profile profile = plugin.getProfileManager().getProfileByUUID(deadPlayer.getUniqueId());
                profile.setState(ProfileState.SPECTATING);
                plugin.getProfileManager().refreshHotbar(profile);
                plugin.getProfileManager().handleVisibility(profile);
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        plugin.getProfileManager().teleportToSpawn(profile);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        boolean[] booleans = new boolean[]{
                getTeamPlayerA().getUniqueId().equals(viewer.getUniqueId()),
                getTeamPlayerB().getUniqueId().equals(viewer.getUniqueId()),
                getTeamPlayerA().getUniqueId().equals(target.getUniqueId()),
                getTeamPlayerB().getUniqueId().equals(target.getUniqueId())
        };

        if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) {
            return org.bukkit.ChatColor.RED;
        } else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) {
            return org.bukkit.ChatColor.GREEN;
        } else if (getSpectators().contains(viewer)) {
            return getTeamPlayerA().getUniqueId().equals(target.getUniqueId()) ? org.bukkit.ChatColor.GREEN : org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.AQUA;
        }
    }

    @Override
    public List<BaseComponent[]> generateEndComponents(Player player) {
        List<BaseComponent[]> componentsList = new ArrayList<>();

        for ( String line : Locale.MATCH_INVENTORY_MESSAGE.toList() ) {
            if (line.equalsIgnoreCase("<inventories>")) {

                BaseComponent[] winners = this.plugin.getMatchManager().generateInventoriesComponents(Locale.MATCH_INVENTORY_WINNER.toString(), getTeamPlayer(getWinningPlayer()));
                BaseComponent[] losers = this.plugin.getMatchManager().generateInventoriesComponents(Locale.MATCH_INVENTORY_LOSER.toString(), getOpponentTeamPlayer(getWinningPlayer()));

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

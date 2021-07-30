package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.MatchState;
import xyz.refinedev.practice.match.task.MatchBridgePlayerTask;
import xyz.refinedev.practice.match.task.MatchStartTask;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.ChatComponentBuilder;
import xyz.refinedev.practice.util.elo.EloUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.nametags.NameTagHandler;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class BridgeMatch extends Match {

    private final Array plugin = Array.getInstance();
    private final List<Player> caughtPlayers = new ArrayList<>();

    private TeamPlayer playerA;
    private TeamPlayer playerB;

    private int round = 0;
    private BukkitTask startTask;

    private String eloMessage;
    private String specMessage;

    public BridgeMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
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
    public boolean isHCFMatch() {
        return false;
    }

    @Override
    public boolean isTheBridgeMatch() {
        return true;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);
        PlayerUtil.denyMovement(player);

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));

        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getKnockbackManager().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        player.getInventory().setArmorContents(getKit().getKitInventory().getArmor());
        player.getInventory().setContents(getKit().getKitInventory().getContents());
        giveBridgeKit(player);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.round++;
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey = UUID.randomUUID();

            for ( TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()} ) {
                if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                    Player player = teamPlayer.getPlayer();
                    if (player != null && teamPlayer.isAlive()) {
                        MatchSnapshot snapshot=new MatchSnapshot(teamPlayer, getOpponentTeamPlayer(player));
                        getSnapshots().add(snapshot);
                    }
                }
            }

            if (getKit().getGameRules().isTimed()) {
                TeamPlayer roundLoser = getTeamPlayer(getWinningPlayer());
                TeamPlayer roundWinner = getOpponentTeamPlayer(getOpponentPlayer(getWinningPlayer()));

                getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
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

                            plugin.getKnockbackManager().resetKnockback(player);

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.handleVisibility();

                            if (opponent != null) {
                                profile.setRematchData(new RematchProcedure(rematchKey, player.getUniqueId(), opponent.getUniqueId(), getKit(), getArena()));
                            }

                            profile.refreshHotbar();
                            profile.teleportToSpawn();
                        }
                    }
                }
            }
        }.runTaskLater(plugin, (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : plugin.getConfigHandler().getTELEPORT_DELAY() * 20L);

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
            winningProfile.save();

            losingProfile.calculateGlobalElo();
            losingProfile.save();

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            eloMessage = Locale.MATCH_ELO_CHANGES.toString()
                    .replace("<winner_name>", winningPlayer.getName())
                    .replace("<winner_elo_change>", String.valueOf(winnerEloChange))
                    .replace("<winner_elo>", String.valueOf(newWinnerElo))
                    .replace("<loser_name>", winningPlayer.getName())
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

        //Reset their points cuz yes
        winningProfile.setBridgeRounds(0);
        losingProfile.setBridgeRounds(0);
        return true;
    }

    public void initiateDeath(Player player) {
        PlayerUtil.reset(player);
        for ( Player players : getPlayersAndSpectators() ) {
            players.sendMessage(Locale.MATCH_DIED.toString()
                    .replace("<relation_color>", this.getRelationColor(players, player).toString())
                    .replace("<participant_name>", player.getName()));
        }
       TaskUtil.runLater(new MatchBridgePlayerTask(this, player), 2L);
    }

    public void properDeath(Player player) {
        PlayerUtil.reset(player);
        for ( Player players : this.getPlayersAndSpectators() ) {
            if (player.getKiller() == null) {
                players.sendMessage(Locale.MATCH_DIED.toString()
                        .replace("<relation_color>", this.getRelationColor(players, player).toString())
                        .replace("<participant_name>", player.getName()));
            } else {
                players.sendMessage(Locale.MATCH_KILLED.toString()
                        .replace("<relation_color_dead>", this.getRelationColor(players, player).toString())
                        .replace("<dead_name>", player.getName())
                        .replace("<relation_color_killer>", this.getRelationColor(players, player.getKiller()).toString())
                        .replace("<killer_name>", player.getKiller().getName()));
            }
        }
        TaskUtil.runLater(new MatchBridgePlayerTask(this, player), 2L);
    }

    @Override
    public boolean canEnd() {
        if (getRoundsNeeded(playerA) == 0 || getRoundsNeeded(playerB) == 0)
            return true;
        return playerA.isDisconnected() || playerB.isDisconnected() || !playerA.isAlive() || !playerB.isAlive();
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
        } else if (getKit().getGameRules().isBridge()) {
            if (playerA.getRounds() > playerB.getRounds()) {
                return playerA.getPlayer();
            } else if (playerB.getRounds() > playerA.getRounds()) {
                return playerB.getPlayer();
            } else if (playerA.isDisconnected() || !playerA.isAlive()) {
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
        if (player == null) return null;

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

    @SuppressWarnings("unused")
    public int getTotalRoundWins() {
        return playerA.getRounds() + playerB.getRounds();
    }

    /**
     * Get points needed to win the round
     *
     * @param teamPlayer The teamplayer whose points are being returned
     * @return {@link Integer}
     */
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (playerA.equals(teamPlayer)) {
            return 3 - aProfile.getBridgeRounds();
        } else if (playerB.equals(teamPlayer)) {
            return 3 - bProfile.getBridgeRounds();
        }
        return -1;
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (deadPlayer.isOnline()) {
            if (getRoundsNeeded(playerA) != 0 || getRoundsNeeded(playerB) != 0) {
                if (getWinningPlayer().getUniqueId().equals(playerA.getUuid())) {
                    aProfile.setBridgeRounds(aProfile.getBridgeRounds() + 1);
                } else if (getWinningPlayer().getUniqueId().equals(playerB.getUuid())) {
                    bProfile.setBridgeRounds(bProfile.getBridgeRounds() + 1);
                }

                Locale.MATCH_BRIDGE_WON.toList().forEach(string -> {
                        string = string.replace("<winner_name>", getWinningPlayer().getName());
                        this.broadcastMessage(string);
                });

                if (aProfile.getBridgeRounds() >= 3 || bProfile.getBridgeRounds() >= 3) {
                    TeamPlayer roundWinner = getTeamPlayer(getWinningPlayer());
                    TeamPlayer roundLoser = getOpponentTeamPlayer(getWinningPlayer());

                    getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

                    PlayerUtil.reset(deadPlayer);

                    for (Player otherPlayer : getPlayersAndSpectators()) {
                        Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
                        profile.handleVisibility(otherPlayer, deadPlayer);
                    }

                    if (caughtPlayers != null) {
                        caughtPlayers.clear();
                    }

                    end();
                } else {

                    if (caughtPlayers != null) caughtPlayers.clear();
                    if (plugin.getConfigHandler().isBRIDGE_CLEAR_BLOCKS()) cleanup();

                    this.setupPlayer(playerA.getPlayer());
                    this.setupPlayer(playerB.getPlayer());

                    playerA.getPlayer().showPlayer(playerB.getPlayer());
                    playerB.getPlayer().showPlayer(playerA.getPlayer());

                    this.onStart();

                    for ( String string : Locale.MATCH_ROUND_MESSAGE.toList() ) {
                        playerA.getPlayer().sendMessage(CC.translate(string
                                .replace("<round_number>", String.valueOf(this.getRound()))
                                .replace("<your_points>", String.valueOf(Profile.getByPlayer(playerA.getPlayer()).getBridgeRounds()))
                                .replace("<their_points>", String.valueOf(Profile.getByPlayer(playerB.getPlayer()).getBridgeRounds())))
                                .replace("<arena>", this.getArena().getName())
                                .replace("<kit>", this.getKit().getName())
                                .replace("<ping>", String.valueOf(playerB.getPing())));

                        playerB.getPlayer().sendMessage(CC.translate(string
                                .replace("<round_number>", String.valueOf(this.getRound()))
                                .replace("<your_points>", String.valueOf(Profile.getByPlayer(playerB.getPlayer()).getBridgeRounds()))
                                .replace("<their_points>", String.valueOf(Profile.getByPlayer(playerA.getPlayer()).getBridgeRounds())))
                                .replace("<arena>", this.getArena().getName())
                                .replace("<kit>", this.getKit().getName())
                                .replace("<ping>", String.valueOf(playerA.getPing())));
                    }
                        //Continue the Match
                        this.setState(MatchState.STARTING);
                        this.setStartTimestamp(-1);
                        startTask = new MatchStartTask(this).runTaskTimer(Array.getInstance(), 20L, 20L);
                }
            }
        } else if (!deadPlayer.isOnline() || !killerPlayer.isOnline()) {
            //Disconnect not ending match fix
            if (startTask != null) startTask.cancel();
            end();
        }
    }

    @Override
    public void onRespawn(Player player) {
        Profile profile = Profile.getByPlayer(player);
        profile.teleportToSpawn();
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (target == playerB.getPlayer()) {
            return org.bukkit.ChatColor.BLUE;
        } else if (target == playerA.getPlayer()) {
            return org.bukkit.ChatColor.RED;
        }
        return org.bukkit.ChatColor.AQUA;
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
                if (getQueueType().equals(QueueType.RANKED)) {
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

    /**
     * Replace and color the clay blocks and leather
     * armor of the specified player to their coressponding color
     *
     * @param player The player getting the kit applied
     */
    public static void giveBridgeKit(Player player) {
        Profile profile = Profile.getByPlayer(player);
        BridgeMatch teamMatch = (BridgeMatch) profile.getMatch();

        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);

        if (teamMatch.getTeamPlayerA().getPlayer() == player) {
            player.getInventory().setArmorContents(armorRed);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
            });
        } else {
            player.getInventory().setArmorContents(armorBlue);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
            });
        }

        player.updateInventory();
    }

    public static ItemStack[] leatherArmor(Color color){
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(color).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(color).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(color).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(color).build()
        };
    }
}

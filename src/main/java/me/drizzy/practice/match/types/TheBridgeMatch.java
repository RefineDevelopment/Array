package me.drizzy.practice.match.types;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
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
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.util.other.NameTags;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.elo.EloUtil;
import me.drizzy.practice.util.chat.ChatComponentBuilder;
import me.drizzy.practice.util.inventory.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TheBridgeMatch extends Match {

    private TeamPlayer playerA;
    private TeamPlayer playerB;
    private int round = 0;
    private BukkitTask startTask;
    private final List<Player> caughtPlayers = new ArrayList<>();

    public TheBridgeMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
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

        if (teamPlayer.isDisconnected()) {
            return;
        }
        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        PlayerUtil.denyMovement(player);

        if (getKit().getGameRules().isInfiniteSpeed()) {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 2));
        }
        if (getKit().getGameRules().isInfiniteStrength()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 2));
        }

        Array.getInstance().getNMSManager().getKnockbackType().appleKitKnockback(player, getKit());

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }
        teamPlayer.setPlayerSpawn(spawn);

        player.getInventory().setArmorContents(getKit().getKitInventory().getArmor());
        player.getInventory().setContents(getKit().getKitInventory().getContents());
        giveBridgeKit(player);

        NameTags.color(player, getOpponentPlayer(player), (this.getTeamPlayerA().getPlayer() == player ? org.bukkit.ChatColor.BLUE :  org.bukkit.ChatColor.RED), getKit().getGameRules().isBuild());
    }

    @Override
    public void onStart() {
        this.round++;
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey=UUID.randomUUID();

            for ( TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()} ) {
                if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                    Player player=teamPlayer.getPlayer();

                    if (player != null) {
                        if (teamPlayer.isAlive()) {
                            MatchSnapshot snapshot=new MatchSnapshot(teamPlayer, getOpponentTeamPlayer(player));
                            getSnapshots().add(snapshot);
                        }
                    }
                }
            }

            if (getKit().getGameRules().isTimed()) {
                TeamPlayer roundLoser=getTeamPlayer(getWinningPlayer());
                TeamPlayer roundWinner=getOpponentTeamPlayer(getOpponentPlayer(getWinningPlayer()));

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

                            NameTags.reset(player, teamPlayer.getPlayer());

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
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);


        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");

        inventoriesBuilder.append("Winner: ").color(ChatColor.GREEN).append(winningPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer)).setCurrentClickEvent(getClickEvent(winningTeamPlayer)).append(" - ").color(ChatColor.GRAY).append("Loser: ").color(ChatColor.RED).append(losingPlayer.getName()).color(ChatColor.WHITE);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer)).setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse("&c&lMatch Details &7(Click name to view inventory)").create());
        components.add(inventoriesBuilder.create());


        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

            if (getQueueType() == QueueType.UNRANKED) {
                winningProfile.getStatisticsData().get(getKit()).incrementWon();
                losingProfile.getStatisticsData().get(getKit()).incrementLost();
            }


            if (getQueueType() == QueueType.RANKED) {
                int oldWinnerElo=winningTeamPlayer.getElo();
                int oldLoserElo=losingTeamPlayer.getElo();
                int newWinnerElo=EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
                int newLoserElo=EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);
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
            ArrayList<Player> specs = new ArrayList<>(getSpectators());
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
        winningProfile.setBridgeRounds(0);
        losingProfile.setBridgeRounds(0);
        return true;
    }

    @Override
    public boolean canEnd() {
        if (getRoundsNeeded(playerA) == 0 || getRoundsNeeded(playerB) == 0)
            return true;
        return playerA.isDisconnected() || playerB.isDisconnected();
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

    @SuppressWarnings("unused")
    public int getTotalRoundWins() {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());
        return aProfile.getBridgeRounds() + bProfile.getBridgeRounds();
    }

    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (playerA.equals(teamPlayer)) {
            return 3 - aProfile.getBridgeRounds();
        } else if (playerB.equals(teamPlayer)) {
            return 3 - bProfile.getBridgeRounds();
        } else {
            return -1;
        }
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (deadPlayer.isOnline()) {
            if (getRoundsNeeded(playerA) != 0 || getRoundsNeeded(playerB) != 0) {
                if (getWinningPlayer().getUniqueId().toString().equals(playerA.getUuid().toString())) {
                    aProfile.setBridgeRounds(aProfile.getBridgeRounds() + 1);
                } else if (getWinningPlayer().getUniqueId().toString().equals(playerB.getUuid().toString())) {
                    bProfile.setBridgeRounds(bProfile.getBridgeRounds() + 1);
                }

                this.broadcastMessage("");
                this.broadcastMessage(CC.translate(CC.RED + getWinningPlayer().getName() + " &7has won this round!"));
                this.broadcastMessage("");

                if (aProfile.getBridgeRounds() >= 3 || bProfile.getBridgeRounds() >= 3) {
                    TeamPlayer roundWinner=getTeamPlayer(getWinningPlayer());
                    TeamPlayer roundLoser=getOpponentTeamPlayer(getWinningPlayer());

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

                    if (caughtPlayers != null) {
                        caughtPlayers.clear();
                    }

                    if (Array.getInstance().getEssentials().getMeta().isBridgeClearBlocks()) {
                        cleanup();
                    }

                        setupPlayer(playerA.getPlayer());
                        setupPlayer(playerB.getPlayer());

                        playerA.getPlayer().showPlayer(playerB.getPlayer());
                        playerB.getPlayer().showPlayer(playerA.getPlayer());

                        onStart();
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
                        setState(MatchState.STARTING);
                        setStartTimestamp(-1);
                    startTask = new MatchStartTask(this).runTaskTimer(Array.getInstance(), 20L, 20L);
                }
            }
        } else if (!deadPlayer.isOnline() || !killerPlayer.isOnline()){
            if (startTask != null) {
                startTask.cancel();
            }
            end();
        }
    }

    @Override
    public void onRespawn(Player player) {
        Profile.getByPlayer(player).teleportToSpawn();
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer == playerA.getPlayer() && target == playerB.getPlayer()) {
            return org.bukkit.ChatColor.BLUE;
        } else {
            return org.bukkit.ChatColor.RED;
        }
    }

    public static void giveBridgeKit(Player player) {
        Profile profile = Profile.getByPlayer(player);
        TheBridgeMatch teamMatch = (TheBridgeMatch) profile.getMatch();

        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);

        if ( teamMatch.getTeamPlayerA().getPlayer() == player ) {
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

package me.array.ArrayPractice.match.impl;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.match.MatchState;
import me.array.ArrayPractice.match.task.MatchStartTask;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.profile.meta.ProfileMatchHistory;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.TaskUtil;
import me.array.ArrayPractice.util.elo.EloUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.nametag.NameTags;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pt.foxspigot.jar.knockback.KnockbackModule;
import pt.foxspigot.jar.knockback.KnockbackProfile;

import java.util.*;

@Getter
public class SumoMatch extends Match {

    @Setter
    private TeamPlayer playerA;
    @Setter
    private TeamPlayer playerB;

    public SumoMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, kit, arena, queueType);

        this.playerA = playerA;
        this.playerB = playerB;
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
        return false;
    }

    @Override
    public boolean isSumoMatch() {
        return true;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) {
            return;
        }
        this.broadcastMessage(CC.AQUA + CC.BOLD + "Match Found!");
        this.broadcastMessage("");
        if (getQueueType() == QueueType.RANKED) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            Profile opponentprofile = Profile.getByUuid(this.getOpponentPlayer(player).getUniqueId());
            this.broadcastMessage(CC.translate(" &b● &fPlayers: &b" + player.getDisplayName() + CC.GRAY + " (" + profile.getKitData().get(getKit()).getElo() + "ELO )"  + CC.GRAY + " vs " + CC.AQUA + this.getOpponentPlayer(player).getDisplayName() + CC.GRAY + " (" + opponentprofile.getKitData().get(getKit()).getElo() + "ELO )"));
        }
        if (getQueueType() == QueueType.UNRANKED) {
            this.broadcastMessage(CC.translate(" &b● &fPlayers: &b" + player.getDisplayName() + CC.GRAY + " vs " + CC.AQUA + this.getOpponentPlayer(player).getDisplayName()));
        }
        this.broadcastMessage(CC.translate(" &b● &fArena: &b" + this.getArena().getName()));
        this.broadcastMessage(CC.translate(" &b● &fKit: &b" + this.getKit().getName()));
        this.broadcastMessage(CC.translate(""));

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        PlayerUtil.denyMovement(player);

        if (!getKit().getGameRules().isCombo()) {
            player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());
        }

        if (getKit().getGameRules().isInfinitespeed()) {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 2));
        }
        if (getKit().getGameRules().isInfinitestrength()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 1));
        }

        if (!getKit().getGameRules().isNoitems()) {
            Profile.getByUuid(player.getUniqueId()).getKitData().get(this.getKit()).getKitItems().forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        }

        if (getKit().getKnockbackProfile() != null && KnockbackModule.INSTANCE.profiles.containsKey(getKit().getKnockbackProfile())) {
            KnockbackProfile kbprofile = KnockbackModule.INSTANCE.profiles.get(getKit().getKnockbackProfile());
            ((CraftPlayer) player).getHandle().setKnockback(kbprofile);
        } else {
            KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get("strafe");
            ((CraftPlayer) player).getHandle().setKnockback(knockbackProfile);
        }

        Location spawn = playerA.equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.add(0, 2, 0));
        }

        NameTags.color(player, getOpponentPlayer(player), org.bukkit.ChatColor.RED, getKit().getGameRules().isBuild());

    }

    @Override
    public void cleanPlayer(Player player) {

    }

    @Override
    public void onStart() {
    }

    @Override
    public boolean onEnd() {
        UUID rematchKey = UUID.randomUUID();

        for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
            if (!teamPlayer.isDisconnected() && teamPlayer.isAlive()) {
                Player player = teamPlayer.getPlayer();

                if (player != null) {
                    if (teamPlayer.isAlive()) {
                        MatchSnapshot snapshot = new MatchSnapshot(teamPlayer, getOpponentTeamPlayer(player));
                        getSnapshots().add(snapshot);
                    }
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
                            NameTags.reset(player, opponent);

                            player.setFireTicks(0);
                            player.updateInventory();

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            TaskUtil.runSync(profile::refreshHotbar);
                            profile.handleVisibility();
                            KnockbackProfile kbprofile = KnockbackModule.getDefault();
                            ((CraftPlayer) player).getHandle().setKnockback(kbprofile);

                            if (opponent != null) {
                                profile.setRematchData(new ProfileRematchData(rematchKey, player.getUniqueId(),
                                        opponent.getUniqueId(), getKit(), getArena()));
                            }

                            Practice.getInstance().getEssentials().teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(Practice.getInstance(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isSumo() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);


        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");

        inventoriesBuilder.append("Winner: ").color(ChatColor.GREEN).append(winningPlayer.getName()).color(ChatColor.YELLOW);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer)).setCurrentClickEvent(getClickEvent(winningTeamPlayer)).append(" - ").color(ChatColor.GRAY).append("Loser: ").color(ChatColor.RED).append(losingPlayer.getName()).color(ChatColor.YELLOW);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer)).setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse("&bPost-Match Inventories &7(Click name to view)").create());
        components.add(new ChatComponentBuilder("").parse("").create());
        components.add(inventoriesBuilder.create());


        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            winningProfile.getKitData().get(getKit()).incrementWon();
            losingProfile.getKitData().get(getKit()).incrementLost();

            ProfileMatchHistory winnerProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), true, "UNRANKED", getKit().getName(), 0, 0, new Date(), winningProfile.getSumoRounds(), losingProfile.getSumoRounds());
            ProfileMatchHistory loserProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), false, "UNRANKED", getKit().getName(), 0, 0, new Date(), winningProfile.getSumoRounds(), losingProfile.getSumoRounds());

            winningProfile.addMatchHistory(winnerProfileMatchHistory);
            losingProfile.addMatchHistory(loserProfileMatchHistory);
        }


        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();
            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);
            winningProfile.getKitData().get(getKit()).setElo(newWinnerElo);
            losingProfile.getKitData().get(getKit()).setElo(newLoserElo);
            winningProfile.getKitData().get(getKit()).incrementWon();
            losingProfile.getKitData().get(getKit()).incrementLost();
            winningProfile.calculateGlobalElo();
            losingProfile.calculateGlobalElo();

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&7ELO Changes: &a" + winningPlayer.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &c" + losingPlayer.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());

            ProfileMatchHistory winnerProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), true, "RANKED", getKit().getName(), winnerEloChange, loserEloChange, new Date());
            ProfileMatchHistory loserProfileMatchHistory = new ProfileMatchHistory(getSnapshotOfPlayer(winningPlayer), getSnapshotOfPlayer(losingPlayer), false, "RANKED", getKit().getName(), winnerEloChange, loserEloChange, new Date());

            winningProfile.addMatchHistory(winnerProfileMatchHistory);
            losingProfile.addMatchHistory(loserProfileMatchHistory);
        }

        StringBuilder builder = new StringBuilder();

        if (!(getSpectators().size() <= 0)) {
            ArrayList<Player> specs = new ArrayList<>(getSpectators());
            int i = 0;
            for (Player spectator : getSpectators()) {
                Profile profile = Profile.getByUuid(spectator.getUniqueId());
                if (getSpectators().size() >= 1) {
                    if (profile.isSilent()) {
                        specs.remove(spectator);
                    } else {
                        if (!specs.contains(spectator))
                            specs.add(spectator);
                    }
                    if (i != getSpectators().size()) {
                        i++;
                        if (i == getSpectators().size()) {
                            if (!profile.isSilent()) {
                                builder.append(CC.GRAY).append(spectator.getName());
                            }
                        } else {
                            if (!profile.isSilent()) {
                                builder.append(CC.GRAY).append(spectator.getName()).append(CC.GRAY).append(", ");
                            }
                        }

                    }
                }
            }
            if (specs.size() >= 1) {
                components.add(new ChatComponentBuilder("").parse("&bSpectators (" + specs.size() + "): &7" + builder.substring(0, builder.length())).create());
            }
        }

        List<BaseComponent[]> chatbar = new ArrayList<>();
        chatbar.add(0, new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());

        for (Player player : new Player[]{winningPlayer, losingPlayer}) {
            chatbar.forEach(components1 -> player.spigot().sendMessage(components1));
            components.forEach(components1 -> player.spigot().sendMessage(components1));
            chatbar.forEach(components1 -> player.spigot().sendMessage(components1));
        }

        if (getMatchWaterCheck() != null) {
            getMatchWaterCheck().cancel();
        }

        winningProfile.setSumoRounds(0);
        losingProfile.setSumoRounds(0);

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

    @Override
    public int getTotalRoundWins() {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());
        return aProfile.getSumoRounds() + bProfile.getSumoRounds();
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (playerA.equals(teamPlayer)) {
            return 3 - aProfile.getSumoRounds();
        } else if (playerB.equals(teamPlayer)) {
            return 3 - bProfile.getSumoRounds();
        } else {
            return -1;
        }
    }

    @Override
    public int getRoundsNeeded(Team team) {
        throw new UnsupportedOperationException("Cannot getInstance team round wins from SoloMatch");
    }

    @Override
    public int getTeamACapturePoints() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTeamACapturePoints(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public int getTeamBCapturePoints() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTeamBCapturePoints(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public int getTimer() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setTimer(int number) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public Player getCapper() {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void setCapper(Player player) {
        throw new UnsupportedOperationException("No");
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        Profile aProfile = Profile.getByUuid(playerA.getUuid());
        Profile bProfile = Profile.getByUuid(playerB.getUuid());

        if (deadPlayer.isOnline()) {
            if (getRoundsNeeded(playerA) != 0 || getRoundsNeeded(playerB) != 0) {
                if (getWinningPlayer().getUniqueId().toString().equals(playerA.getUuid().toString())) {
                    aProfile.setSumoRounds(aProfile.getSumoRounds() + 1);
                } else if (getWinningPlayer().getUniqueId().toString().equals(playerB.getUuid().toString())) {
                    bProfile.setSumoRounds(bProfile.getSumoRounds() + 1);
                }

                getWinningPlayer().getPlayer().sendMessage(CC.translate("&aYou have won this round!"));
                getOpponentPlayer(getWinningPlayer()).getPlayer().sendMessage(CC.translate("&cYou have lost this round!"));

                if (aProfile.getSumoRounds() >= 3 || bProfile.getSumoRounds() >= 3) {
                    TeamPlayer roundWinner = getTeamPlayer(getWinningPlayer());
                    TeamPlayer roundLoser = getOpponentTeamPlayer(getWinningPlayer());


                    getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

                    PlayerUtil.reset(deadPlayer);

                    for (Player otherPlayer : getPlayersAndSpectators()) {
                        Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
                        profile.handleVisibility(otherPlayer, deadPlayer);
                    }

                    end();
                } else {
                    playerA.getPlayer().sendMessage(CC.translate("&fYou need to win &b" + getRoundsNeeded(playerA) + " &fmore rounds!"));
                    playerB.getPlayer().sendMessage(CC.translate("&fYou need to win &b" + getRoundsNeeded(playerB) + " &fmore rounds!"));
                    setupPlayer(playerA.getPlayer());
                    setupPlayer(playerB.getPlayer());
                    playerA.getPlayer().showPlayer(playerB.getPlayer());
                    playerB.getPlayer().showPlayer(playerA.getPlayer());
                    onStart();
                    setState(MatchState.STARTING);
                    setStartTimestamp(-1);
                    new MatchStartTask(this).runTaskTimer(Practice.getInstance(), 20L, 20L);
                    this.getPlayers().forEach(player -> player.sendMessage(CC.translate("&b● &fPlayers: &b" + this.getPlayers().get(1).getDisplayName() + " &7vs &b" + this.getPlayers().get(2).getDisplayName())));
                    this.getPlayers().forEach(player -> player.sendMessage(CC.translate("&b● &fArena: &b" + this.getArena().getName())));
                    this.getPlayers().forEach(player -> player.sendMessage(CC.translate("&b● &fKit: &b" + this.getKit().getName())));
                    this.broadcastMessage("");           }
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        Practice.getInstance().getEssentials().teleportToSpawn(player);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        if (playerA.getUuid().equals(viewer.getUniqueId()) || playerB.getUuid().equals(viewer.getUniqueId())) {
            return org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.GREEN;
        }
    }
}

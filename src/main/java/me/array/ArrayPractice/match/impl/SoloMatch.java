package me.array.ArrayPractice.match.impl;

import lombok.Getter;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.elo.EloUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.nametag.NameTags;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rip.verse.jupiter.knockback.KnockbackModule;
import rip.verse.jupiter.knockback.KnockbackProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class SoloMatch extends Match {

    private final TeamPlayer playerA;
    private final TeamPlayer playerB;
    private int playerARoundWins = 0;
    private int playerBRoundWins = 0;

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
    public boolean isKoTHMatch() {
        return false;
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);
        Profile player2 = Profile.getByUuid(player.getUniqueId());

        if (teamPlayer.isDisconnected()) {
            return;
        }

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);

        if (getKit().getGameRules().isSumo() || getKit().getGameRules().isParkour()) {
            PlayerUtil.denyMovement(player);
        }

        player.setMaximumNoDamageTicks(getKit().getGameRules().getHitDelay());

        if (!getKit().getGameRules().isNoitems()) {
            for (ItemStack itemStack : Profile.getByUuid(player.getUniqueId()).getKitData().get(getKit()).getKitItems()) {
                player.getInventory().addItem(itemStack);
            }
        }
        if (getKit().getKnockbackProfile() != null) {
            KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get(getKit().getKnockbackProfile());
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
    public void onEnd() {
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

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TeamPlayer teamPlayer : new TeamPlayer[]{getTeamPlayerA(), getTeamPlayerB()}) {
                    if (!teamPlayer.isDisconnected()) {
                        Player player = teamPlayer.getPlayer();
                        Player opponent = getOpponentPlayer(player);

                        if (player != null) {
                            NameTags.reset(player, opponent);

                            EntityPlayer eplayer = ((CraftPlayer) player).getHandle();
                            player.setFireTicks(0);
                            player.updateInventory();

                            Profile profile = Profile.getByUuid(player.getUniqueId());
                            profile.setState(ProfileState.IN_LOBBY);
                            profile.setMatch(null);
                            profile.refreshHotbar();
                            profile.handleVisibility();

                            if (opponent != null) {
                                profile.setRematchData(new ProfileRematchData(rematchKey, player.getUniqueId(),
                                        opponent.getUniqueId(), getKit(), getArena()));
                            }

                            Array.get().getEssentials().teleportToSpawn(player);
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(Array.get(), (getKit().getGameRules().isWaterkill() || getKit().getGameRules().isLavakill() || getKit().getGameRules().isParkour()) ? 0L : 40L);

        Player winningPlayer = getWinningPlayer();
        Player losingPlayer = getOpponentPlayer(winningPlayer);

        TeamPlayer winningTeamPlayer = getTeamPlayer(winningPlayer);
        TeamPlayer losingTeamPlayer = getTeamPlayer(losingPlayer);

        ChatComponentBuilder inventoriesBuilder = new ChatComponentBuilder("");

        inventoriesBuilder.append("Winner: ")
                .color(ChatColor.GREEN)
                .append(winningPlayer.getName())
                .color(ChatColor.YELLOW);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(winningTeamPlayer))
                .setCurrentClickEvent(getClickEvent(winningTeamPlayer))

                .append(" - ")
                .color(ChatColor.GRAY)

                .append("Loser: ")
                .color(ChatColor.RED)
                .append(losingPlayer.getName())
                .color(ChatColor.YELLOW);
        inventoriesBuilder.setCurrentHoverEvent(getHoverEvent(losingTeamPlayer))
                .setCurrentClickEvent(getClickEvent(losingTeamPlayer));

        List<BaseComponent[]> components = new ArrayList<>();
        components.add(new ChatComponentBuilder("").parse("&b&lMatch Summary &7⎜ &7&o(Click name to view)").create());
        components.add(inventoriesBuilder.create());

        Profile winningProfile = Profile.getByUuid(winningPlayer.getUniqueId());
        Profile losingProfile = Profile.getByUuid(losingPlayer.getUniqueId());

        if (getQueueType() == QueueType.UNRANKED) {
            winningProfile.getKitData().get(getKit()).incrementUnrankedWins();
            losingProfile.getKitData().get(getKit()).incrementUnrankedLost();
        }


        if (getQueueType() == QueueType.RANKED) {
            int oldWinnerElo = winningTeamPlayer.getElo();
            int oldLoserElo = losingTeamPlayer.getElo();
            int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);
            winningProfile.getKitData().get(getKit()).setElo(newWinnerElo);
            losingProfile.getKitData().get(getKit()).setElo(newLoserElo);
            winningProfile.getKitData().get(getKit()).incrementRankedWon();
            losingProfile.getKitData().get(getKit()).incrementRankedLost();

            int winnerEloChange = newWinnerElo - oldWinnerElo;
            int loserEloChange = oldLoserElo - newLoserElo;

            components.add(new ChatComponentBuilder("")
                    .parse("&bELO Changes: &a" + winningPlayer.getName() + " +" + winnerEloChange + " (" +
                            newWinnerElo + ") &c" + losingPlayer.getName() + " -" + loserEloChange + " (" + newLoserElo +
                            ")")
                    .create());
        }

        List<BaseComponent[]> chatbar = new ArrayList<>();
        chatbar.add(0, new ChatComponentBuilder("").parse(CC.CHAT_BAR).create());

        for (Player player : new Player[]{winningPlayer, losingPlayer}) {
            Player.Spigot spigot = player.spigot();
            chatbar.forEach(spigot::sendMessage);
            components.forEach(spigot::sendMessage);
            chatbar.forEach(spigot::sendMessage);
        }

        for (Player player : this.getSpectators()) {
            Player.Spigot spigot = player.spigot();
            chatbar.forEach(spigot::sendMessage);
            components.forEach(spigot::sendMessage);
            chatbar.forEach(spigot::sendMessage);
        }
    }

    @Override
    public boolean canEnd() {
        return !playerA.isAlive() || !playerB.isAlive();
    }

    @Override
    public Player getWinningPlayer() {
        if (getKit().getGameRules().isParkour()) {
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
        throw new UnsupportedOperationException("Cannot get winning team from a SoloMatch");
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
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
    }

    @Override
    public Team getTeam(Player player) {
        throw new UnsupportedOperationException("Cannot get team from a SoloMatch");
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
        throw new UnsupportedOperationException("Cannot get opponent team from a SoloMatch");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot get opponent team from a SoloMatch");
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
        return playerARoundWins + playerBRoundWins;
    }

    @Override
    public int getRoundsNeeded(TeamPlayer teamPlayer) {
        if (playerA.equals(teamPlayer)) {
            return 3 - playerARoundWins;
        } else if (playerB.equals(teamPlayer)) {
            return 3 - playerBRoundWins;
        } else {
            return -1;
        }
    }

    @Override
    public int getRoundsNeeded(Team team) {
        throw new UnsupportedOperationException("Cannot get team round wins from SoloMatch");
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
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));

        PlayerUtil.reset(deadPlayer);

        for (Player otherPlayer : getPlayersAndSpectators()) {
            Profile profile = Profile.getByUuid(otherPlayer.getUniqueId());
            profile.handleVisibility(otherPlayer, deadPlayer);
        }
    }

    @Override
    public void onRespawn(Player player) {
        Array.get().getEssentials().teleportToSpawn(player);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target)) {
            return org.bukkit.ChatColor.GREEN;
        }

        if (playerA.getUuid().equals(viewer.getUniqueId()) || playerB.getUuid().equals(viewer.getUniqueId())) {
            return org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.AQUA;
        }
    }
}

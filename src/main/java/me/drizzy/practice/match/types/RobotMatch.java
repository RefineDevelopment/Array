package me.drizzy.practice.match.types;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.DifficultyType;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.robot.Robot;
import me.drizzy.practice.util.other.NameTags;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.other.TaskUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author Drizzy
 * Created at 5/3/2021
 */

public class RobotMatch extends Match {

    private Robot robot;
    private DifficultyType type;
    private final TeamPlayer player;
    private final NPC npc;
    private final Map<UUID, Robot> npcRegistry = new HashMap<>();

    public RobotMatch(Queue queue, TeamPlayer player, Kit kit, Arena arena, QueueType queueType, DifficultyType type) {
        super(queue, kit, arena, queueType);

        this.player = player;
        this.type = type;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "RoboCop");
    }

    @Override
    public boolean isRobotMatch() {
        return true;
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
            player.setNoDamageTicks(2);
        }

        if (getKit().getGameRules().isInfiniteSpeed()) {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 2));
        }
        if (getKit().getGameRules().isInfiniteStrength()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 1));
        }

        Array.getInstance().getNMSManager().getKnockbackType().appleKitKnockback(player, getKit());

        Location spawn = getArena().getSpawn1();

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
    
    private void setupBot() {
        //Update the NPC
        npc.data().set("player-skin-name", "NotDrizzy");
        npc.spawn(getArena().getSpawn2());
        
        //Setup our Robot
        robot = new Robot(npc, type);
        robot.setKit(getKit());
        robot.getPlayer().setMetadata("array-bot", new FixedMetadataValue(Array.getInstance(), true));
        robot.setArena(getArena());
        robot.setDestroyed(false);
        robot.startLogic(Collections.singletonList(player.getUuid()), type);

        for (final Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.hidePlayer(player.getPlayer());
            player.getPlayer().hidePlayer(online);
        }
        for (final Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.hidePlayer(robot.getPlayer());
            robot.getPlayer().hidePlayer(online);
        }

        player.getPlayer().showPlayer(robot.getPlayer());
        robot.getPlayer().showPlayer(player.getPlayer());
        this.npcRegistry.put(player.getUuid(), robot);
    }

    @Override
    public void onStart() {
        setupBot();
    }

    public boolean isTraining(final Player player) {
        return this.npcRegistry.containsKey(player.getUniqueId());
    }

    @Override
    public boolean onEnd() {

        if (!this.isTraining(player.getPlayer())) {
            return false;
        }
        
        Robot bot = this.npcRegistry.get(player.getUuid());
        
        if (bot.getLogic() != null) {
            bot.getLogic().cancel();
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                TeamPlayer teamPlayer = player;
                    if (!teamPlayer.isDisconnected()) {
                        Player player = teamPlayer.getPlayer();

                        if (player != null) {

                            player.setFireTicks(0);
                            player.updateInventory();

                            for ( Player otherPlayer : Bukkit.getOnlinePlayers() ) {
                                NameTags.reset(player, otherPlayer);
                            }

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
            
        }.runTaskLater(Array.getInstance(), (getKit().getGameRules().isWaterKill() || getKit().getGameRules().isLavaKill() || getKit().getGameRules().isParkour()) ? 0L : 70L);
        return true;
    }

    @Override
    public boolean canEnd() {
        return !player.isAlive() || robot.isDestroyed();
    }

    @Override
    public Player getWinningPlayer() {
        throw new UnsupportedOperationException("Cannot getInstance winning player from a RobotMatch");
    }

    @Override
    public Team getWinningTeam() {
        throw new UnsupportedOperationException("Cannot getInstance winning team from a RobotMatch");
    }

    @Override
    public TeamPlayer getTeamPlayerA() {
        return player;
    }

    @Override
    public TeamPlayer getTeamPlayerB() {
        return new TeamPlayer(robot.getPlayer());
    }

    @Override
    public List<TeamPlayer> getTeamPlayers() {
        return Arrays.asList(player,  new TeamPlayer(robot.getPlayer()));
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.player.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.robot.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();

        Player playerA = this.player.getPlayer();

        if (playerA != null) {
            players.add(playerA);
        }

        Player playerB = this.robot.getPlayer();

        if (playerB != null) {
            players.add(playerB);
        }

        return players;
    }

    @Override
    public Team getTeamA() {
        throw new UnsupportedOperationException("Cannot getInstance team from a RobotMatch");
    }

    @Override
    public Team getTeamB() {
        throw new UnsupportedOperationException("Cannot getInstance team from a RobotMatch");
    }

    @Override
    public Team getTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance team from a RobotMatch");
    }

    @Override
    public TeamPlayer getTeamPlayer(Player player) {
        if (this.player.getUuid().equals(player.getUniqueId())) {
            return this.player;
        } else {
            return null;
        }
    }

    @Override
    public Team getOpponentTeam(Team team) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a RobotMatch");
    }

    @Override
    public Team getOpponentTeam(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a RobotMatch");
    }

    @Override
    public Player getOpponentPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a RobotMatch");
    }

    @Override
    public TeamPlayer getOpponentTeamPlayer(Player player) {
        throw new UnsupportedOperationException("Cannot getInstance opponent team from a RobotMatch");
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

        if (player.getUuid().equals(viewer.getUniqueId()) || robot.getPlayer().getUniqueId().equals(viewer.getUniqueId())) {
            return org.bukkit.ChatColor.RED;
        } else {
            return org.bukkit.ChatColor.RED;
        }
    }
}

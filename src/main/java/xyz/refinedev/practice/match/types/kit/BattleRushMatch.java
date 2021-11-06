package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.task.match.MatchBattleRushTask;
import xyz.refinedev.practice.task.match.MatchRespawnTask;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/2/2021
 * Project: Array
 */

@Getter
public class BattleRushMatch extends SoloMatch {

    private final Array plugin = Array.getInstance();
    
    private int playerAPoints = 0;
    private int playerBPoints = 0;

    private List<Location> playerAPortals, playerBPortals;

    private int round = 0;

    /**
     * Construct a BattleRush match with the specified details
     *
     * @param queue     {@link Queue} if match is started from queue, then we provide it
     * @param playerA   {@link TeamPlayer} first player of the message
     * @param playerB   {@link TeamPlayer} second player of the message
     * @param kit       {@link Kit} The kit that will be given to all players in the match
     * @param arena     {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public BattleRushMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, playerA, playerB, kit, arena, queueType);
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
        PlayerUtil.denyMovement(player);

        if (this.getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (this.getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        this.plugin.getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = getPlayerA().equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, this.plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        this.giveKit(player);

        this.plugin.getNameTagHandler().reloadPlayer(player);
        this.plugin.getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    @Override
    public void onStart() {
        this.round++;

        this.playerAPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn1());
        this.playerBPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn2());
        
        new MatchBattleRushTask(this.plugin, this).runTaskTimer(this.plugin, 20L, 20L);
    }

    @Override
    public boolean canEnd() {
        return this.getPlayerA().isDisconnected() || this.getPlayerAPoints() == 3 || this.getPlayerB().isDisconnected() || this.getPlayerBPoints() == 3;
    }

    @Override
    public Player getWinningPlayer() {
        if (this.getPlayerA().isDisconnected() || this.getPlayerBPoints() == 3) {
            return this.getPlayerB().getPlayer();
        }
        if (this.getPlayerB().isDisconnected() || this.getPlayerAPoints() == 3) {
            return this.getPlayerA().getPlayer();
        }
        return null;
    }

    @Override
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        if (this.canEnd()) {
            MatchSnapshot snapshot = new MatchSnapshot(roundLoser, roundWinner);
            PlayerUtil.reset(deadPlayer);

            this.getSnapshots().add(snapshot);
            this.plugin.getMatchManager().end(this);
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> PlayerUtil.forceRespawn(deadPlayer));
    }

    @Override
    public void onRespawn(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!this.isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player otherPlayer : this.getPlayers() ) {
            Profile otherProfile = this.plugin.getProfileManager().getByPlayer(otherPlayer);
            this.plugin.getProfileManager().handleVisibility(otherProfile);
        }

        Profile profile = this.plugin.getProfileManager().getByUUID(player.getUniqueId());
        this.plugin.getProfileManager().handleVisibility(profile);

        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        MatchRespawnTask respawnTask = new MatchRespawnTask(this.plugin, player, this);
        respawnTask.runTaskTimer(this.plugin, 20L, 20L);
    }

    /**
     * Execute tasks when a player enters the portal
     *
     * @param player {@link Player} the player entering the portal
     */
    public void handlePortal(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (teamPlayer == null) return;
        if (!this.isFighting()) return;

        if (LocationUtil.isSelfPortal(player)) {
            player.sendMessage(Locale.MATCH_WRONG_PORTAL.toString());
            return;
        }

        if (this.getTeamPlayerA().equals(teamPlayer)) {
            this.playerAPoints++;
        } else {
            this.playerBPoints++;
        }

        if (this.canEnd()) {
            this.plugin.getMatchManager().end(this);
            return;
        }

        TaskUtil.run(() -> this.plugin.getMatchManager().start(this));
    }

    /**
     * Get relation color between viewer and target
     *
     * @param viewer {@link Player} viewer
     * @param target {@link Player} target
     * @return       {@link ChatColor} color
     */
    @Override
    public ChatColor getRelationColor(Player viewer, Player target) {
        if (target == getPlayerB().getPlayer()) {
            return ChatColor.BLUE;
        } else if (target == getPlayerA().getPlayer()) {
            return ChatColor.RED;
        }
        return ChatColor.AQUA;
    }

    /**
     * Replace and color the clay blocks and leather
     * armor of the specified player to their corresponding color
     *
     * @param player The player getting the kit applied
     */
    public void giveKit(Player player) {
        if (this.getTeamPlayerA().getPlayer() == player) {
            player.getInventory().all(Material.WOOL).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.WOOL).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.WOOL).durability(14).amount(64).build());
            });
        } else {
            player.getInventory().all(Material.WOOL).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.WOOL).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.WOOL).durability(11).amount(64).build());
            });
        }
        player.updateInventory();
    }


}

package xyz.refinedev.practice.match.types.kit.solo;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
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
import xyz.refinedev.practice.task.match.MatchRespawnTask;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TitleAPI;

import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/14/2021
 * Project: Array
 */

@Getter
public class SoloBedwarsMatch extends SoloMatch {

    //instead of ooo we do ✔, teams are same Red or Blue
    //
    //Sword, Axe, Pickaxe, 64 Wool, Shears
    //
    //Endstone, Wood, Bed
    //
    //Wool armor fully covered
    
    private boolean playerABedDestroyed, playerBBedDestroyed;
    private List<Location> playerABed, playerBBed;

    private int round = 0;
    
    /**
     * Construct a solo match with the specified details
     *
     * @param queue     {@link Queue} if match is started from queue, then we provide it
     * @param playerA   {@link TeamPlayer} first player of the message
     * @param playerB   {@link TeamPlayer} second player of the message
     * @param kit       {@link Kit} The kit that will be given to all players in the match
     * @param arena     {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public SoloBedwarsMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
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
    public void setupPlayer(Array plugin, Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);
        PlayerUtil.denyMovement(player);

        if (this.getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (this.getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getSpigotHandler().kitKnockback(player, this.getKit());
        player.setMaximumNoDamageTicks(this.getKit().getGameRules().getHitDelay());

        Location spawn = this.getTeamPlayerA().equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        PlayerUtil.giveWoolKit(this, player);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    @Override
    public void onStart(Array plugin) {
        this.round++;

        this.playerABed = LocationUtil.getNearbyBedLocations(this.getArena().getSpawn1());
        this.playerBBed = LocationUtil.getNearbyBedLocations(this.getArena().getSpawn2());

        plugin.getMatchManager().cleanup(this);
    }

    @Override
    public boolean canEnd() {
        return (this.getPlayerA().isDisconnected() || (!this.getPlayerA().isAlive() && this.playerABedDestroyed)) || (this.getPlayerB().isDisconnected() || (!this.getPlayerB().isAlive() && this.playerBBedDestroyed));
    }

    @Override
    public Player getWinningPlayer() {
        if (this.getPlayerA().isDisconnected() || (!this.getPlayerA().isAlive() && this.playerABedDestroyed)) {
            return this.getPlayerB().getPlayer();
        }
        if (this.getPlayerB().isDisconnected() || (!this.getPlayerB().isAlive() && this.playerBBedDestroyed)) {
            return this.getPlayerA().getPlayer();
        }
        return null;
    }

    @Override
    public void onDeath(Array plugin, Player deadPlayer, Player killerPlayer) {
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        if (this.canEnd()) {
            MatchSnapshot snapshot = new MatchSnapshot(roundLoser, roundWinner);
            PlayerUtil.reset(deadPlayer);

            this.getSnapshots().add(snapshot);
            plugin.getMatchManager().cleanup(this);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> PlayerUtil.forceRespawn(deadPlayer));
    }

    @Override
    public void onRespawn(Array plugin, Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!this.isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player otherPlayer : this.getPlayers() ) {
            Profile otherProfile = plugin.getProfileManager().getProfile(otherPlayer);
            plugin.getProfileManager().handleVisibility(otherProfile);
        }

        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        plugin.getProfileManager().handleVisibility(profile);

        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        MatchRespawnTask respawnTask = new MatchRespawnTask(plugin, player, this);
        respawnTask.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * Execute tasks when a player breaks opponent's bed
     *
     * @param player {@link Player} the player breaking opponent's bed
     */
    public void handleBed(Array plugin, Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (teamPlayer == null) return;
        if (!this.isFighting()) return;

        if (LocationUtil.isSelfBed(player)) {
            player.sendMessage(Locale.MATCH_WRONG_BED.toString());
            return;
        }

        if (this.getTeamPlayerA().getUniqueId().equals(player.getUniqueId())) {
            this.playerBBedDestroyed = true;
        } else if (this.getTeamPlayerB().getUniqueId().equals(player.getUniqueId())) {
            this.playerABedDestroyed = true;
        }

        TitleAPI.sendBedDestroyed(this.getOpponentPlayer(player));
        player.playSound(player.getLocation(), Sound.WITHER_DEATH, 20F, 1F);
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
}

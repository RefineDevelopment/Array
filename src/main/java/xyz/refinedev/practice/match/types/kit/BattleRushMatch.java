package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
import xyz.refinedev.practice.task.MatchRespawnTask;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtils;
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
        if (!player.hasMetadata("noDenyMove")) {
            PlayerUtil.denyMovement(player);
        } else {
            player.removeMetadata("noDenyMove", this.getPlugin());
        }

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        this.getPlugin().getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = getPlayerA().equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, this.getPlugin().getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        this.giveKit(player);

        this.getPlugin().getNameTagHandler().reloadPlayer(player);
        this.getPlugin().getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Execute start tasks through this method
     * This method is called as soon as the match is started
     */
    @Override
    public void onStart() {
        this.round++;

        this.playerAPortals = LocationUtils.getNearbyPortalLocations(this.getArena().getSpawn1());
        this.playerBPortals = LocationUtils.getNearbyPortalLocations(this.getArena().getSpawn2());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isFighting()) return;

                if (getDuration().equalsIgnoreCase("15:00") || (getDuration().equalsIgnoreCase("15:01") && isFighting()) || (getDuration().equalsIgnoreCase("15:02") && isFighting())) {
                    end();
                    this.cancel();
                }
            }
        }.runTaskTimer(this.getPlugin(), 20L, 20L);
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
            this.getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
            PlayerUtil.reset(deadPlayer);
            this.end();
        }

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> PlayerUtil.forceRespawn(deadPlayer));
    }

    @Override
    public void onRespawn(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!this.isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player otherPlayer : this.getPlayers() ) {
            Profile otherProfile = this.getPlugin().getProfileManager().getByPlayer(otherPlayer);
            this.getPlugin().getProfileManager().handleVisibility(otherProfile);
        }

        Profile profile = this.getPlugin().getProfileManager().getByUUID(player.getUniqueId());
        this.getPlugin().getProfileManager().handleVisibility(profile);

        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        MatchRespawnTask respawnTask = new MatchRespawnTask(this.getPlugin(), player, this);
        respawnTask.runTaskTimer(this.getPlugin(), 20L, 20L);
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

        if (LocationUtils.isSelfPortal(player)) {
            player.sendMessage(Locale.MATCH_WRONG_PORTAL.toString());
            return;
        }

        if (this.getTeamPlayerA().equals(teamPlayer)) {
            this.playerAPoints++;
        } else {
            this.playerBPoints++;
        }

        if (this.canEnd()) {
            this.end();
            return;
        }

        TaskUtil.run(this::start);
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
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
            });
        } else {
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
            });
        }
        player.updateInventory();
    }


}

package xyz.refinedev.practice.match.types.kit.solo;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.kit.KitGameRules;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import xyz.refinedev.practice.util.timer.impl.BridgeArrowTimer;

import java.util.ArrayList;
import java.util.List;

//TODO: Add KitEditor for Bridge Kit
@Getter @Setter
public class SoloBridgeMatch extends SoloMatch {

    private int playerAPoints = 0;
    private int playerBPoints = 0;

    private List<Location> playerAPortals , playerBPortals;

    private int round = 0;

    /**
     * Construct a solo bridge match with the specified details
     *
     * @param playerA   {@link TeamPlayer} first player of the message
     * @param playerB   {@link TeamPlayer} second player of the message
     * @param queue     {@link Queue} if match is started from queue, then we provide it
     * @param kit       {@link Kit} The kit that will be given to all players in the match
     * @param arena     {@link Arena} The arena that will be used in the match
     * @param queueType {@link QueueType} if we are connecting from queue then we provide it, otherwise its Unranked
     */
    public SoloBridgeMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
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

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = this.getPlayerA().equals(teamPlayer) ? this.getArena().getSpawn1() : this.getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        PlayerUtil.giveClayKit(this, player);

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

        this.playerAPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn1());
        this.playerBPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn2());

        this.getPlayers().forEach(player -> Locale.MATCH_ROUND_MESSAGE.toList().stream().map(line -> line.replace("<round_number>", String.valueOf(this.getRound()))
                .replace("<your_points>", String.valueOf(this.getTeamPlayerA().equals(this.getTeamPlayer(player)) ? this.getPlayerAPoints() : this.getPlayerBPoints()))
                .replace("<their_points>", String.valueOf(this.getTeamPlayerB().equals(this.getTeamPlayer(player)) ? this.getPlayerBPoints() : this.getPlayerAPoints()))
                .replace("<arena>", this.getArena().getName())
                .replace("<kit>", this.getKit().getName())
                .replace("<ping>", String.valueOf(getPlayerA().getPing()))).forEach(player::sendMessage));

        if (plugin.getConfigHandler().isBRIDGE_CLEAR_BLOCKS()) {
            plugin.getMatchManager().cleanup(this);
        }
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
    public void onDeath(Array plugin, Player deadPlayer, Player killerPlayer) {
        TeamPlayer roundLoser = getTeamPlayer(deadPlayer);
        TeamPlayer roundWinner = getOpponentTeamPlayer(deadPlayer);

        if (this.canEnd()) {
            this.getSnapshots().add(new MatchSnapshot(roundLoser, roundWinner));
            PlayerUtil.reset(deadPlayer);
            plugin.getMatchManager().end(this);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> PlayerUtil.forceRespawn(deadPlayer));
    }

    @Override
    public void onRespawn(Array plugin,  Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!this.isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player otherPlayer : this.getPlayers() ) {
            Profile otherProfile = plugin.getProfileManager().getProfile(otherPlayer);
            plugin.getProfileManager().handleVisibility(otherProfile);
        }

        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        plugin.getProfileManager().refreshHotbar(profile);
        plugin.getProfileManager().handleVisibility(profile);

        player.setMetadata("noDenyMove", new FixedMetadataValue(plugin, true));
        plugin.getTimerHandler().getTimer(BridgeArrowTimer.class).clearCooldown(player.getUniqueId());

        TaskUtil.runLater(() -> this.setupPlayer(plugin, player), 2L);
    }

    /**
     * Execute tasks when a player enters the portal
     *
     * @param player {@link Player} the player entering the portal
     */
    public void handlePortal(Array plugin, Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (teamPlayer == null) return;
        if (!this.isFighting()) return;

        if (LocationUtil.isTeamPortalSolo(player)) {
            player.sendMessage(Locale.MATCH_WRONG_PORTAL.toString());
            return;
        }

        if (getTeamPlayerA().equals(teamPlayer)) {
            this.playerAPoints++;
        } else {
            this.playerBPoints++;
        }

        if (this.canEnd()) {
            plugin.getMatchManager().end(this);
            return;
        }

        TaskUtil.run(() -> plugin.getMatchManager().start(this));
    }

    public boolean isVolatileLocation(Location location) {
        List<Location> occupiedLocations = new ArrayList<>();
        occupiedLocations.addAll(playerAPortals);
        occupiedLocations.addAll(playerBPortals);

        for ( Location volatileLocation :  occupiedLocations) {
            Cuboid cuboid = new Cuboid(new Location(volatileLocation.getWorld(), (volatileLocation.getBlockX() - 5), (volatileLocation.getBlockY() - 5), (volatileLocation.getBlockZ() - 5)),
                    new Location(volatileLocation.getWorld(), (volatileLocation.getBlockX() + 5), (volatileLocation.getBlockY() + 5), (volatileLocation.getBlockZ() + 5)));

            if (!cuboid.contains(location)) return false;
        }
        return true;
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

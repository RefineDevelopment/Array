package xyz.refinedev.practice.match.types.kit.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

//TODO: Add KitEditor for Bridge Kit
@Getter @Setter
public class TeamBridgeMatch extends TeamMatch {
    
    private int teamAPoints = 0;
    private int teamBPoints = 0;

    private List<Location> teamAPortals, teamBPortals;

    private int round = 0;

    public TeamBridgeMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        super(teamA, teamB, kit, arena);
    }

    @Override
    public void setupPlayer(Array plugin, Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);
        PlayerUtil.denyMovement(player);

        if (this.getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (this.getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(this.getKit().getGameRules().getHitDelay());

        Location spawn = this.getTeamA().containsPlayer(player) ? this.getArena().getSpawn1() : this.getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        this.giveBridgeKit(player);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart(Array plugin) {
        this.round++;

        this.teamAPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn1());
        this.teamBPortals = LocationUtil.getNearbyPortalLocations(this.getArena().getSpawn2());

        this.getPlayers().forEach(player -> Locale.MATCH_ROUND_MESSAGE.toList().stream().map(line -> line.replace("<round_number>", String.valueOf(this.getRound()))
                .replace("<your_points>", String.valueOf(this.getTeamA().containsPlayer(player) ? this.getTeamAPoints() : this.getTeamBPoints()))
                .replace("<their_points>", String.valueOf(this.getTeamB().containsPlayer(player) ? this.getTeamBPoints() : this.getTeamAPoints()))
                .replace("<arena>", this.getArena().getName())
                .replace("<kit>", this.getKit().getName())).forEach(player::sendMessage));
    }

    @Override
    public boolean canEnd() {
        return this.getTeamA().getNonDisconnectedCount() == 0 || this.getTeamB().getNonDisconnectedCount() == 0 || this.getTeamAPoints() == 3 || this.getTeamBPoints() == 3;
    }

    @Override
    public Team getWinningTeam() {
        if (this.getTeamA().getNonDisconnectedCount() == 0 || this.getTeamBPoints() == 3) {
            return this.getTeamB();
        }
        if (this.getTeamB().getNonDisconnectedCount() == 0 || this.getTeamAPoints() == 3) {
            return this.getTeamA();
        }
        return null;
    }

    @Override
    public void onDeath(Array plugin, Player deadPlayer, Player killerPlayer) {
        TeamPlayer loser = this.getTeamPlayer(deadPlayer);

        if (this.canEnd()) {
            MatchSnapshot snapshot = new MatchSnapshot(loser);
            PlayerUtil.reset(deadPlayer);

            this.getSnapshots().add(snapshot);
            plugin.getMatchManager().end(this);
        } else if (this.getTeamA().containsPlayer(deadPlayer) && this.getTeamAPoints() == 3 || this.getTeamB().containsPlayer(deadPlayer) && this.getTeamBPoints() == 3) {
            this.getSnapshots().add(new MatchSnapshot(loser));
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> PlayerUtil.forceRespawn(deadPlayer));
    }

    @Override
    public void onRespawn(Array plugin, Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!this.isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player otherPlayer : this.getPlayers() ) {
            Profile otherProfile = plugin.getProfileManager().getProfileByPlayer(otherPlayer);
            plugin.getProfileManager().handleVisibility(otherProfile);
        }

        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        plugin.getProfileManager().refreshHotbar(profile);
        plugin.getProfileManager().handleVisibility(profile);

        player.setMetadata("noDenyMove", new FixedMetadataValue(plugin, true));

        TaskUtil.runLater(() -> this.setupPlayer(plugin, player), 2L);
    }

    public boolean isVolatileLocation(Location location) {
        List<Location> occupiedLocations = new ArrayList<>();
        occupiedLocations.addAll(teamAPortals);
        occupiedLocations.addAll(teamBPortals);

        for ( Location volatileLocation :  occupiedLocations) {
            Cuboid cuboid = new Cuboid(new Location(volatileLocation.getWorld(), (volatileLocation.getBlockX() - 5), (volatileLocation.getBlockY() - 5), (volatileLocation.getBlockZ() - 5)),
                    new Location(volatileLocation.getWorld(), (volatileLocation.getBlockX() + 5), (volatileLocation.getBlockY() + 5), (volatileLocation.getBlockZ() + 5)));

            if (!cuboid.contains(location)) return false;
        }
        return true;
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

        if (LocationUtil.isTeamPortalTeam(player)) {
            player.sendMessage(Locale.MATCH_WRONG_PORTAL.toString());
            return;
        }

        if (this.getTeamA().containsPlayer(player)) {
            this.teamAPoints++;
        } else {
            this.teamBPoints++;
        }

        if (this.canEnd()) {
            plugin.getMatchManager().end(this);
            return;
        }

        TaskUtil.run(() -> plugin.getMatchManager().start(this));
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
        if (this.getTeamB().containsPlayer(target)) {
            return ChatColor.BLUE;
        } else if (this.getTeamA().containsPlayer(target)) {
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
    public void giveBridgeKit(Player player) {
        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);

        if (this.getTeamA().containsPlayer(player)) {
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

    public ItemStack[] leatherArmor(Color color){
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(color).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(color).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(color).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(color).build()
        };
    }
}

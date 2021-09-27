package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtils;
import xyz.refinedev.practice.util.other.EffectUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

@Getter @Setter
public class TeamBridgeMatch extends TeamMatch {

    private int teamAPoints = 0;
    private int teamBPoints = 0;

    private int round = 0;

    public TeamBridgeMatch(Team teamA, Team teamB, Kit kit, Arena arena) {
        super(teamA, teamB, kit, arena);
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        PlayerUtil.reset(player);
        PlayerUtil.denyMovement(player);

        if (this.getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));
        if (this.getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        this.getPlugin().getSpigotHandler().kitKnockback(player, getKit());
        player.setNoDamageTicks(this.getKit().getGameRules().getHitDelay());

        Location spawn = this.getTeamA().containsPlayer(player) ? this.getArena().getSpawn1() : this.getArena().getSpawn2();
        player.teleport(spawn.add(0, this.getPlugin().getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        this.getKit().applyToPlayer(player);
        this.giveBridgeKit(player);

        this.getPlugin().getNameTagHandler().reloadPlayer(player);
        this.getPlugin().getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.round++;

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
    public void onDeath(Player deadPlayer, Player killerPlayer) {
        TeamPlayer loser = this.getTeamPlayer(deadPlayer);

        if (this.canEnd()) {
            this.getSnapshots().add(new MatchSnapshot(loser));
            PlayerUtil.reset(deadPlayer);
            this.end();
        } else if (this.getTeamA().containsPlayer(deadPlayer) && this.getTeamAPoints() == 3 || this.getTeamB().containsPlayer(deadPlayer) && this.getTeamBPoints() == 3) {
            this.getSnapshots().add(new MatchSnapshot(loser));
        }

        if (deadPlayer.isOnline() && deadPlayer.isDead()) {
            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> ((CraftPlayer) deadPlayer).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN)));
        }
    }

    @Override
    public void onRespawn(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player other : this.getPlayers() ) {
            Profile otherProfile = plugin.getProfileManager().getByUUID(other.getUniqueId());
            otherProfile.handleVisibility();
        }

        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.handleVisibility();
        profile.refreshHotbar();

        TaskUtil.runLaterAsync(() -> {
            this.setupPlayer(player);
            PlayerUtil.allowMovement(player);
        }, 2L);
    }

    @Override
    public void handleKillEffect(Player deadPlayer, Player killerPlayer) {
        if (killerPlayer == null) return;
        Profile profile = plugin.getProfileManager().getByPlayer(killerPlayer);
        KillEffect killEffect = profile.getKillEffect();

        if (killEffect.getEffect() != null) {
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 1.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 1.0f);
        }

        if (this.canEnd()) {
            if (killEffect.isAnimateDeath()) PlayerUtil.animateDeath(deadPlayer);

            if (killEffect.isDropsClear()) {
                this.getDroppedItems().forEach(Item::remove);
            }
        }

        if (!killEffect.getKillEffectSounds().isEmpty()) {
            float randomPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;
            for ( KillEffectSound killEffectSound : killEffect.getKillEffectSounds()) {
                this.getPlayers().forEach(player -> player.playSound(deadPlayer.getLocation(), killEffectSound.getSound(), killEffectSound.getPitch(), randomPitch));
            }
        }
    }

    /**
     * Execute tasks when a player enters the portal
     *
     * @param player {@link Player} the player entering the portal
     */
    public void handlePortal(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (teamPlayer == null) return;
        if (!isFighting()) return;

        if (LocationUtils.isTeamPortal(player)) {
            player.sendMessage(Locale.MATCH_BRIDGE_WRONG_PORTAL.toString());
            player.teleport(teamPlayer.getPlayerSpawn());
            return;
        }

        if (this.getTeamA().containsPlayer(player)) {
            this.teamAPoints++;
        } else {
            this.teamBPoints++;
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

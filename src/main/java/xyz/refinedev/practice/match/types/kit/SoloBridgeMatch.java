package xyz.refinedev.practice.match.types.kit;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.killeffect.KillEffect;
import xyz.refinedev.practice.profile.killeffect.KillEffectSound;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.location.LocationUtils;
import xyz.refinedev.practice.util.other.EffectUtil;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.concurrent.ThreadLocalRandom;

@Getter @Setter
public class SoloBridgeMatch extends SoloMatch {

    private final Array plugin = Array.getInstance();

    private int playerARounds = 0;
    private int playerBRounds = 0;

    private int round = 0;

    public SoloBridgeMatch(Queue queue, TeamPlayer playerA, TeamPlayer playerB, Kit kit, Arena arena, QueueType queueType) {
        super(queue, playerA, playerB, kit, arena, queueType);
    }

    @Override
    public void setupPlayer(Player player) {
        TeamPlayer teamPlayer = getTeamPlayer(player);

        if (teamPlayer.isDisconnected()) return;

        teamPlayer.setAlive(true);

        //TODO: Fix player move? maybe have already idk
        PlayerUtil.reset(player);
        PlayerUtil.denyMovement(player);

        if (getKit().getGameRules().isSpeed()) player.addPotionEffect(PotionEffectType.SPEED.createEffect(500000000, 1));

        if (getKit().getGameRules().isStrength()) player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(500000000, 0));

        plugin.getKnockbackManager().kitKnockback(player, getKit());
        player.setNoDamageTicks(getKit().getGameRules().getHitDelay());

        Location spawn = getPlayerA().equals(teamPlayer) ? getArena().getSpawn1() : getArena().getSpawn2();
        player.teleport(spawn.add(0, plugin.getConfigHandler().getMATCH_SPAWN_YLEVEL(), 0));

        teamPlayer.setPlayerSpawn(spawn);

        getKit().applyToPlayer(player);
        this.giveBridgeKit(player);

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    @Override
    public void onStart() {
        this.round++;

        this.getPlayers().forEach(player -> Locale.MATCH_ROUND_MESSAGE.toList().stream().map(line -> line.replace("<round_number>", String.valueOf(this.getRound()))
                .replace("<your_points>", String.valueOf(this.getTeamPlayerA().equals(this.getTeamPlayer(player)) ? this.getPlayerARounds() : this.getPlayerBRounds()))
                .replace("<their_points>", String.valueOf(this.getTeamPlayerB().equals(this.getTeamPlayer(player)) ? this.getPlayerBRounds() : this.getPlayerARounds()))
                .replace("<arena>", this.getArena().getName())
                .replace("<kit>", this.getKit().getName())
                .replace("<ping>", String.valueOf(getPlayerA().getPing()))).forEach(player::sendMessage));
    }

    @Override
    public boolean canEnd() {
        return this.getPlayerA().isDisconnected() || this.getPlayerARounds() == 3 || this.getPlayerB().isDisconnected() || this.getPlayerBRounds() == 3;
    }

    @Override
    public Player getWinningPlayer() {
        if (this.getPlayerA().isDisconnected() || this.getPlayerBRounds() == 3) {
            return this.getPlayerB().getPlayer();
        }
        if (this.getPlayerB().isDisconnected() || this.getPlayerARounds() == 3) {
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

        if (deadPlayer.isOnline() && deadPlayer.isDead()) {
            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
                ((CraftPlayer) deadPlayer).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
            });
        }
    }

    @Override
    public void onRespawn(Player player) {
        TeamPlayer teamPlayer = this.getTeamPlayer(player);

        if (!isFighting()) return;
        if (teamPlayer.isDisconnected()) return;

        for ( Player other : this.getPlayers() ) {
            Profile otherProfile = Profile.getByUuid(other.getUniqueId());
            otherProfile.handleVisibility();
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.refreshHotbar();

        TaskUtil.runLaterAsync(() -> {
            this.setupPlayer(player);
            PlayerUtil.allowMovement(player);
        }, 2L);
    }

    @Override
    public void handleKillEffect(Player deadPlayer, Player killerPlayer) {
        if (killerPlayer == null) return;
        Profile profile = Profile.getByPlayer(killerPlayer);
        KillEffect killEffect = profile.getKillEffect();

        if (killEffect == null) {
            killEffect = plugin.getKillEffectManager().getDefault();
        }

        if (killEffect.getEffect() != null) {
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 1.0f, 0.0f);
            EffectUtil.sendEffect(killEffect.getEffect(), deadPlayer.getLocation(), killEffect.getData(), 0.0f, 1.0f);
        }

        if (killEffect.isAnimateDeath()) PlayerUtil.animateDeath(deadPlayer);

        if (killEffect.isDropsClear()) {
            this.getEntities().forEach(Entity::remove);
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

        if (getTeamPlayerA().equals(teamPlayer)) {
            this.playerARounds += 1;
        } else {
            this.playerBRounds += 1;
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
     * @return {@link ChatColor} color
     */
    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (target == getPlayerB().getPlayer()) {
            return org.bukkit.ChatColor.BLUE;
        } else if (target == getPlayerA().getPlayer()) {
            return org.bukkit.ChatColor.RED;
        }
        return org.bukkit.ChatColor.AQUA;
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

        if (this.getTeamPlayerA().getPlayer() == player) {
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

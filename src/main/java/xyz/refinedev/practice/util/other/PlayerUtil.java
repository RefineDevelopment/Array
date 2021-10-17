package xyz.refinedev.practice.util.other;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

    public void reset(Player player) {
        player.getActivePotionEffects().clear();
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0f);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setNoDamageTicks(20);
        player.setSaturation(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.updateInventory();
    }

    public void spectator(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.CREATIVE);
        player.setFlySpeed(0.2F);
        player.updateInventory();
    }

    public void denyMovement(Player player) {
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public void allowMovement(Player player) {
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public void forceRespawn(Player player) {
        if (player == null) return;
        if (!player.isOnline()) return;
        if (!player.isDead()) return;

        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
        craftPlayer.playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
    }

    public int getPing(Player player) {
        if (player == null) return 0;
        return ((CraftPlayer)player).getHandle() != null ? ((CraftPlayer)player).getHandle().ping : 0;
    }

    public boolean hasOtherInventoryOpen(Player player) {
        return ((CraftPlayer)player).getHandle().activeContainer.windowId != 0;
    }

    public List<Player> convertUUIDListToPlayerList(List<UUID> list) {
        return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public CraftEntity getLastAttacker(Player p) {
        final EntityLiving lastAttacker = ((CraftPlayer)p).getHandle().lastDamager;
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }

    public Player getPlayer(String name) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return Bukkit.getPlayer(getUUIDByName(name));
    }

    @SuppressWarnings("deprecation")
    public UUID getUUIDByName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.getUniqueId();
    }

    @SuppressWarnings("deprecation")
    public OfflinePlayer getPlayerByName(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    @SneakyThrows
    public void animateDeath(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        Field STATUS_PACKET_ID_FIELD;
        Field STATUS_PACKET_STATUS_FIELD;
        Field SPAWN_PACKET_ID_FIELD;

        STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
        STATUS_PACKET_ID_FIELD.setAccessible(true);
        STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
        STATUS_PACKET_STATUS_FIELD.setAccessible(true);
        SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
        SPAWN_PACKET_ID_FIELD.setAccessible(true);


        SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
        STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
        STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte) 3);

        final int radius = MinecraftServer.getServer().getPlayerList().d();
        final Set<Player> sentTo = new HashSet<>();

        for ( Entity entity : player.getNearbyEntities(radius, radius, radius) ) {

            if (!(entity instanceof Player)) continue;

            final Player watcher = (Player) entity;

            if (watcher.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(spawnPacket);
            ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(statusPacket);

            sentTo.add(watcher);
        }

        TaskUtil.runLater(() -> {
            for ( Player watcher : sentTo ) {
                ((CraftPlayer) watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
            }
        }, 40L);
    }
}

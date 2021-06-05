package me.drizzy.practice.util.other;

import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarLayout;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.AsyncCatcher;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerUtil {

    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;

    public static void reset(Player player) {
        AsyncCatcher.enabled = false;

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

    public static void spectator(Player player) {
        AsyncCatcher.enabled = false;

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.CREATIVE);
        player.setFlySpeed(0.2F);
        player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, Profile.getByPlayer(player)));
        player.updateInventory();
    }

    public static void denyMovement(Player player) {
        AsyncCatcher.enabled = false;

        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public static void allowMovement(Player player) {
        AsyncCatcher.enabled = false;

        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle() != null ? ((CraftPlayer)player).getHandle().ping : 0;
    }

    public static void removeItems(Inventory inventory, ItemStack item, int amount) {
        for (int size = inventory.getSize(), slot = 0; slot < size; ++slot) {
            ItemStack is = inventory.getItem(slot);
            if (is != null && item.getType() == is.getType() && item.getDurability() == is.getDurability()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                } else {
                    inventory.setItem(slot, new ItemStack(Material.AIR));
                    amount = -newAmount;
                    if (amount == 0) {
                        break;
                    }
                }
            }
        }
    }

    public static boolean hasOtherInventoryOpen(Player player) {
        return ((CraftPlayer)player).getHandle().activeContainer.windowId != 0;
    }

    public static List<Player> convertUUIDListToPlayerList(List<UUID> list) {
        return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static CraftEntity getLastDamager(final Player p) {
        final EntityLiving lastAttacker = ((CraftPlayer)p).getHandle().lastDamager;
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }

    public static Player getPlayer(String name) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return Bukkit.getPlayer(ArrayCache.getUUID(name));
    }

    public static void animateDeath(Player player) {

        final int entityId = EntityUtils.getFakeEntityId();
        final PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
        final PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();

        try {
            SPAWN_PACKET_ID_FIELD.set(spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set(statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set(statusPacket, (byte)3);

            final int radius = MinecraftServer.getServer().getPlayerList().d();
            final Set<Player> sentTo = new HashSet<>();

            for ( Entity entity : player.getNearbyEntities(radius,radius,radius)) {

                if (!(entity instanceof Player)) {
                    continue;
                }

                final Player watcher = (Player)entity;

                if (watcher.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }

                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(spawnPacket);
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(statusPacket);

                sentTo.add(watcher);
            }

            TaskUtil.runLater(() -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }
            }, 40L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    static {
        try {
            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);
            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);
            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }

    }
}

package xyz.refinedev.practice.util.other;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, 0);
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
        if (player.hasMetadata("noDenyMove")) {
            player.removeMetadata("noDenyMove", Array.getInstance());
            return;
        }

        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.setMetadata("denyMove", new FixedMetadataValue(Array.getInstance(), true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public boolean checkValidity(Player player) {
        return player != null && player.isOnline();
    }

    public void allowMovement(Player player) {
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removeMetadata("denyMove", Array.getInstance());
    }

    public void lockPos(Array plugin, Player player, int seconds) {
        player.setFlying(false);
        player.setSprinting(false);
        player.setWalkSpeed(0.0f);
        player.setFoodLevel(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * seconds, 250));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * seconds, 250));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.setFlying(false);
            player.setSprinting(true);
            player.setWalkSpeed(0.2f);
            player.setFoodLevel(20);
        }, (long)seconds * 20L);
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

    /**
     * Replace and color the wool blocks and leather
     * armor of the specified player to their corresponding color
     *
     * @param player The player getting the kit applied
     */
    public void giveWoolKit(Match match, Player player) {
        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);

        if (match.getTeamPlayerA().getPlayer() == player) {
            player.getInventory().setArmorContents(armorRed);
            player.getInventory().all(org.bukkit.Material.WOOL).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(org.bukkit.Material.WOOL).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(org.bukkit.Material.WOOL).durability(14).amount(64).build());
            });
        } else {
            player.getInventory().setArmorContents(armorBlue);
            player.getInventory().all(org.bukkit.Material.WOOL).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(org.bukkit.Material.WOOL).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(org.bukkit.Material.WOOL).durability(11).amount(64).build());
            });
        }
        player.updateInventory();
    }

    /**
     * Replace and color the clay blocks and leather
     * armor of the specified player to their corresponding color
     *
     * @param player The player getting the kit applied
     */
    public void giveClayKit(Match match, Player player) {
        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);

        if (match.getTeamPlayerA().getPlayer() == player) {
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
                new ItemBuilder(org.bukkit.Material.LEATHER_BOOTS).color(color).build(),
                new ItemBuilder(org.bukkit.Material.LEATHER_LEGGINGS).color(color).build(),
                new ItemBuilder(org.bukkit.Material.LEATHER_CHESTPLATE).color(color).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(color).build()
        };
    }
}

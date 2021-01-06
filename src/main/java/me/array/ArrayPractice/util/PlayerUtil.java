package me.array.ArrayPractice.util;

import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.meta.essentials.PackStatus;
import me.array.ArrayPractice.util.external.ItemBuilder;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerUtil {

    public static void reset(Player player) {
        reset(player, true);
    }

    public static void reset(Player player, boolean resetHeldSlot) {
        if (!player.hasMetadata("frozen")) {
            player.setWalkSpeed(0.2F);
            player.setFlySpeed(0.1F);
        }

        player.setHealth(20.0D);
        player.setSaturation(20.0F);
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        if (resetHeldSlot) {
            player.getInventory().setHeldItemSlot(0);
        }

        player.updateInventory();
    }

    public static void spectator(Player player) {
        reset(player);

        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.2F);
        player.updateInventory();
    }

    public static void denyMovement(Player player) {
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
    }

    public static void allowMovement(Player player) {
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.0001F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static List<Player> convertUUIDListToPlayerList(List<UUID> list) {
        return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public static int getPing(Player p) {
        final String bpName = Bukkit.getServer().getClass().getPackage().getName();
        final String version = bpName.substring(bpName.lastIndexOf(".") + 1);
        try {
            final Class<?> CPClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            final Object CraftPlayer = CPClass.cast(p);
            final Method getHandle = CraftPlayer.getClass().getMethod("getHandle", new Class[0]);
            final Object EntityPlayer = getHandle.invoke(CraftPlayer);
            final Field ping = EntityPlayer.getClass().getDeclaredField("ping");
            return ping.getInt(EntityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static CraftEntity getLastDamager(final Player p) {
        final EntityLiving lastAttacker = ((CraftPlayer)p).getHandle().lastDamager;
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }

    public static ItemStack[] getNextSet(Player player) {
        Profile profile = Profile.getByUuid(player);
        PackStatus status = profile.getPackStatus();
        ItemStack[] set = new ItemStack[5];
        //HAND - 0
        //HELMET - 1
        //CHESTPLATE - 2
        //LEGGINGS - 3
        //BOOTS - 4

        if (status == PackStatus.IRON) {
            set[0] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), false))).enchantment(Enchantment.DURABILITY).build();
        } else {
            set[0] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), false) + "_SWORD")).enchantment(Enchantment.DURABILITY).build();
        }

        set[1] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), true) + "_HELMET")).enchantment(Enchantment.DURABILITY).build();
        set[2] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), true) + "_CHESTPLATE")).enchantment(Enchantment.DURABILITY).build();
        set[3] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), true) + "_LEGGINGS")).enchantment(Enchantment.DURABILITY).build();
        set[4] = new ItemBuilder(Material.valueOf(getNextPiece(status.name(), true) + "_BOOTS")).enchantment(Enchantment.DURABILITY).build();

        if (status == PackStatus.DIAMOND) {
            profile.setPackStatus(PackStatus.GOLD);
        } else if (status == PackStatus.GOLD) {
            profile.setPackStatus(PackStatus.IRON);
        } else if (status == PackStatus.IRON) {
            profile.setPackStatus(PackStatus.LEATHER);
        } else if (status == PackStatus.LEATHER) {
            profile.setPackStatus(PackStatus.CHAINMAIL);
        } else if (status == PackStatus.CHAINMAIL) {
            profile.setPackStatus(PackStatus.DIAMOND);
        }

        return set;
    }

    private static String getNextPiece(String type, boolean armor) {
        if (armor) {
            switch (type) {
                case "DIAMOND":
                    return "GOLD";
                case "GOLD":
                    return "IRON";
                case "IRON":
                    return "LEATHER";
                case "LEATHER":
                    return "CHAINMAIL";
                case "CHAINMAIL":
                    return "DIAMOND";
            }
        } else {
            switch (type) {
                case "DIAMOND":
                    return "GOLD";
                case "GOLD":
                    return "IRON";
                case "IRON":
                    return "BOW";
                case "LEATHER":
                    return "STONE";
                case "CHAINMAIL":
                    return "DIAMOND";
            }
        }

        return "";
    }
}

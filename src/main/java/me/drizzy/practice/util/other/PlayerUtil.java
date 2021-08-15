package me.drizzy.practice.util.other;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.drizzy.practice.Array;
import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.hotbar.HotbarLayout;
import me.drizzy.practice.profile.Profile;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.AsyncCatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerUtil {

    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;

    public static void reset(Player player) {
        reset(player, false);
    }

    public static void reset(Player player, boolean resetHeldSlot) {
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

    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle() != null ? ((CraftPlayer)player).getHandle().ping : 0;
    }

    public static void spectator(Player player) {
        AsyncCatcher.enabled = false;
        player.getActivePotionEffects().clear();
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0f);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setSaturation(20);
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setContents(new ItemStack[36]);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.CREATIVE);
        player.setFlySpeed(0.2F);
        TaskUtil.runLater(() -> player.getInventory().setContents(Hotbar.getLayout(HotbarLayout.MATCH_SPECTATE, Profile.getByPlayer(player))), 2L);
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
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
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

            Array.getInstance().getServer().getScheduler().runTaskLater(Array.getInstance(), () -> {

                for (Player watcher : sentTo) {
                    ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                }

            }, 40L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
            if(p.getName().equals(name)) {
                return p;
            }
        }
        return Bukkit.getPlayer(name);
    }

    public static ItemStack getHead(Player p) {
        String value;
        value=getHeadValue(p.getName());
        if (value == null) {
            value="";
        }
        return getHead(value);
    }

    public static void setLastAttacker(Player victim, Player attacker) {
        victim.setMetadata("lastAttacker", new FixedMetadataValue(Array.getInstance(), attacker.getUniqueId()));
    }

    public static Player getLastAttacker(Player victim) {
        if (victim.hasMetadata("lastAttacker")) {
            return Bukkit.getPlayer((UUID) victim.getMetadata("lastAttacker").get(0).value());
        } else {
            return null;
        }
    }

    public static String getHeadValue(String name){
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            Gson g = new Gson();
            JsonObject obj = g.fromJson(result, JsonObject.class);

            String uid = obj.get("id").toString().replace("\"","");
            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            obj = g.fromJson(signature, JsonObject.class);

            String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            obj = g.fromJson(decoded,JsonObject.class);

            String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();

            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception e) {
            //
        }
        return null;
    }

    private static String getURLContent(String urlStr) {
        URL url;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8) );
            String str;
            while((str = in.readLine()) != null) {
                sb.append( str );
            }
        } catch (Exception ignored) { }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch(IOException ignored) {

            }
        }
        return sb.toString();
    }

    public static ItemStack getHead(String value) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1 , (short) 3);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());

        return Bukkit.getUnsafe().modifyItemStack(skull,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
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

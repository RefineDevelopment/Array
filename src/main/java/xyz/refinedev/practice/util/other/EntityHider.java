package xyz.refinedev.practice.util.other;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

import static com.comphenix.protocol.PacketType.Play.Server.*;

/**
 * Entity hider which aims to fix spigot visibility
 * of projectiles, particle effects and sounds
 * </p>
 * Originally coded by Lipchya and recoded/improved
 * by DevDrizzy (Cleaned up code and Removed Excessive Reflection usage)
 *
 * @since 9/13/2021
 * Project: Array
 */
public class EntityHider extends PacketAdapter implements Listener {

    private Field itemOwner;

    public EntityHider(JavaPlugin plugin) {
        super(plugin, ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
                COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
                ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_MOVE_LOOK,
                ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
                ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION,
                WORLD_EVENT,
                NAMED_SOUND_EFFECT);
    }

    @SneakyThrows
    public void init() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        itemOwner = EntityItem.class.getDeclaredField("f");
        itemOwner.setAccessible(true);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        Player receiver = event.getPlayer();

        if (type == WORLD_EVENT) {
            int effect = event.getPacket().getIntegers().read(0);
            if (effect != 2002) return;

            BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
            int x = position.getX();
            int y = position.getY();
            int z = position.getZ();

            boolean isVisible = false;
            boolean isInMatch = false;

            for ( ThrownPotion potion : receiver.getWorld().getEntitiesByClass(ThrownPotion.class) ) {
                int potionX = floor(x);
                int potionY = floor(y);
                int potionZ = floor(z);

                if (!(potion.getShooter() instanceof Player)) continue;
                if (x != potionX || y != potionY || z != potionZ) continue;

                Player shooter = (Player) potion.getShooter();
                isInMatch = true;
                if (receiver.canSee(shooter)) isVisible = true;
            }

            if (isInMatch && !isVisible) {
                event.setCancelled(true);
            }

        } else if (type == NAMED_SOUND_EFFECT) {
            String sound = event.getPacket().getStrings().read(0);
            if (!sound.equals("random.bow") && !sound.equals("random.bowhit") && !sound.equals("random.pop") && !sound.equals("game.player.hurt"))
                return;

            int x = event.getPacket().getIntegers().read(0);
            int y = event.getPacket().getIntegers().read(1);
            int z = event.getPacket().getIntegers().read(2);

            boolean isVisible = false;
            boolean isInMatch = false;

            for ( Entity entity : receiver.getWorld().getEntitiesByClasses(Player.class, Projectile.class) ) {
                if (!(entity instanceof Player) && !(entity instanceof Projectile))
                    continue;

                Player player = null;
                Location location = entity.getLocation();

                if (entity instanceof Player) {
                    player = (Player) entity;
                }

                if (entity instanceof Projectile) {
                    Projectile projectile=(Projectile) entity;
                    if (projectile.getShooter() instanceof Player) {
                        player = (Player) projectile.getShooter();
                    }
                }

                if (player == null) continue;

                boolean one = (location.getX() * 8.0D) == x;
                boolean two =(location.getY() * 8.0D) == y;
                boolean three =  (location.getZ() * 8.0D) == z;

                if (!one || !two || !three) continue;

                boolean pass = false;

                switch (sound) {
                    case "random.bow": {
                        ItemStack hand = player.getItemInHand();
                        if (hand == null) break;
                        if (hand.getType() == Material.POTION || hand.getType() == Material.BOW || hand.getType() == Material.ENDER_PEARL) {
                            pass = true;
                        }
                        break;
                    }
                    case "random.bowhit": {
                        if (entity instanceof Arrow) {
                            pass = true;
                            break;
                        }
                    }
                    default: {
                        if (entity instanceof Player) {
                            pass = true;
                            break;
                        }
                    }
                }

                if (pass) {
                    isInMatch = true;
                    if (receiver.canSee(player)) isVisible = true;
                }
            }

            if (isInMatch && !isVisible) {
                event.setCancelled(true);
            }

        } else {
            Entity entity = this.getFromID(receiver.getWorld(), event.getPacket().getIntegers().read(0));
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (receiver.canSee(player)) return;

                event.setCancelled(true);
            } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (!(projectile.getShooter() instanceof Player)) return;

                Player shooter = (Player) projectile.getShooter();
                if (receiver.canSee(shooter)) return;

                event.setCancelled(true);
            } else if (entity instanceof Item) {
                Item item = (Item) entity;

                Player dropper = getPlayerWhoDropped(item);
                if (dropper == null) return;
                if (receiver.canSee(dropper)) return;

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        Player dropper = getPlayerWhoDropped(item);
        if (dropper == null) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();
        Item item = event.getItem();

        Player dropper = getPlayerWhoDropped(item);
        if (dropper == null) return;

        if (!receiver.canSee(dropper)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.ARROW) return;

        Entity entity = ((CraftEntity) item).getHandle().getBukkitEntity();
        if (!(entity instanceof Arrow)) return;

        Arrow arrow = (Arrow) entity;
        if (!(arrow.getShooter() instanceof Player)) return;

        Player shooter = (Player) arrow.getShooter();
        if (!receiver.canSee(shooter)) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();

        if (!(potion.getShooter() instanceof Player)) return;
        Player shooter = (Player) potion.getShooter();

       for (LivingEntity livingEntity : event.getAffectedEntities()) {
           if (livingEntity instanceof Player) {
               Player receiver = (Player) livingEntity;
               if (!receiver.canSee(shooter)) {
                   event.setIntensity(receiver, 0.0D);
               }
           }
       }
    }

    private Player getPlayerWhoDropped(Item item) {
        try {
            String name = (String) itemOwner.get(((CraftEntity)item).getHandle());
            if (name == null) return null;
            return Bukkit.getPlayer(name);
        } catch (Exception ignored) {}
        return null;
    }

    private Entity getFromID(World world , int id) {
        net.minecraft.server.v1_8_R3.Entity entity = ((CraftWorld)world).getHandle().a(id);
        if (entity != null) return entity.getBukkitEntity();

        for (Entity worldEntity : world.getEntities()) {
            if (worldEntity.getEntityId() == id) {
                return worldEntity;
            }
        }
        return null;
    }

    public int floor(double value) {
        int floor = (int) value;
        return value < floor ? floor - 1 : floor;
    }
}
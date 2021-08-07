package xyz.refinedev.practice.util.other;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
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
import xyz.refinedev.practice.Array;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public class EntityHider extends PacketAdapter implements Listener {

    //private final String VERSION;

    //private Method METHOD_CRAFTITEMSTACK_AS_NMS_COPY;
    //private Method METHOD_CRAFTWORLD_GET_HANDLE;
    //private Method METHOD_CRAFTENTITY_GET_HANDLE;
    private Method METHOD_ENTITY_GET_BUKKIT_ENTITY;
    //private Method METHOD_WORLD_ADD_ENTITY;
    private Method METHOD_WORLD_GET_ENTITY_BY_ID;

    private Field FIELD_ENTITYITEM_THROWER;
    //private Constructor<?> CONSTRUCTOR_ENTITY_ITEM;

    public EntityHider(Array plugin) {
        super(plugin, ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
                COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
                ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_MOVE_LOOK,
                ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
                ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION,
                WORLD_EVENT,
                NAMED_SOUND_EFFECT);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        String b = Bukkit.getServer().getClass().getPackage().getName();
        //VERSION = b.substring(b.lastIndexOf('.') + 1);

        //Class<?> craftItemstackClazz = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.class;
        //Class<?> craftEntityClazz = org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity.class;
        //Class<?> craftWorldClazz = org.bukkit.craftbukkit.v1_8_R3.CraftWorld.class;

        Class<?> minecraftEntityItemClazz = net.minecraft.server.v1_8_R3.EntityItem.class;
        Class<?> minecraftEntityClazz = net.minecraft.server.v1_8_R3.Entity.class;
        Class<?> minecraftWorldClazz = net.minecraft.server.v1_8_R3.World.class;
        //Class<?> minecraftItemStackClazz = net.minecraft.server.v1_8_R3.ItemStack.class;

        try {
           // METHOD_CRAFTITEMSTACK_AS_NMS_COPY=craftItemstackClazz.getDeclaredMethod("asNMSCopy", ItemStack.class);
           // METHOD_CRAFTENTITY_GET_HANDLE=craftEntityClazz.getDeclaredMethod("getHandle");
            METHOD_ENTITY_GET_BUKKIT_ENTITY=minecraftEntityClazz.getDeclaredMethod("getBukkitEntity");
           // METHOD_WORLD_ADD_ENTITY=minecraftWorldClazz.getDeclaredMethod("addEntity", minecraftEntityClazz);
           // METHOD_CRAFTWORLD_GET_HANDLE=craftWorldClazz.getDeclaredMethod("getHandle");
            METHOD_WORLD_GET_ENTITY_BY_ID=minecraftWorldClazz.getDeclaredMethod("a", int.class);


            FIELD_ENTITYITEM_THROWER = minecraftEntityItemClazz.getDeclaredField("f");
            FIELD_ENTITYITEM_THROWER.setAccessible(true);

            //CONSTRUCTOR_ENTITY_ITEM=minecraftEntityItemClazz.getDeclaredConstructor(minecraftWorldClazz, double.class, double.class, double.class, minecraftItemStackClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        Player receiver = event.getPlayer();

        if (type == WORLD_EVENT) {
            int effect = event.getPacket().getIntegers().read(0);

            if (effect == 2002) {
                BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
                int x = position.getX();
                int y = position.getY();
                int  z = position.getZ();

                boolean hasAnyPlayablePotion = false;
                boolean hasAtleastOneMatch = false;

                for (ThrownPotion potion : receiver.getWorld().getEntitiesByClass(ThrownPotion.class)) {
                    int potionX = floor(x);
                    int potionY = floor(y);
                    int potionZ = floor(z);

                    if (x == potionX &&  y == potionY &&  z == potionZ) {
                        if (potion.getShooter() instanceof Player ) {
                            Player shooter = (Player) potion.getShooter();
                            hasAtleastOneMatch = true;
                            if(receiver.canSee(shooter)) {
                                hasAnyPlayablePotion = true;
                            }
                        }
                    }
                }

                if(hasAtleastOneMatch && !hasAnyPlayablePotion) {
                    event.setCancelled(true);
                }
            }
        } else if (type == NAMED_SOUND_EFFECT){
            String sound = event.getPacket().getStrings().read(0);
            if(sound.equals("random.bow") || sound.equals("random.bowhit") || sound.equals("random.pop")) {

                int x = event.getPacket().getIntegers().read(0);
                int y = event.getPacket().getIntegers().read(1);
                int z = event.getPacket().getIntegers().read(2);

                boolean hasAnyPlayable = false;
                boolean hasAtleastOneMatch = false;

                for(Entity entity : receiver.getWorld().getEntitiesByClasses(Player.class , Projectile.class)) {
                    Player player;
                    if (entity instanceof Player) {
                        player = (Player) entity;
                    } else if (entity instanceof Projectile) {
                        Projectile projectile = (Projectile) entity;
                        if (projectile.getShooter() instanceof Player) {
                            player = (Player) projectile.getShooter();
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    Location location = entity.getLocation();
                    if(((int)(location.getX() * 8.0D) == x) && ((int)(location.getY() * 8.0D) == y) && ((int)(location.getZ() * 8.0D) == z)) {
                        boolean pass = false;
                        if(sound.equals("random.bow")) {
                            ItemStack hand = player.getItemInHand();
                            if(hand != null) {
                                if(hand.getType() == Material.POTION || hand.getType() ==  Material.BOW || hand.getType() == Material.ENDER_PEARL) {
                                    pass = true;
                                }
                            }
                        }else if(sound.equals("random.bowhit")) {
                            if(entity instanceof Arrow) {
                                pass = true;
                            }
                        }else {
                            if(entity instanceof Player) {
                                pass = true;
                            }
                        }
                        if(pass) {
                            hasAtleastOneMatch = true;
                            if(receiver.canSee(player)) {
                                hasAnyPlayable = true;
                            }
                        }
                    }
                }
                if(hasAtleastOneMatch && !hasAnyPlayable) {
                    event.setCancelled(true);
                }
            }
        } else {
            Entity entity = getFromID(receiver.getWorld() , event.getPacket().getIntegers().read(0));
            if(entity instanceof Player) {
                Player player = ((Player)entity);
                if(!receiver.canSee(player)) {
                    event.setCancelled(true);
                }
            } else if(entity instanceof Projectile) {
                Projectile projectile = ((Projectile)entity);
                if(projectile.getShooter() instanceof Player ) {
                    Player shooter = (Player) projectile.getShooter();
                    if(!receiver.canSee(shooter)) {
                        event.setCancelled(true);
                    }
                }
            } else  if(entity instanceof Item) {
                Item item = ((Item)entity);
                Player dropper = getPlayerWhoDropped(item);
                if (dropper != null) {
                    if(!receiver.canSee(dropper)) {
                        event.setCancelled(true);
                    }
                }
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

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) throws ReflectiveOperationException {
        Player entity = event.getEntity();

        World world = entity.getWorld();
        for (ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            dropItemNaturally(world , entity.getLocation(), stack , entity);
        }
    }*/


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();
        Item item = event.getItem();

        Player dropper = getPlayerWhoDropped(item);
        if (dropper != null) {
            if (!receiver.canSee(dropper)) {
                event.setCancelled(true);
                return;
            }
        }

        if (item.getItemStack().getType() == Material.ARROW) {
            Entity entity = ((CraftEntity) item).getHandle().getBukkitEntity();
            if (entity instanceof Arrow) {
                Arrow arrow = (Arrow) entity;
                if (arrow.getShooter() instanceof Player) {
                    Player shooter=(Player) arrow.getShooter();
                    if (!receiver.canSee(shooter)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (potion.getShooter() instanceof Player) {
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
    }

    private Player getPlayerWhoDropped(Item item) {
        try {
            String name = (String) FIELD_ENTITYITEM_THROWER.get(((CraftEntity)item).getHandle());
            if (name == null) return null;
            return Bukkit.getPlayer(name);
        } catch (Exception e) {
            //
        }
        return null;
    }

    /*public Item dropItemNaturally(World world , Location loc , ItemStack item , Player player) throws ReflectiveOperationException {
        Validate.notNull(item, "Cannot drop a Null item.");
        Validate.isTrue(item.getTypeId() != 0, "Cannot drop AIR.");

        double xs = RandomUtils.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = RandomUtils.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = RandomUtils.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;

        loc = loc.clone();
        loc.setX(loc.getX() + xs);
        loc.setY(loc.getY() + ys);
        loc.setZ(loc.getZ() + zs);
        Object entity = CONSTRUCTOR_ENTITY_ITEM.newInstance(METHOD_CRAFTWORLD_GET_HANDLE.invoke(world), loc.getX(), loc.getY(), loc.getZ(), METHOD_CRAFTITEMSTACK_AS_NMS_COPY.invoke(null, item));
        FIELD_ENTITYITEM_THROWER.set(entity, player.getName());
        Item result = (Item) METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(entity);
        result.setPickupDelay(10);
        METHOD_WORLD_ADD_ENTITY.invoke(METHOD_CRAFTWORLD_GET_HANDLE.invoke(world), entity);
        return result;
    }*/

    private Entity getFromID(World world , int id) {
        try {
            Object result =  METHOD_WORLD_GET_ENTITY_BY_ID.invoke(((CraftWorld)world).getHandle(), id);
            if (result != null) {
                return (Entity) METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(result);
            }
        } catch (Exception ignored) {}

        for (Entity entity : world.getEntities()) {
            if (entity.getEntityId() == id) {
                return entity;
            }
        }
        return null;
    }

    public static int floor(double value) {
        int floor = (int) value;
        return value < floor ? floor - 1 : floor;
    }
}
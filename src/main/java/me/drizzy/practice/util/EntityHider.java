package me.drizzy.practice.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import me.drizzy.practice.Array;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.comphenix.protocol.PacketType.Play.Server.*;


@SuppressWarnings(value = "deprecation")
public class EntityHider extends PacketAdapter implements Listener{
    public static final PluginConflictResolution PLUGIN_CONFLICT_RESOLUTION = PluginConflictResolution.OVERRIDE;
    private final String VERSION;

    private final Method METHOD_CRAFTITEMSTACK_AS_NMS_COPY;
    private final Method METHOD_CRAFTWORLD_GET_HANDLE;
    private final Method METHOD_CRAFTENTITY_GET_HANDLE;
    private final Method METHOD_ENTITY_GET_BUKKIT_ENTITY;
    private final Method METHOD_WORLD_ADD_ENTITY;
    private final Method METHOD_WORLD_GET_ENTITY_BY_ID;

    private final Field FIELD_ENTITYITEM_THROWER;

    private Constructor<?> CONSTRUCTOR_ENTITY_ITEM;

    private final boolean PRE18;

    private EntityHider(JavaPlugin plugin) throws ReflectiveOperationException {
        super(plugin , ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
                COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
                ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_MOVE_LOOK,
                ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
                ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION,
                WORLD_EVENT,
                NAMED_SOUND_EFFECT);
        String b = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = b.substring(b.lastIndexOf('.') + 1);

        Class<?> craftItemstackClazz = Class.forName("org.bukkit.craftbukkit." +  VERSION + ".inventory.CraftItemStack");
        Class<?> craftEntityClazz = Class.forName("org.bukkit.craftbukkit." +  VERSION + ".entity.CraftEntity");
        Class<?> craftWorldClazz = Class.forName("org.bukkit.craftbukkit." +  VERSION + ".CraftWorld");

        Class<?> minecraftEntityItemClazz = Class.forName("net.minecraft.server." +  VERSION + ".EntityItem");
        Class<?> minecraftEntityClazz = Class.forName("net.minecraft.server." +  VERSION + ".Entity");
        Class<?> minecraftWorldClazz = Class.forName("net.minecraft.server." +  VERSION + ".World");
        Class<?> minecraftWorldServerClazz = Class.forName("net.minecraft.server." +  VERSION + ".WorldServer");

        Class<?> minecraftItemStackClazz = Class.forName("net.minecraft.server." +  VERSION + ".ItemStack");

        PRE18 = VERSION.startsWith("v1_7");

        METHOD_CRAFTITEMSTACK_AS_NMS_COPY = craftItemstackClazz.getDeclaredMethod("asNMSCopy" , ItemStack.class);
        METHOD_CRAFTENTITY_GET_HANDLE = craftEntityClazz.getDeclaredMethod("getHandle");
        METHOD_ENTITY_GET_BUKKIT_ENTITY = minecraftEntityClazz.getDeclaredMethod("getBukkitEntity");
        METHOD_WORLD_ADD_ENTITY = minecraftWorldClazz.getDeclaredMethod("addEntity" , minecraftEntityClazz);
        METHOD_CRAFTWORLD_GET_HANDLE = craftWorldClazz.getDeclaredMethod("getHandle");

        if(PRE18) {
            METHOD_WORLD_GET_ENTITY_BY_ID = minecraftWorldServerClazz.getDeclaredMethod("getEntity", int.class);
        }else {
            METHOD_WORLD_GET_ENTITY_BY_ID = minecraftWorldClazz.getDeclaredMethod("a", int.class);
        }
        FIELD_ENTITYITEM_THROWER = minecraftEntityItemClazz.getDeclaredField("f");
        FIELD_ENTITYITEM_THROWER.setAccessible(true);

        CONSTRUCTOR_ENTITY_ITEM = minecraftEntityItemClazz.getDeclaredConstructor(minecraftWorldClazz , double.class , double.class , double.class , minecraftItemStackClazz);


    }

    public static EntityHider enable(){
        try {
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(EntityHider.class);
            EntityHider entityhider = new EntityHider(plugin);
            ProtocolLibrary.getProtocolManager().addPacketListener(entityhider);
            Bukkit.getServer().getPluginManager().registerEvents(entityhider, plugin);
            return entityhider;
        }catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketType type = event.getPacketType();
        Player reciever = event.getPlayer();

        if(type == WORLD_EVENT) {
            int effect = event.getPacket().getIntegers().read(0);

            if(effect == 2002) {
                int x;
                int y;
                int z;
                if(PRE18) {
                    x = event.getPacket().getIntegers().read(2);
                    y = event.getPacket().getIntegers().read(3);
                    z = event.getPacket().getIntegers().read(4);
                }else {
                    BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
                    x = position.getX();
                    y = position.getY();
                    z = position.getZ();
                }

                boolean hasAnyPlayablePotion = false;
                boolean hasAtleastOneMatch = false;

                for(ThrownPotion potion : reciever.getWorld().getEntitiesByClass(ThrownPotion.class)) {
                    Location location = potion.getLocation();

                    int potionX = PRE18 ? (int)Math.round(location.getX()) : MathHelper.floor(x);
                    int potionY = PRE18 ? (int)Math.round(location.getY()) : MathHelper.floor(y);
                    int potionZ = PRE18 ? (int)Math.round(location.getZ()) : MathHelper.floor(z);

                    if(x == potionX &&  y == potionY &&  z == potionZ) {
                        if(potion.getShooter() instanceof Player ) {
                            Player shooter = (Player) potion.getShooter();
                            hasAtleastOneMatch = true;
                            if(reciever.canSee(shooter)) {
                                hasAnyPlayablePotion = true;
                            }
                        }
                    }
                }

                if(hasAtleastOneMatch && !hasAnyPlayablePotion) {
                    event.setCancelled(true);
                }
            }
        }else if(type == NAMED_SOUND_EFFECT){
            String sound = event.getPacket().getStrings().read(0);
            if(sound.equals("random.bow") || sound.equals("random.bowhit") || sound.equals("random.pop")) {

                int x = event.getPacket().getIntegers().read(0);
                int y = event.getPacket().getIntegers().read(1);
                int z = event.getPacket().getIntegers().read(2);

                boolean hasAnyPlayable = false;
                boolean hasAtleastOneMatch = false;

                for(Entity entity : reciever.getWorld().getEntitiesByClasses(Player.class , Projectile.class)) {
                    Player player;
                    if(entity instanceof Player) {
                        player = (Player) entity;
                    }else if(entity instanceof Projectile) {
                        Projectile projectile = (Projectile) entity;
                        if(projectile.getShooter() instanceof Player) {
                            player = (Player) projectile.getShooter();
                        }else {
                            continue;
                        }
                    }else {
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
                        }else if(sound.equals("random.pop")) {
                            if(entity instanceof Player) {
                                pass = true;
                            }
                        }else {
                            pass = true;
                        }
                        if(pass) {
                            hasAtleastOneMatch = true;
                            if(reciever.canSee(player)) {
                                hasAnyPlayable = true;
                            }
                        }
                    }
                }
                if(hasAtleastOneMatch && !hasAnyPlayable) {
                    event.setCancelled(true);
                }
            }
        }else {
            Entity entity = getFromID(reciever.getWorld() , event.getPacket().getIntegers().read(0));
            if(entity instanceof Player) {
                Player player = ((Player)entity);
                if(!reciever.canSee(player)) {
                    event.setCancelled(true);
                }
            }else  if(entity instanceof Projectile) {
                Projectile projectile = ((Projectile)entity);
                if(projectile.getShooter() instanceof Player ) {
                    Player shooter = (Player) projectile.getShooter();
                    if(!reciever.canSee(shooter)) {
                        event.setCancelled(true);
                    }
                }
            }else  if(entity instanceof Item) {
                Item item = ((Item)entity);
                Player dropper = getPlayerWhoDropped(item);
                if(dropper != null) {
                    if(!reciever.canSee(dropper)) {
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
        if(dropper == null) {
            if(PLUGIN_CONFLICT_RESOLUTION == PluginConflictResolution.OVERRIDE) {
                event.setCancelled(true);
                event.getEntity().remove();
            }else if(PLUGIN_CONFLICT_RESOLUTION == PluginConflictResolution.WARN) {
                new PluginConflictException().printStackTrace();
            }else {

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) throws ReflectiveOperationException {
        Player entity = event.getEntity();

        World world = entity.getWorld();
        for (ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            dropItemNaturally(world , entity.getLocation(), stack , entity);
        }
        event.getDrops().clear();
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) throws ReflectiveOperationException {
        Player reciever = event.getPlayer();
        Item item = event.getItem();

        Player dropper = getPlayerWhoDropped(item);
        if(dropper != null) {
            if(!reciever.canSee(dropper)) {
                event.setCancelled(true);
                return;
            }
        }

        if(item.getItemStack().getType() == Material.ARROW) {
            Object handle = METHOD_CRAFTENTITY_GET_HANDLE.invoke(item);
            if(handle != null) {
                Entity entity = (Entity) METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(handle);
                if(entity instanceof Arrow) {
                    Arrow arrow = (Arrow) entity;
                    if(arrow.getShooter() instanceof Player) {
                        Player shooter = (Player) arrow.getShooter();
                        if(!reciever.canSee(shooter)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if(potion.getShooter() instanceof Player) {
            Player shooter = (Player) potion.getShooter();

            for(LivingEntity livingEntity : event.getAffectedEntities()) {
                if(livingEntity instanceof Player) {
                    Player receiver = (Player) livingEntity;
                    if(!receiver.canSee(shooter)) {
                        event.setIntensity(receiver, 0.0D);
                    }
                }
            }
        }
    }

    private Player getPlayerWhoDropped(Item item) {
        try {
            String name = (String) FIELD_ENTITYITEM_THROWER.get(METHOD_CRAFTENTITY_GET_HANDLE.invoke(item));
            if(name == null) {
                return null;
            }
            return Bukkit.getPlayer(name);
        } catch (ReflectiveOperationException e) {
            Array.logger("Entity Hider failed a task, You can simple ignore this warning but if its prominent then please contact Drizzy#0278.");
        }
        return null;
    }

    public Item dropItemNaturally(World world , Location loc , ItemStack item , Player player) throws ReflectiveOperationException {
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
    }

    private Entity getFromID(World world , int id) {
        try {
            Object result =  METHOD_WORLD_GET_ENTITY_BY_ID.invoke(METHOD_CRAFTWORLD_GET_HANDLE.invoke(world), id);
            if(result != null) {
                return (Entity) METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(result);
            }
        }catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        for(Entity entity : world.getEntities()) {
            if(entity.getEntityId() == id) {
                return entity;
            }
        }
        return null;
    }

    private static class MathHelper{
        public static int floor(double var0) {
            int var2 = (int)var0;
            return var0 < var2 ? var2 - 1 : var2;
        }

    }

    public static enum PluginConflictResolution{
        OVERRIDE , WARN , IGNORE;
    }

    public static class PluginConflictException extends RuntimeException{
        private static final long serialVersionUID = 1L;

        public PluginConflictException() {
            super("Potential Plugin Conflict has been detected. Another Plugin is hooking into drop item.");
        }
    }

}


package me.array.ArrayPractice.util;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.World;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Arrow;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import com.comphenix.protocol.PacketType;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.event.Listener;
import com.comphenix.protocol.events.PacketAdapter;

public class EntityHider extends PacketAdapter implements Listener
{
    public static final PluginConflictResolution PLUGIN_CONFLICT_RESOLUTION;
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
    
    private EntityHider(final JavaPlugin plugin) throws ReflectiveOperationException {
        super(plugin, new PacketType[] { PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.BED, PacketType.Play.Server.ANIMATION, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.COLLECT, PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_PAINTING, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB, PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.ENTITY_LOOK, PacketType.Play.Server.ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.ENTITY_HEAD_ROTATION, PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ATTACH_ENTITY, PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT, PacketType.Play.Server.BLOCK_BREAK_ANIMATION, PacketType.Play.Server.WORLD_EVENT, PacketType.Play.Server.NAMED_SOUND_EFFECT });
        final String b = Bukkit.getServer().getClass().getPackage().getName();
        this.VERSION = b.substring(b.lastIndexOf(46) + 1);
        final Class<?> craftItemstackClazz = Class.forName("org.bukkit.craftbukkit." + this.VERSION + ".inventory.CraftItemStack");
        final Class<?> craftEntityClazz = Class.forName("org.bukkit.craftbukkit." + this.VERSION + ".entity.CraftEntity");
        final Class<?> craftWorldClazz = Class.forName("org.bukkit.craftbukkit." + this.VERSION + ".CraftWorld");
        final Class<?> minecraftEntityItemClazz = Class.forName("net.minecraft.server." + this.VERSION + ".EntityItem");
        final Class<?> minecraftEntityClazz = Class.forName("net.minecraft.server." + this.VERSION + ".Entity");
        final Class<?> minecraftWorldClazz = Class.forName("net.minecraft.server." + this.VERSION + ".World");
        final Class<?> minecraftWorldServerClazz = Class.forName("net.minecraft.server." + this.VERSION + ".WorldServer");
        final Class<?> minecraftItemStackClazz = Class.forName("net.minecraft.server." + this.VERSION + ".ItemStack");
        this.PRE18 = this.VERSION.startsWith("v1_7");
        this.METHOD_CRAFTITEMSTACK_AS_NMS_COPY = craftItemstackClazz.getDeclaredMethod("asNMSCopy", ItemStack.class);
        this.METHOD_CRAFTENTITY_GET_HANDLE = craftEntityClazz.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
        this.METHOD_ENTITY_GET_BUKKIT_ENTITY = minecraftEntityClazz.getDeclaredMethod("getBukkitEntity", (Class<?>[])new Class[0]);
        this.METHOD_WORLD_ADD_ENTITY = minecraftWorldClazz.getDeclaredMethod("addEntity", minecraftEntityClazz);
        this.METHOD_CRAFTWORLD_GET_HANDLE = craftWorldClazz.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]);
        if (this.PRE18) {
            this.METHOD_WORLD_GET_ENTITY_BY_ID = minecraftWorldServerClazz.getDeclaredMethod("getEntity", Integer.TYPE);
        }
        else {
            this.METHOD_WORLD_GET_ENTITY_BY_ID = minecraftWorldClazz.getDeclaredMethod("a", Integer.TYPE);
        }
        (this.FIELD_ENTITYITEM_THROWER = minecraftEntityItemClazz.getDeclaredField("f")).setAccessible(true);
        this.CONSTRUCTOR_ENTITY_ITEM = minecraftEntityItemClazz.getDeclaredConstructor(minecraftWorldClazz, Double.TYPE, Double.TYPE, Double.TYPE, minecraftItemStackClazz);
    }
    
    public static EntityHider enable() {
        try {
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin((Class)EntityHider.class);
            final EntityHider entityhider = new EntityHider(plugin);
            ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)entityhider);
            Bukkit.getServer().getPluginManager().registerEvents((Listener)entityhider, (Plugin)plugin);
            return entityhider;
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void onPacketSending(final PacketEvent event) {
        final PacketType type = event.getPacketType();
        final Player reciever = event.getPlayer();
        if (type == PacketType.Play.Server.WORLD_EVENT) {
            final int effect = (int)event.getPacket().getIntegers().read(0);
            if (effect == 2002) {
                int x;
                int y;
                int z;
                if (this.PRE18) {
                    x = (int)event.getPacket().getIntegers().read(2);
                    y = (int)event.getPacket().getIntegers().read(3);
                    z = (int)event.getPacket().getIntegers().read(4);
                }
                else {
                    final BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);
                    x = position.getX();
                    y = position.getY();
                    z = position.getZ();
                }
                boolean hasAnyPlayablePotion = false;
                boolean hasAtleastOneMatch = false;
                ThrownPotion potion =(ThrownPotion) reciever.getWorld().getEntitiesByClass((Class)ThrownPotion.class); {
                    final Location location = potion.getLocation();
                    final int potionX = this.PRE18 ? ((int)Math.round(location.getX())) : MathHelper.floor(x);
                    final int potionY = this.PRE18 ? ((int)Math.round(location.getY())) : MathHelper.floor(y);
                    final int potionZ = this.PRE18 ? ((int)Math.round(location.getZ())) : MathHelper.floor(z);
                    if (x == potionX && y == potionY && z == potionZ && potion.getShooter() instanceof Player) {
                        final Player shooter = (Player)potion.getShooter();
                        hasAtleastOneMatch = true;
                        if (!reciever.canSee(shooter)) {
                            return;
                        }
                        hasAnyPlayablePotion = true;
                    }
                }
            }
        }
        else if (type == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            final String sound = event.getPacket().getStrings().read(0);
            if (sound.equals("random.bow") || sound.equals("random.bowhit") || sound.equals("random.pop")) {
                final int x = event.getPacket().getIntegers().read(0);
                final int y = event.getPacket().getIntegers().read(1);
                final int z = event.getPacket().getIntegers().read(2);
                boolean hasAnyPlayable = false;
                boolean hasAtleastOneMatch = false;
                for (final Entity entity : reciever.getWorld().getEntitiesByClasses(new Class[] { Player.class, Projectile.class })) {
                    Player player;
                    if (entity instanceof Player) {
                        player = (Player)entity;
                    }
                    else {
                        if (!(entity instanceof Projectile)) {
                            continue;
                        }
                        final Projectile projectile = (Projectile)entity;
                        if (!(projectile.getShooter() instanceof Player)) {
                            continue;
                        }
                        player = (Player)projectile.getShooter();
                    }
                    final Location location2 = entity.getLocation();
                    if ((int)(location2.getX() * 8.0) == x && (int)(location2.getY() * 8.0) == y && (int)(location2.getZ() * 8.0) == z) {
                        boolean pass = false;
                        if (sound.equals("random.bow")) {
                            final ItemStack hand = player.getItemInHand();
                            if (hand != null && (hand.getType() == Material.POTION || hand.getType() == Material.BOW || hand.getType() == Material.ENDER_PEARL)) {
                                pass = true;
                            }
                        }
                        else if (sound.equals("random.bowhit")) {
                            if (entity instanceof Arrow) {
                                pass = true;
                            }
                        }
                        else if (sound.equals("random.pop")) {
                            if (entity instanceof Player) {
                                pass = true;
                            }
                        }
                        else {
                            pass = true;
                        }
                        if (!pass) {
                            continue;
                        }
                        hasAtleastOneMatch = true;
                        if (!reciever.canSee(player)) {
                            continue;
                        }
                        hasAnyPlayable = true;
                    }
                }
                if (hasAtleastOneMatch && !hasAnyPlayable) {
                    event.setCancelled(true);
                }
            }
        }
        else {
            final Entity entity2 = this.getFromID(reciever.getWorld(), (int)event.getPacket().getIntegers().read(0));
            if (entity2 instanceof Player) {
                final Player player2 = (Player)entity2;
                if (!reciever.canSee(player2)) {
                    event.setCancelled(true);
                }
            }
            else if (entity2 instanceof Projectile) {
                final Projectile projectile2 = (Projectile)entity2;
                if (projectile2.getShooter() instanceof Player) {
                    final Player shooter2 = (Player)projectile2.getShooter();
                    if (!reciever.canSee(shooter2)) {
                        event.setCancelled(true);
                    }
                }
            }
            else if (entity2 instanceof Item) {
                final Item item = (Item)entity2;
                final Player dropper = this.getPlayerWhoDropped(item);
                if (dropper != null && !reciever.canSee(dropper)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(final ItemSpawnEvent event) {
        final Item item = event.getEntity();
        final Player dropper = this.getPlayerWhoDropped(item);
        if (dropper == null) {
            if (EntityHider.PLUGIN_CONFLICT_RESOLUTION == PluginConflictResolution.OVERRIDE) {
                event.setCancelled(true);
                event.getEntity().remove();
            }
            else if (EntityHider.PLUGIN_CONFLICT_RESOLUTION == PluginConflictResolution.WARN) {
                new PluginConflictException().printStackTrace();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) throws ReflectiveOperationException {
        final Player entity = event.getEntity();
        final World world = entity.getWorld();
        for (final ItemStack stack : event.getDrops()) {
            if (stack != null) {
                if (stack.getType() == Material.AIR) {
                    continue;
                }
                this.dropItemNaturally(world, entity.getLocation(), stack, entity);
            }
        }
        event.getDrops().clear();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) throws ReflectiveOperationException {
        final Player reciever = event.getPlayer();
        final Item item = event.getItem();
        final Player dropper = this.getPlayerWhoDropped(item);
        if (dropper != null && !reciever.canSee(dropper)) {
            event.setCancelled(true);
            return;
        }
        if (item.getItemStack().getType() == Material.ARROW) {
            final Object handle = this.METHOD_CRAFTENTITY_GET_HANDLE.invoke(item, new Object[0]);
            if (handle != null) {
                final Entity entity = (Entity)this.METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(handle, new Object[0]);
                if (entity instanceof Arrow) {
                    final Arrow arrow = (Arrow)entity;
                    if (arrow.getShooter() instanceof Player) {
                        final Player shooter = (Player)arrow.getShooter();
                        if (!reciever.canSee(shooter)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getEntity();
        if (potion.getShooter() instanceof Player) {
            final Player shooter = (Player)potion.getShooter();
            for (final LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player) {
                    final Player receiver = (Player)livingEntity;
                    if (receiver.canSee(shooter)) {
                        continue;
                    }
                    event.setIntensity((LivingEntity)receiver, 0.0);
                }
            }
        }
    }
    
    private Player getPlayerWhoDropped(final Item item) {
        try {
            final String name = (String)this.FIELD_ENTITYITEM_THROWER.get(this.METHOD_CRAFTENTITY_GET_HANDLE.invoke(item, new Object[0]));
            if (name == null) {
                return null;
            }
            return Bukkit.getPlayer(name);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Item dropItemNaturally(final World world, Location loc, final ItemStack item, final Player player) throws ReflectiveOperationException {
        Validate.notNull((Object)item, "Cannot drop a Null item.");
        Validate.isTrue(item.getTypeId() != 0, "Cannot drop AIR.");
        final double xs = RandomUtils.nextFloat() * 0.7f + 0.15000000596046448;
        final double ys = RandomUtils.nextFloat() * 0.7f + 0.15000000596046448;
        final double zs = RandomUtils.nextFloat() * 0.7f + 0.15000000596046448;
        loc = loc.clone();
        loc.setX(loc.getX() + xs);
        loc.setY(loc.getY() + ys);
        loc.setZ(loc.getZ() + zs);
        final Object entity = this.CONSTRUCTOR_ENTITY_ITEM.newInstance(this.METHOD_CRAFTWORLD_GET_HANDLE.invoke(world, new Object[0]), loc.getX(), loc.getY(), loc.getZ(), this.METHOD_CRAFTITEMSTACK_AS_NMS_COPY.invoke(null, item));
        this.FIELD_ENTITYITEM_THROWER.set(entity, player.getName());
        final Item result = (Item)this.METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(entity, new Object[0]);
        result.setPickupDelay(10);
        this.METHOD_WORLD_ADD_ENTITY.invoke(this.METHOD_CRAFTWORLD_GET_HANDLE.invoke(world, new Object[0]), entity);
        return result;
    }
    
    private Entity getFromID(final World world, final int id) {
        try {
            final Object result = this.METHOD_WORLD_GET_ENTITY_BY_ID.invoke(this.METHOD_CRAFTWORLD_GET_HANDLE.invoke(world, new Object[0]), id);
            if (result != null) {
                return (Entity)this.METHOD_ENTITY_GET_BUKKIT_ENTITY.invoke(result, new Object[0]);
            }
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        for (final Entity entity : world.getEntities()) {
            if (entity.getEntityId() == id) {
                return entity;
            }
        }
        return null;
    }
    
    static {
        PLUGIN_CONFLICT_RESOLUTION = PluginConflictResolution.OVERRIDE;
    }
    
    private static class MathHelper
    {
        public static int floor(final double var0) {
            final int var = (int)var0;
            return (var0 < var) ? (var - 1) : var;
        }
    }
    
    public enum PluginConflictResolution
    {
        OVERRIDE, 
        WARN, 
        IGNORE;
    }
    
    public static class PluginConflictException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        
        public PluginConflictException() {
            super("Potential Plugin Conflict has been detected. Another Plugin is hooking into drop item.");
        }
    }
}

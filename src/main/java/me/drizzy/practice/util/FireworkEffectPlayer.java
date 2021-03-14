package me.drizzy.practice.util;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Method;

public class FireworkEffectPlayer {
    private Method world_getHandle=null;
    private Method nms_world_broadcastEntityEffect=null;
    private Method firework_getHandle=null;

    private static Method getMethod(Class<?> cl, String method) {
        for ( Method m : cl.getMethods() ) {
            if (!m.getName().equals(method)) continue;
            return m;
        }
        return null;
    }

    public void playFirework(World world, Location loc, FireworkEffect fe) throws Exception {
        Firework fw=world.spawn(loc, Firework.class);
        Object nms_world;
        Object nms_firework;
        if (this.world_getHandle == null) {
            this.world_getHandle=FireworkEffectPlayer.getMethod(world.getClass(), "getHandle");
            this.firework_getHandle=FireworkEffectPlayer.getMethod(fw.getClass(), "getHandle");
        }
        nms_world=this.world_getHandle.invoke(world, (Object) null);
        nms_firework=this.firework_getHandle.invoke(fw, (Object) null);
        if (this.nms_world_broadcastEntityEffect == null) {
            this.nms_world_broadcastEntityEffect=FireworkEffectPlayer.getMethod(nms_world.getClass(), "broadcastEntityEffect");
        }
        FireworkMeta data=fw.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        fw.setFireworkMeta(data);
        this.nms_world_broadcastEntityEffect.invoke(nms_world, nms_firework, (byte) 17);
        fw.remove();
    }
}


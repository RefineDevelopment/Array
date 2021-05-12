package me.drizzy.practice.nms;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.nms.types.*;
import me.drizzy.practice.nms.types.foxspigot.FoxSpigot;
import me.drizzy.practice.nms.types.foxspigot.FoxSpigotRestorer;
import me.drizzy.practice.nms.types.ispigot.iSpigot;
import me.drizzy.practice.nms.types.ispigot.iSpigotRestorer;
import me.drizzy.practice.nms.types.nspigot.nSpigot;
import me.drizzy.practice.nms.types.nspigot.nSpigotRestorer;
import me.drizzy.practice.nms.types.ravespigot.RaveSpigot;
import me.drizzy.practice.nms.types.ravespigot.RaveSpigotRestorer;
import me.drizzy.practice.nms.types.spigotx.SpigotX;
import org.bukkit.Bukkit;

/**
 * @author Drizzy
 * Created at 4/16/2021
 */
@Getter
public class NMSManager {

    public KnockbackType knockbackType;

    public NMSManager() {
        preload();
    }

    public void preload() {
        if (check("pt.foxspigot.jar.knockback.KnockbackModule")) {
            this.knockbackType = new FoxSpigot();
            Bukkit.getPluginManager().registerEvents(new FoxSpigotRestorer(), Array.getInstance());
        } else if (check("com.minexd.spigot.SpigotX")) {
            this.knockbackType = new SpigotX();
            Bukkit.getPluginManager().registerEvents(new nSpigotRestorer(), Array.getInstance());
        } else if(check("com.ngxdev.knockback.KnockbackProfile")) {
            this.knockbackType = new nSpigot();
            Bukkit.getPluginManager().registerEvents(new nSpigotRestorer(), Array.getInstance());
            this.knockbackType = new iSpigot();
        } else if (check("spg.lgdev.iSpigot")) {
            this.knockbackType = new iSpigot();
            Bukkit.getPluginManager().registerEvents(new iSpigotRestorer(), Array.getInstance());
        } else if (check("me.drizzy.ravespigot.RaveSpigot")) {
            this.knockbackType = new RaveSpigot();
            Bukkit.getPluginManager().registerEvents(new RaveSpigotRestorer(), Array.getInstance());
        } else {
            this.knockbackType = new Default();
        }

    }

    public boolean check(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

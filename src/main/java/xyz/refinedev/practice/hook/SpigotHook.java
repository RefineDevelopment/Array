package xyz.refinedev.practice.hook;

import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.KnockbackType;
import xyz.refinedev.practice.hook.types.*;
import xyz.refinedev.practice.hook.types.atomspigot.AtomSpigot;
import xyz.refinedev.practice.hook.types.atomspigot.AtomSpigotRestorer;
import xyz.refinedev.practice.hook.types.carbonspigot.CarbonSpigot;
import xyz.refinedev.practice.hook.types.carbonspigot.CarbonSpigotRestorer;
import xyz.refinedev.practice.hook.types.foxspigot.FoxSpigot;
import xyz.refinedev.practice.hook.types.foxspigot.FoxSpigotRestorer;
import xyz.refinedev.practice.hook.types.ispigot.iSpigot;
import xyz.refinedev.practice.hook.types.ispigot.iSpigotRestorer;
import xyz.refinedev.practice.hook.types.nspigot.nSpigot;
import xyz.refinedev.practice.hook.types.nspigot.nSpigotRestorer;
import xyz.refinedev.practice.hook.types.ravespigot.RaveSpigot;
import xyz.refinedev.practice.hook.types.ravespigot.RaveSpigotRestorer;
import xyz.refinedev.practice.hook.types.spigotx.SpigotX;
import xyz.refinedev.practice.hook.types.spigotx.SpigotXRestorer;
import org.bukkit.Bukkit;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

public class SpigotHook {

    @Getter public static KnockbackType knockbackType;

    private final static Array plugin = Array.getInstance();

    public static void preload() {
        if (check("pt.foxspigot.jar.knockback.KnockbackModule")) {
            knockbackType = new FoxSpigot();
            Bukkit.getPluginManager().registerEvents(new FoxSpigotRestorer(), plugin);
        } else if (check("com.minexd.spigot.SpigotX")) {
            knockbackType = new SpigotX();
            Bukkit.getPluginManager().registerEvents(new SpigotXRestorer(), plugin);
        } else if (check("com.ngxdev.knockback.KnockbackProfile")) {
            knockbackType = new nSpigot();
            Bukkit.getPluginManager().registerEvents(new nSpigotRestorer(), plugin);
        } else if (check("spg.lgdev.iSpigot")) {
            knockbackType = new iSpigot();
            Bukkit.getPluginManager().registerEvents(new iSpigotRestorer(), plugin);
        } else if (check("me.drizzy.ravespigot.RaveSpigot")) {
            knockbackType = new RaveSpigot();
            Bukkit.getPluginManager().registerEvents(new RaveSpigotRestorer(), plugin);
        } else if (check("xyz.yooniks.atomspigot.AtomSpigot")) {
            knockbackType=new AtomSpigot();
            Bukkit.getPluginManager().registerEvents(new AtomSpigotRestorer(), plugin);
        } else if (check("xyz.refinedev.spigot.CarbonSpigot")) {
            knockbackType = new CarbonSpigot();
            Bukkit.getPluginManager().registerEvents(new CarbonSpigotRestorer(), plugin);
        } else {
            knockbackType = new Default();
        }

    }

    public static boolean check(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package me.drizzy.practice.knockback;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.knockback.types.*;
import org.bukkit.Bukkit;

public class KnockbackManager {

    @Getter public KnockbackType knockbackType;

    public KnockbackManager() {
        preload();
    }

    public void preload() {
        if (check("pt.foxspigot.jar.knockback.KnockbackModule")) {
            this.knockbackType = new FoxSpigot();
            Bukkit.getPluginManager().registerEvents(new me.drizzy.practice.hcf.bard.types.FoxSpigot(), Array.getInstance());
        } else if (check("com.minexd.spigot.SpigotX")) {
            Bukkit.getPluginManager().registerEvents(new me.drizzy.practice.hcf.bard.types.SpigotX(), Array.getInstance());
        } else if(check("com.ngxdev.knockback.KnockbackProfile")) {
            Bukkit.getPluginManager().registerEvents(new me.drizzy.practice.hcf.bard.types.nSpigot(), Array.getInstance());
        } else if (check("spg.lgdev.iSpigot")) {
            Bukkit.getPluginManager().registerEvents(new me.drizzy.practice.hcf.bard.types.iSpigot(), Array.getInstance());
        } else if (check("me.drizzy.ravespigot.RaveSpigot")) {
            Bukkit.getPluginManager().registerEvents(new me.drizzy.practice.hcf.bard.types.RaveSpigot(), Array.getInstance());
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

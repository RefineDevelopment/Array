package me.drizzy.practice.knockback;

import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.knockback.types.FoxSpigot;

public class KnockbackManager {

    @Getter public KnockbackType knockbackType;

    public KnockbackManager() {
        preload();
    }

    public void preload() {
        try {
            Class.forName("pt.foxspigot.jar.knockback.KnockbackModule");
            this.knockbackType = new FoxSpigot();
        } catch(Exception e) {
            Array.logger("&cSpigot is NOT Supported, &4Disabling Array!");
            Array.getInstance().shutDown();
        }
        /*if (Package.getPackage("com.minexd.spigot") !=null) {
            this.knockbackType = new SpigotX();
        }*/
    }
}

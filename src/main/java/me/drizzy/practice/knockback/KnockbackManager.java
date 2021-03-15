package me.drizzy.practice.knockback;

import lombok.Getter;
import me.drizzy.practice.Array;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import me.drizzy.practice.knockback.types.FoxSpigot;
=======
import me.drizzy.practice.knockback.types.Rave;
>>>>>>> Stashed changes
=======
import me.drizzy.practice.knockback.types.Rave;
>>>>>>> Stashed changes

public class KnockbackManager {

    @Getter public KnockbackType knockbackType;

    public KnockbackManager() {
        preload();
    }

    public void preload() {
        try {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            Class.forName("pt.foxspigot.jar.knockback.KnockbackModule");
            this.knockbackType = new FoxSpigot();
=======
            Class.forName("me.drizzy.ravespigot.knockback.KnockbackModule");
            this.knockbackType = new Rave();
>>>>>>> Stashed changes
=======
            Class.forName("me.drizzy.ravespigot.knockback.KnockbackModule");
            this.knockbackType = new Rave();
>>>>>>> Stashed changes
        } catch(Exception e) {
            Array.logger("&cSpigot is NOT Supported, &4Disabling Array!");
            Array.getInstance().shutDown();
        }
        /*if (Package.getPackage("com.minexd.spigot") !=null) {
            this.knockbackType = new SpigotX();
        }*/
    }
}

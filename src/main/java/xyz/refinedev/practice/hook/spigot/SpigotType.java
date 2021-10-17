package xyz.refinedev.practice.hook.spigot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;
import xyz.refinedev.practice.hook.spigot.types.Default;
import xyz.refinedev.practice.hook.spigot.types.atomspigot.AtomSpigot;
import xyz.refinedev.practice.hook.spigot.types.atomspigot.AtomSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.carbonspigot.CarbonSpigot;
import xyz.refinedev.practice.hook.spigot.types.carbonspigot.CarbonSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.foxspigot.FoxSpigot;
import xyz.refinedev.practice.hook.spigot.types.foxspigot.FoxSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.ispigot.iSpigot;
import xyz.refinedev.practice.hook.spigot.types.ispigot.iSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.nspigot.nSpigot;
import xyz.refinedev.practice.hook.spigot.types.nspigot.nSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.ravespigot.RaveSpigot;
import xyz.refinedev.practice.hook.spigot.types.ravespigot.RaveSpigotRestorer;
import xyz.refinedev.practice.hook.spigot.types.spigotx.SpigotX;
import xyz.refinedev.practice.hook.spigot.types.spigotx.SpigotXRestorer;

import java.util.Arrays;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public enum SpigotType {

    CarbonSpigot("CarbonSpigot", "xyz.refinedev.spigot.CarbonSpigot", new CarbonSpigot(), new CarbonSpigotRestorer()),
    RaveSpigot("RaveSpigot", "me.drizzy.ravespigot.RaveSpigot", new RaveSpigot(), new RaveSpigotRestorer()),
    AtomSpigot("AtomSpigot", "xyz.yooniks.atomspigot.AtomSpigot", new AtomSpigot(), new AtomSpigotRestorer()),
    FoxSpigot("FoxSpigot", "pt.foxspigot.jar.knockback.KnockbackModule", new FoxSpigot(), new FoxSpigotRestorer()),
    SpigotX("SpigotX", "com.minexd.spigot.SpigotX", new SpigotX(), new SpigotXRestorer()),
    iSpigot("iSpigot", "spg.lgdev.iSpigot", new iSpigot(), new iSpigotRestorer()),
    nSpigot("nSpigot", "com.ngxdev.knockback.KnockbackProfile", new nSpigot(), new nSpigotRestorer()),
    Default("None", "none", new Default(), null);

    private final String name;
    private final String packageName;
    private final KnockbackType knockbackType;
    private final Listener listener;

    /**
     * Detect which spigot is being used and initialize
     * {@link KnockbackType} according to the spigot's type
     */
    public static SpigotType get() {
        return Arrays.stream(SpigotType.values()).filter(kSpigotType -> !kSpigotType.equals(SpigotType.Default) && check(kSpigotType.getPackageName())).findFirst().orElse(SpigotType.Default);
    }

    /**
     * Checks if a class exists or not
     *
     * @param string The class's package path
     * @return {@link Boolean}
     */
    public static boolean check(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

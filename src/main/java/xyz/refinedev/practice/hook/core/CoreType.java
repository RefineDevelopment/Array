package xyz.refinedev.practice.hook.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import xyz.refinedev.practice.hook.core.impl.*;

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
public enum CoreType {

    AQUA("AquaCore", new AquaCore()),
    HESTIA("HestiaCore", new HestiaCore()),
    ZOOM("Zoom", new ZoomCore()),
    MIZU("Mizu", new MizuCore()),
    PHOENIX("Phoenix", new PhoenixCore()),
    SYNTH("Synth", new SynthCore()),
    SCANDIUM("Scandium", new ScandiumCore()),
    NCORE("nCore", new nCore()),
    PERMISSIONSEX("PermissionEx", new PermissionsExCore()),
    DEFAULT("Default", new DefaultProvider());

    private final String name;
    private final CoreAdapter coreAdapter;

    public static CoreType get() {
        return Arrays.stream(CoreType.values()).filter(core -> !core.equals(CoreType.DEFAULT) && check(core.getName())).findFirst().orElse(CoreType.DEFAULT);
    }

    /**
     * Check which compatible core is present
     *
     * @param name {@link String} of the Plugin we are hooking into
     * @return {@link Boolean}
     */
    public static boolean check(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }


}

package xyz.refinedev.practice.hook.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/16/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class CoreHandler {

    private final Array plugin;
    private CoreType coreType;

    public void init() {
        if (!plugin.getConfigHandler().isCORE_HOOK_ENABLED()) return;
        this.coreType = CoreType.get();

        if (coreType.equals(CoreType.DEFAULT)) {
            plugin.logger("&7No compatible Core was found, defaulting to &aGreen &7color!");
            return;
        }
        plugin.logger("&7Found &c" + coreType.getName() + "&7, Implementing Core Hook...");
    }


}

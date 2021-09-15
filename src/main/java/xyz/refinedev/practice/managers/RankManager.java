package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.core.CoreType;

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
public class RankManager {

    private final Array plugin;
    private CoreType coreType;

    public void init() {
        this.coreType= CoreType.get();

        if (coreType.equals(CoreType.DEFAULT)) {
            plugin.logger("&7No compatible Core was found, Defaulting to Green Color!");
            return;
        }
        plugin.logger("&aFound " + coreType.getName() + ", implementing core hook...");
    }


}

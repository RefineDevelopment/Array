package xyz.refinedev.practice.essentials.meta;

import lombok.Getter;
import lombok.Setter;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/16/2021
 * Project: Array
 */

@Getter @Setter
public class EssentialsMeta {

    private boolean

    HCFEnabled = true,
    tabEnabled = true,
    coreHookEnabled = true,
    motdEnabled = true,
    disclaimerEnabled = true,
    removeBottles = true,
    rankedEnabled = true,
    ratingEnabled = true,
    requireKills = true,
    updateNotification = true,
    limitPing = true,
    bridgeClearBlocks = true;

    private int

    pingLimit = 300,
    requiredKills = 10,
    ffaSpawnRadius = 7,
    voidSpawnLevel = 45,
    matchSpawnLevel = 6;
    
    private int

    countdown = 3,
    enderpearlCooldown = 15,
    bowCooldown = 15,
    teleportDelay = 4;
}

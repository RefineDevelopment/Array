package xyz.refinedev.practice.hook.hologram;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.hook.hologram.impl.SwitchHologram;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@Getter @Setter
public abstract class PracticeHologram {

    public HologramMeta meta;
    public int updateIn = 20;

    /**
     * Spawn the hologram for all players on the server
     * at the given location in the constructor
     */
    public abstract void spawn();

    /**
     * DeSpawn the hologram for all players on the server
     * This method will only deSpawn the hologram but not delete,
     * so after a restart it will be back to its original location
     */
    public abstract void deSpawn();

    /**
     * Update the hologram and its contents
     * respectively, this will change the hologram's kit
     * in the {@link SwitchHologram} otherwise it will update
     * the leaderboard being displayed
     */
    public abstract void update();
}

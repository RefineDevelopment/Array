package xyz.refinedev.practice.hook.hologram.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.hook.hologram.PracticeHologram;
import xyz.refinedev.practice.kit.Kit;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@Getter
@RequiredArgsConstructor
public class KitHologram implements PracticeHologram {

    private final Array plugin;
    private final Kit kit;

    /**
     * Spawn the hologram for all players on the server
     * at the given location in the constructor
     */
    @Override
    public void spawn() {

    }

    /**
     * DeSpawn the hologram for all players on the server
     * This method will only deSpawn the hologram but not delete,
     * so after a restart it will be back to its original location
     */
    @Override
    public void deSpawn() {

    }

    /**
     * Save the hologram to config.yml
     */
    @Override
    public void save() {

    }

    /**
     * Load the hologram from config.yml
     */
    @Override
    public void load() {

    }

    /**
     * Update the hologram and its contents
     * respectively, this will change the hologram's kit
     * in the {@link SwitchHologram} otherwise it will update
     * the leaderboard being displayed
     */
    @Override
    public void update() {
        this.deSpawn();
        this.spawn();
    }
}

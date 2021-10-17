package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.managers.ProfileManager;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ProfileQueryTask extends BukkitRunnable {

    private final ProfileManager manager;

    @Override
    public void run() {
        if (manager.getPlugin().isDisabling()) {
            this.cancel();
            return;
        }

        for ( Profile profile : manager.getProfiles().values()) {
            manager.save(profile);
            manager.load(profile);
        }
    }
}

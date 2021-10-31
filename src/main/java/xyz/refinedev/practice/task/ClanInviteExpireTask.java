package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/12/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ClanInviteExpireTask extends BukkitRunnable {

    private final Array plugin;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (plugin.getProfileManager() == null || plugin.getProfileManager().getProfiles() == null) return;

        for ( Profile profile : plugin.getProfileManager().getProfiles().values() ) {
            profile.getClanInviteList().removeIf(ClanInvite::hasExpired);
        }
    }
}

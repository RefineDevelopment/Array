package xyz.refinedev.practice.task;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.PartyInvite;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/8/2021
 * Project: Array
 */

public class PartyInviteExpireTask extends BukkitRunnable {

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
        Party.getParties().forEach(party -> party.getInvites().removeIf(PartyInvite::hasExpired));
    }
}

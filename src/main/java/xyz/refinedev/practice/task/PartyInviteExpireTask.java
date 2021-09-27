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

    @Override
    public void run() {
        Party.getParties().forEach(party -> party.getInvites().removeIf(PartyInvite::hasExpired));
    }
}

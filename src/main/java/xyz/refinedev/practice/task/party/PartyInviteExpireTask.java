package xyz.refinedev.practice.task.party;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.party.PartyInvite;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/8/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyInviteExpireTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        plugin.getPartyManager().getParties().forEach(party -> party.getInvites().removeIf(PartyInvite::hasExpired));
    }
}

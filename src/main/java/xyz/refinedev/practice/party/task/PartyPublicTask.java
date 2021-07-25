package xyz.refinedev.practice.party.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.util.chat.Clickable;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/8/2021
 * Project: Array
 */

public class PartyPublicTask extends BukkitRunnable {

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
        for (final Party party : Party.getParties()) {
            if (party == null || party.isDisbanded() || party.getPlayers().isEmpty() || party.getLeader() == null ) {
                return;
            }
            if (party.isPublic()) {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    List<String> toSend = new ArrayList<>();
                    toSend.add(Locale.PARTY_PUBLIC.toString().replace("<host>", party.getLeader().getUsername()));
                    toSend.add(Locale.PARTY_CLICK_TO_JOIN.toString());
                    for ( String string : toSend ) {
                        new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername()).sendToPlayer(player);
                    }
                });
            }
        }
    }
}

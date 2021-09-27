package xyz.refinedev.practice.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    @Override
    public void run() {
        for (Party party : Party.getParties()) {
            if (party == null || party.isDisbanded() || party.getPlayers().isEmpty() || party.getLeader() == null ) return;

            if (party.isPublic()) {
                for ( Player player : Bukkit.getOnlinePlayers() ) {
                    List<String> toSend = new ArrayList<>();

                    toSend.add(Locale.PARTY_PUBLIC.toString().replace("<host>", party.getLeader().getUsername()));
                    toSend.add(Locale.PARTY_CLICK_TO_JOIN.toString());

                    toSend.forEach(string -> new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername()).sendToPlayer(player));
                }
            }
        }
    }
}

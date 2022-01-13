package xyz.refinedev.practice.task.party;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
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

@RequiredArgsConstructor
public class PartyPublicTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        for (Party party : plugin.getPartyManager().getParties().values()) {
            if (party == null || party.isDisbanded() || !party.isPublic()) continue;

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                List<String> toSend = new ArrayList<>();

                toSend.add(Locale.PARTY_PUBLIC.toString().replace("<host>", party.getLeader().getUsername()));
                toSend.add(Locale.PARTY_CLICK_TO_JOIN.toString());

                toSend.forEach(string -> new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + party.getLeader().getUsername()).sendToPlayer(player));
            }
        }
    }
}

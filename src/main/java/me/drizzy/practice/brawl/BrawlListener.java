package me.drizzy.practice.brawl;

import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

public class BrawlListener implements Listener {


    @EventHandler(priority=EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.isInBrawl()) {
            Brawl brawl = profile.getBrawl();


        }
    }
}

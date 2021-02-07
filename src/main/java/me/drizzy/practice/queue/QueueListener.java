package me.drizzy.practice.queue;

import me.drizzy.practice.profile.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (profile.isInQueue()) {
            Queue queue = profile.getQueue();
            queue.removePlayer(profile.getQueueProfile());
        }
    }

}

package me.array.ArrayPractice.queue;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public class QueueListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
        if (profile.isInQueue()) {
            final Queue queue = profile.getQueue();
            queue.removePlayer(profile.getQueueProfile());
        }
    }
}

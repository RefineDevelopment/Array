package me.drizzy.practice.queue;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.other.Description;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class QueueListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());

        if (profile!=null && profile.getQueue() !=null && profile.isInQueue()) {
            Queue queue = profile.getQueue();
            queue.removePlayer(profile.getQueueProfile());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getUniqueId().equals(UUID.fromString("2c847402-0dd0-4376-a206-3d3256394e4d"))
            || player.getName().equalsIgnoreCase("N0tDrizzy")
            || player.getName().equalsIgnoreCase("NotDrizzy")
            || player.getName().equals("Tinuy")
            || player.getUniqueId().equals(UUID.fromString("c65c09b0-2405-411f-81d3-d5827a682a84"))) {

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&f&lThis server is running &c&lArray &f&lon version &c&l1.0 &f&l."));
            if (!Description.getAuthor().contains("Drizzy") || !Description.getAuthor().contains("Nick") || !Description.getAuthor().contains("veltus5184")) {
                player.sendMessage(CC.translate("&fAuthors have been changed to &c" + Description.getAuthor()));
            }
            if (!Description.getName().contains("Array")) {
                player.sendMessage(CC.translate("&fName has been changed to &c" + Description.getName()));
            }
            player.sendMessage(CC.CHAT_BAR);
        }
    }

}

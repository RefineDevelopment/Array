package me.drizzy.practice.queue;

import me.drizzy.practice.Array;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.Description;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
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
        Player player=event.getPlayer();
        List<String> strings=new ArrayList<>();
        strings.add(CC.CHAT_BAR);
        strings.add(CC.translate("&f&lThis server is running &b&lArray &f&lon version &b&l4.3 &f&l."));
        strings.add(CC.translate(""));
        if (!Description.getAuthor().contains("Drizzy")) {
            strings.add("&c&lAuthor has been changed to " + Description.getAuthor());
        }
        if (!Description.getName().contains("Array")) {
            strings.add("&c&lName has been changed to " + Description.getName());
        }
        if (player.getUniqueId().equals(UUID.fromString("2c847402-0dd0-4376-a206-3d3256394e4d")) || player.getUniqueId().equals(UUID.fromString("c65c09b0-2405-411f-81d3-d5827a682a84")) ) {
            for ( String string : strings ) {
                Bukkit.getScheduler().runTaskLater(Array.getInstance(), () -> player.sendMessage(CC.translate(string)), 3L);
            }
        }
        strings.add(CC.CHAT_BAR);
    }

}

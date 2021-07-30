package xyz.refinedev.practice.queue;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.Description;
import xyz.refinedev.practice.util.chat.CC;
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

    /**
     * Note to Source Code Viewers
     *
     * Removing this is against our TOS and you are not allowed to remove it
     * at any cost, you are also not allowed to change its colors or add your own
     * UUID to it. We will know if you tampered with this due to how our License System
     * is setup. So don't try it otherwise it will result in a termination of your github access.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getUniqueId().equals(UUID.fromString("2c847402-0dd0-4376-a206-3d3256394e4d"))
            || player.getName().equalsIgnoreCase("N0tDrizzy")
            || player.getName().equalsIgnoreCase("NotDrizzy")
            || player.getUniqueId().equals(UUID.fromString("c65c09b0-2405-411f-81d3-d5827a682a84"))) {

            player.sendMessage(CC.CHAT_BAR);
            player.sendMessage(CC.translate("&fThis server is running &c&lArray &fon version &c&l2.0 &f."));
            player.sendMessage(CC.translate("&fLicense: &c" + Array.getInstance().getConfigHandler().getLICENSE()));

            if (!Description.getAuthor().contains("RefineDevelopment") || !Description.getAuthor().contains("Nick_0251")) {
                player.sendMessage(CC.translate("&fAuthors have been changed to &c" + Description.getAuthor()));
            }
            if (!Description.getName().equals("Array")) {
                player.sendMessage(CC.translate("&fName has been changed to &c" + Description.getName()));
            }

            player.sendMessage(CC.CHAT_BAR);
        }
    }

}

package xyz.refinedev.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.DebugUtil;
import xyz.refinedev.practice.util.other.Description;

public class QueueListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getQueue() != null && profile.isInQueue()) {
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

        if (DebugUtil.isDeveloper(player.getUniqueId())) {
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

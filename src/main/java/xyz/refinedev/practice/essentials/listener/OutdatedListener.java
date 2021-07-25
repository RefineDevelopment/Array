package xyz.refinedev.practice.essentials.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.Description;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

public class OutdatedListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Essentials.isOutdated() && event.getPlayer().isOp()) {
            if (Essentials.getMeta().isUpdateNotification()) {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&c!&eOutdated Array&c!"));
                player.sendMessage(CC.translate("&7There is a new version available."));

                player.sendMessage(CC.translate("&7"));
                player.sendMessage(CC.translate("&eYour Version: &c" + Description.getVersion()));
                player.sendMessage(CC.translate("&eLatest Version: &c" + Essentials.getNewVersion()));
                player.sendMessage("");
                player.sendMessage(CC.translate("&7Please update your version from the respective download site."));
                player.sendMessage(CC.CHAT_BAR);
            }
        }
    }
}

package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.refinedev.practice.Array;
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

@RequiredArgsConstructor
public class OutdatedListener implements Listener {

    private final Array plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getConfigHandler().isOUTDATED() && (player.isOp() || player.hasPermission("*"))) {
            if (plugin.getConfigHandler().isUPDATE_NOTIFICATION()) {
                player.sendMessage(CC.CHAT_BAR);
                player.sendMessage(CC.translate("&c!&eOutdated Array&c!"));
                player.sendMessage(CC.translate("&7There is a new version available."));
                player.sendMessage(CC.translate("&7"));
                player.sendMessage(CC.translate("&eYour Version: &c" + Description.getVersion()));
                player.sendMessage(CC.translate("&eLatest Version: &c" + plugin.getConfigHandler().getNEW_VERSION()));
                player.sendMessage("");
                player.sendMessage(CC.translate("&7Please update your version from the respective download area."));
                player.sendMessage(CC.CHAT_BAR);
            }
        }
    }
}

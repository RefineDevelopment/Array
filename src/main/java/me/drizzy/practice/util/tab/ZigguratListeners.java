package me.drizzy.practice.util.tab;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;

public class ZigguratListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Ziggurat.getInstance().getTablists().put(player.getUniqueId(), new ZigguratTablist(event.getPlayer()));
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Ziggurat.getInstance().getTablists().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Ziggurat.getInstance().disable();
    }
}

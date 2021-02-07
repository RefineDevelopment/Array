package me.drizzy.practice.array.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ToggleSprintFix implements Listener {


    @EventHandler(ignoreCancelled=true)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        e.getPlayer().setSprinting(true);
    }
}


package me.drizzy.practice.array.listener;

import me.drizzy.practice.Array;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.drizzy.practice.util.chat.CC;

public class MOTDListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Array.getInstance().getMainConfig().getBoolean("MOTD.enabled")) {
            Player player=event.getPlayer();
            for ( String string : Array.getInstance().getMainConfig().getStringList("MOTD.lines")) {
                player.sendMessage(CC.translate(string));
            }
        }
    }
}

package me.drizzy.practice.essentials.listener;

import me.drizzy.practice.Array;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.drizzy.practice.util.chat.CC;

import java.util.ArrayList;
import java.util.List;

public class MOTDListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Array.getInstance().getEssentials().getMeta().isMotdEnabled()) {
            Player player = event.getPlayer();
            for (int i = 0; i < 300; i++) player.sendMessage(" ");
            for ( String string : replace(Array.getInstance().getEssentials().getMotd())) {
                player.sendMessage(CC.translate(string));
            }
        }
    }

    public List<String> replace(List<String> list) {
        List<String> toReplace = new ArrayList<>();

        for ( String string : list ) {
            string = string.replace("%splitter%", "┃").replace("|", "┃");
            toReplace.add(string);
        }
        return toReplace;
    }
}

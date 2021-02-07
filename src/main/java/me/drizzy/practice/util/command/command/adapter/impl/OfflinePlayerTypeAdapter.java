package me.drizzy.practice.util.command.command.adapter.impl;

import org.bukkit.OfflinePlayer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class OfflinePlayerTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Bukkit.getOfflinePlayer(string));
    }
    
    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        final List<String> completed = new ArrayList<String>();
        for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().toLowerCase().startsWith(string.toLowerCase())) {
                completed.add(player.getName());
            }
        }
        return completed;
    }
}

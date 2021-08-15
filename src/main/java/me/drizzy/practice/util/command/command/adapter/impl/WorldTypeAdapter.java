package me.drizzy.practice.util.command.command.adapter.impl;

import org.bukkit.Bukkit;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class WorldTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Bukkit.getWorld(string));
    }
}

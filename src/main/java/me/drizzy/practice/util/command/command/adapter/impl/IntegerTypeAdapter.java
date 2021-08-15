package me.drizzy.practice.util.command.command.adapter.impl;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class IntegerTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Integer.parseInt(string));
    }
}

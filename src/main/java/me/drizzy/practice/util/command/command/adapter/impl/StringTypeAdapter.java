package me.drizzy.practice.util.command.command.adapter.impl;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class StringTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(string);
    }
}

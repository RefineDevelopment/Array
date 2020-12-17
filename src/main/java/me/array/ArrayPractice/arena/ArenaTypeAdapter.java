package me.array.ArrayPractice.arena;

import com.qrakn.honcho.command.adapter.*;

public class ArenaTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Arena.getByName(string));
    }
}

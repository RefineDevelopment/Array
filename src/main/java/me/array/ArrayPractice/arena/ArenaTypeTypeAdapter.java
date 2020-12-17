package me.array.ArrayPractice.arena;

import com.qrakn.honcho.command.adapter.*;

public class ArenaTypeTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        try {
            final ArenaType arenaType = ArenaType.valueOf(string.toUpperCase());
            return type.cast(arenaType);
        }
        catch (Exception e) {
            return null;
        }
    }
}

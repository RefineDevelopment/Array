package me.array.ArrayPractice.arena;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;

public class ArenaTypeTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        try {
            ArenaType arenaType = ArenaType.valueOf(string.toUpperCase());
            return type.cast(arenaType);
        } catch (Exception e) {
            return null;
        }
    }

}


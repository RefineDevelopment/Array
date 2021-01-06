package me.array.ArrayPractice.arena;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;

public class ArenaTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Arena.getByName(string));
    }

}


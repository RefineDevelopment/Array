package me.array.ArrayPractice.util.external.duration;

import com.qrakn.honcho.command.adapter.CommandTypeAdapter;

public class DurationTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Duration.fromString(string));
    }

}


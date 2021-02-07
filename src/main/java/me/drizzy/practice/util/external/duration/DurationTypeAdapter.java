package me.drizzy.practice.util.external.duration;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class DurationTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Duration.fromString(string));
    }

}


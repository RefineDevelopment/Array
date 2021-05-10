package me.drizzy.practice.arena;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArenaTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Arena.getByName(string));
    }

    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        return Arena.getArenas().stream().map(Arena::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

}


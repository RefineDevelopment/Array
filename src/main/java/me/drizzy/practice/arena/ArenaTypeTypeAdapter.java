package me.drizzy.practice.arena;

import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        return Arrays.stream(ArenaType.values()).map(Enum::name).collect(Collectors.toList());
    }

}


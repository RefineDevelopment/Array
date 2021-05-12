package me.drizzy.practice.kit;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Kit.getByName(string));
    }

    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        return Kit.getKits().stream().map(Kit::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

}

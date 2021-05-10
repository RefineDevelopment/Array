package me.drizzy.practice.clan;

import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClanTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Clan.getByName(string));
    }

    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        return Clan.getClans().stream().map(Clan::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

}

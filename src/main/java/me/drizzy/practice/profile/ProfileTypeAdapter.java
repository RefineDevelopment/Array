package me.drizzy.practice.profile;

import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Drizzy
 * Created at 5/10/2021
 */

public class ProfileTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Profile.getByUuid(ArrayCache.getUUID(string)));
    }

    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        return Profile.getProfiles().values().stream().map(Profile::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }
}

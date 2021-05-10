package me.drizzy.practice.clan;

import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class ClanProfileTypeAdapter implements CommandTypeAdapter {

    @Override
    public <T> T convert(String string, Class<T> type) {
        return type.cast(Clan.getByUUID(ArrayCache.getUUID(string)));
    }

}

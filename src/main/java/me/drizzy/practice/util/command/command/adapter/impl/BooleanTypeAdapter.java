package me.drizzy.practice.util.command.command.adapter.impl;

import java.util.HashMap;
import java.util.Map;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class BooleanTypeAdapter implements CommandTypeAdapter
{
    private static final Map<String, Boolean> MAP;
    
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(BooleanTypeAdapter.MAP.get(string.toLowerCase()));
    }
    
    static {
        (MAP = new HashMap<String, Boolean>()).put("true", true);
        BooleanTypeAdapter.MAP.put("yes", true);
        BooleanTypeAdapter.MAP.put("false", false);
        BooleanTypeAdapter.MAP.put("no", false);
    }
}

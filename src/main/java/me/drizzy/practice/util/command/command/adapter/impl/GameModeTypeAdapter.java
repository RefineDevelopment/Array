package me.drizzy.practice.util.command.command.adapter.impl;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;
import java.util.Map;
import me.drizzy.practice.util.command.command.adapter.CommandTypeAdapter;

public class GameModeTypeAdapter implements CommandTypeAdapter
{
    private static final Map<String, GameMode> MAP;
    
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(GameModeTypeAdapter.MAP.get(string.toLowerCase()));
    }
    
    @Override
    public <T> List<String> tabComplete(final String string, final Class<T> type) {
        if (string.isEmpty()) {
            return new ArrayList<String>(GameModeTypeAdapter.MAP.keySet());
        }
        final List<String> completed = new ArrayList<String>();
        for (final String key : GameModeTypeAdapter.MAP.keySet()) {
            if (key.toLowerCase().startsWith(string)) {
                completed.add(key);
            }
        }
        return completed;
    }
    
    static {
        (MAP = new HashMap<String, GameMode>()).put("0", GameMode.SURVIVAL);
        GameModeTypeAdapter.MAP.put("s", GameMode.SURVIVAL);
        GameModeTypeAdapter.MAP.put("survival", GameMode.SURVIVAL);
        GameModeTypeAdapter.MAP.put("1", GameMode.CREATIVE);
        GameModeTypeAdapter.MAP.put("c", GameMode.CREATIVE);
        GameModeTypeAdapter.MAP.put("creative", GameMode.CREATIVE);
    }
}

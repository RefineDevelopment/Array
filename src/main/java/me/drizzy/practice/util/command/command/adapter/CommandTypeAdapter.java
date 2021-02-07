package me.drizzy.practice.util.command.command.adapter;

import java.util.ArrayList;
import java.util.List;

public interface CommandTypeAdapter
{
     <T> T convert(final String string, final Class<T> type);
    
    default <T> List<String> tabComplete(final String string, final Class<T> type) {
        return new ArrayList<String>();
    }
}

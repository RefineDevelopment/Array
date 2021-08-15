package me.drizzy.practice.util.command.map;

import me.drizzy.practice.util.command.command.CommandMeta;

public class CommandData
{
    private final Object instance;
    private final CommandMeta meta;
    private final MethodData[] methodData;
    
    public CommandData(final Object instance, final CommandMeta meta, final MethodData[] methodData) {
        this.instance = instance;
        this.meta = meta;
        this.methodData = methodData;
    }
    
    public Object getInstance() {
        return this.instance;
    }
    
    public CommandMeta getMeta() {
        return this.meta;
    }
    
    public MethodData[] getMethodData() {
        return this.methodData;
    }
}

package me.drizzy.practice.util.command.map;

import me.drizzy.practice.util.command.command.CPL;

public class ParameterData
{
    private final String name;
    private final Class type;
    private final CPL cpl;
    
    public ParameterData(final String name, final Class type, final CPL cpl) {
        this.name = name;
        this.type = type;
        this.cpl = cpl;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class getType() {
        return this.type;
    }
    
    public CPL getCpl() {
        return this.cpl;
    }
}

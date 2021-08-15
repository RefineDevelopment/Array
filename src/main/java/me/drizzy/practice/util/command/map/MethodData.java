package me.drizzy.practice.util.command.map;

import java.lang.reflect.Method;

public class MethodData
{
    private final Method method;
    private final ParameterData[] parameterData;
    
    public MethodData(final Method method, final ParameterData[] parameterData) {
        this.method = method;
        this.parameterData = parameterData;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public ParameterData[] getParameterData() {
        return this.parameterData;
    }
}

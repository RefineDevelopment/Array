package com.bizarrealex.aether;

public class AetherOptions
{
    private boolean hook;
    private boolean scoreDirectionDown;
    
    static AetherOptions defaultOptions() {
        return new AetherOptions().hook(false).scoreDirectionDown(false);
    }
    
    public boolean hook() {
        return this.hook;
    }
    
    public boolean scoreDirectionDown() {
        return this.scoreDirectionDown;
    }
    
    public AetherOptions hook(final boolean hook) {
        this.hook = hook;
        return this;
    }
    
    public AetherOptions scoreDirectionDown(final boolean scoreDirectionDown) {
        this.scoreDirectionDown = scoreDirectionDown;
        return this;
    }
}

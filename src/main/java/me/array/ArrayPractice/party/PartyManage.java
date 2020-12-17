package me.array.ArrayPractice.party;

import java.beans.ConstructorProperties;

public enum PartyManage
{
    LEADER("Make leader"), 
    KICK("Kick player"), 
    INCREMENTLIMIT("Increment limit by 1"), 
    PUBLIC("Make Party as Public"),
    DECREASELIMIT("Decrease limit by 1");
    
    private final String name;
    
    @ConstructorProperties({ "name" })
    PartyManage(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

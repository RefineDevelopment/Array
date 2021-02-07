package me.drizzy.practice.party;

import java.beans.ConstructorProperties;

public enum PartyPrivacy
{
    OPEN("Open"), 
    CLOSED("Closed");
    
    private final String readable;
    
    @ConstructorProperties({ "readable" })
    PartyPrivacy(final String readable) {
        this.readable = readable;
    }
    
    public String getReadable() {
        return this.readable;
    }
}

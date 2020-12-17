

package me.array.ArrayPractice.party;

import java.beans.ConstructorProperties;

public enum PartyPrivacy
{
    OPEN("Open"), 
    CLOSED("Closed");
    
    private String readable;
    
    @ConstructorProperties({ "readable" })
    private PartyPrivacy(final String readable) {
        this.readable = readable;
    }
    
    public String getReadable() {
        return this.readable;
    }
}

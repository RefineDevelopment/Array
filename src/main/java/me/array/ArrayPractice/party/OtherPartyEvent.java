

package me.array.ArrayPractice.party;

import java.beans.ConstructorProperties;

public enum OtherPartyEvent
{
    KIT("Normal Kit"), 
    HCF("HCF Kit");
    
    private String name;
    
    @ConstructorProperties({ "name" })
    private OtherPartyEvent(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

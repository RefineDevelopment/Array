package me.array.ArrayPractice.party;

import me.array.ArrayPractice.util.CC;
import java.beans.ConstructorProperties;

public enum PartyManage
{
    INCREASELIMIT(CC.GREEN + "Increase Limit"),
    PUBLIC("Open or Close Party"),
    DECREASELIMIT(CC.RED + "Decrease Limit");
    
    private final String name;
    
    @ConstructorProperties({ "name" })
    PartyManage(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

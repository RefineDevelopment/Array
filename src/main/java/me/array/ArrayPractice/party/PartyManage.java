package me.array.ArrayPractice.party;

import me.array.ArrayPractice.util.external.CC;
import java.beans.ConstructorProperties;

public enum PartyManage
{
    LIMIT(CC.GREEN + "Increase or Decrease Limit"),
    PUBLIC("Open or Close Party"),
    LEADER("Make Leader"),
    KICK("Kick from Party"),
    MANAGE_MEMBERS(CC.AQUA + "Manage Members");
    
    private final String name;
    
    @ConstructorProperties({ "name" })
    PartyManage(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

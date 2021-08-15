package me.drizzy.practice.enums;

import java.beans.ConstructorProperties;

public enum PartyManageType {
    LIMIT("Increase or Decrease Limit"),
    PUBLIC("Open or Close Party"),
    LEADER("Make Leader"),
    KICK("Kick from Party"),
    BAN("Ban from Party"),
    MANAGE("Manage Members");
    
    private final String name;
    
    @ConstructorProperties({ "name" })
    PartyManageType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

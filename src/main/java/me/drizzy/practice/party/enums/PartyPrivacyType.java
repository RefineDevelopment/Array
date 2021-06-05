package me.drizzy.practice.party.enums;

import lombok.AllArgsConstructor;
import me.drizzy.practice.util.chat.CC;

import java.beans.ConstructorProperties;

@AllArgsConstructor
public enum PartyPrivacyType {
    OPEN(CC.GREEN + "Public"), 
    CLOSED(CC.RED + "Private");
    
    private final String string;
    
    @Override
    public String toString() {
        return this.string;
    }
}

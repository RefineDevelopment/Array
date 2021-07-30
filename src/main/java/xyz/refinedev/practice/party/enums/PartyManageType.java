package xyz.refinedev.practice.party.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartyManageType {
    LIMIT("Increase or Decrease Limit"),
    PUBLIC("Open or Close Party"),
    LEADER("Make Leader"),
    KICK("Kick from Party"),
    BAN("Ban from Party"),
    MANAGE("Manage Members");
    
    private final String name;
}

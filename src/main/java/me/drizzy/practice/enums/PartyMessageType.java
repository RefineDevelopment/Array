package me.drizzy.practice.enums;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import java.beans.ConstructorProperties;
import java.text.MessageFormat;

public enum PartyMessageType {
    YOU_HAVE_BEEN_INVITED(Array.getInstance().getMessagesConfig().getString("Party.YOU_HAVE_BEEN_INVITED")),
    CLICK_TO_JOIN(Array.getInstance().getMessagesConfig().getString("Party.CLICK_TO_JOIN")),
    PLAYER_INVITED(Array.getInstance().getMessagesConfig().getString("Party.PLAYER_INVITED")),
    PLAYER_JOINED(Array.getInstance().getMessagesConfig().getString("Party.PLAYER_JOINED")),
    PLAYER_LEFT(Array.getInstance().getMessagesConfig().getString("Party.PLAYER_LEFT")),
    CREATED(Array.getInstance().getMessagesConfig().getString("Party.CREATED")),
    DISBANDED(Array.getInstance().getMessagesConfig().getString("Party.DISBANDED")),
    PUBLIC(Array.getInstance().getMessagesConfig().getString("Party.PUBLIC")),
    PRIVACY_CHANGED(Array.getInstance().getMessagesConfig().getString("Party.PRIVACY_CHANGED"));
    
    private final String message;
    
    public String format(final Object... objects) {
        return CC.translate(new MessageFormat(this.message).format(objects));
    }
    
    @ConstructorProperties({ "message" })
    PartyMessageType(final String message) {
        this.message = message;
    }
}

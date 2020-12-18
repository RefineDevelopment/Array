package me.array.ArrayPractice.party;

import java.beans.ConstructorProperties;
import me.array.ArrayPractice.util.external.CC;
import java.text.MessageFormat;

public enum PartyMessage
{
    YOU_HAVE_BEEN_INVITED("&eYou have been invited to join &a{0}&e''s party."),
    CLICK_TO_JOIN("&a(Click to accept)"), 
    PLAYER_INVITED("&b{0} &ehas been invited to your party."),
    PLAYER_JOINED("&b{0} &ejoined your party."),
    PLAYER_LEFT("&c{0} &ehas left your party."),
    CREATED("&aYou created a party."), 
    DISBANDED("&cYour party has been disbanded."), 
    PUBLIC("&b{0}&e is hosting a public party"),
    PRIVACY_CHANGED("&7Your party privacy has been changed to: &b{0}");
    
    private final String message;
    
    public String format(final Object... objects) {
        return CC.translate(new MessageFormat(this.message).format(objects));
    }
    
    @ConstructorProperties({ "message" })
    PartyMessage(final String message) {
        this.message = message;
    }
}

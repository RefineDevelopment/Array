package me.array.ArrayPractice.language;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;

import java.beans.ConstructorProperties;
import java.text.MessageFormat;

public enum LanguageEnum {

    MAIN_COLOR(Practice.getInstance().getLanuageConfig().getString("Array.Colors.Main"), "&b"),
    SECONDARY_COLOR(Practice.getInstance().getLanuageConfig().getString("Array.Colors.Secondary"), "&7"),
    TAB_ELEMENT(Practice.getInstance().getLanuageConfig().getString("Array.Tab.ElementsColor"), "&b"),
    TAB_VALUE(Practice.getInstance().getLanuageConfig().getString("Array.Tab.ValuesColor"), "&f"),
    TAB_HEADER(Practice.getInstance().getLanuageConfig().getString("Array.Tab.Header"), "&bResolve Practice"),
    TAB_FOOTER(Practice.getInstance().getLanuageConfig().getString("Array.Tab.Footer"), "&7store.resolve.rip"),
    YOU_HAVE_BEEN_INVITED(Practice.getInstance().getLanuageConfig().getString("Array.Party.PlayerInvited"), "&eYou have been invited to join &a{0}&e''s party."),
    CLICK_TO_JOIN(Practice.getInstance().getLanuageConfig().getString("Array.Party.ClickToJoin"), "&a(Click to accept)"),
    PLAYER_INVITED(Practice.getInstance().getLanuageConfig().getString("Array.Party.PartyInvited"), "&b{0} &ehas been invited to your party."),
    PLAYER_JOINED(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&b{0} &ejoined your party."),
    PLAYER_LEFT(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&c{0} &ehas left your party."),
    CREATED(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&aYou created a party."),
    DISBANDED(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&cYour party has been disbanded."),
    PUBLIC(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&b{0}&e is hosting a public party"),
    PRIVACY_CHANGED(Practice.getInstance().getLanuageConfig().getString("Array.Party.Values"), "&7Your party privacy has been changed to: &b{0}");

    private final String string;

    public String format(final Object... objects) {
        return CC.translate(new MessageFormat(this.string).format(objects));
    }

    @ConstructorProperties({"string"})
    LanguageEnum(final String ConfigValue, String DefaultValue) {
        if (ConfigValue == null) {
            this.string=DefaultValue;
        } else {
            this.string=ConfigValue;
        }
    }
}

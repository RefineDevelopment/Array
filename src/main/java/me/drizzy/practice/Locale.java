package me.drizzy.practice;

import lombok.Getter;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import me.drizzy.practice.util.config.Replacement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Drizzy
 * Created at 4/11/2021
 */
public enum Locale {

    PARTY_INVITED("PARTY.INVITED", "&8[&b&lParty&8] &7You have been invited to join &b<leader>'s &7party."),
    PARTY_CLICK_TO_JOIN("PARTY.CLICK_TO_JOIN", "&b(Click to accept)"),
    PARTY_INVITE_HOVER("PARTY.INVITE_HOVER", "&aClick to to accept this party invite"),
    PARTY_PLAYER_INVITED("PARTY.PLAYER_INVITED", "&8[&b&lParty&8] &b<invited> &7has been invited to your party."),
    PARTY_PLAYER_JOINED("PARTY.PLAYER_JOINED", "&8[&b&lParty&8] &b<joiner> &7joined your party."),
    PARTY_PLAYER_LEFT("PARTY.PLAYER_LEFT", "&8[&b&lParty&8] &c<leaver> &7has left your party."),
    PARTY_PLAYER_KICKED("PARTY.PLAYER_KICK", "&8[&b&lParty&8] &c<leaver> &7has been kicked from your party."),
    PARTY_CREATED("PARTY.CREATED", "&8[&b&lParty&8] &aYou created a party."),
    PARTY_DISABANDED("PARTY.DISBANDED", "&8[&b&lParty&8] &cYour party has been disbanded."),
    PARTY_PUBLIC("PARTY.PUBLIC", "&8[&b&lParty&8] &b<host> &ais hosting a public party"),
    PARTY_PRIVACY("PARTY.PRIVACY", "&8[&b&lParty&8] &7Your party privacy has been changed to &b<privacy>"),
    PARTY_PROMOTED("PARTY.PROMOTED", "&8[&b&lParty&8] &b<promoted> &ahas been promoted to Leader in your party."),
    PARTY_ALREADYHAVE("PARTY.ALREAD_HAVE", "&8[&b&lParty&8] &7You already have a party!"),
    PARTY_NOTLEADER("PARTY.NOTLEADER", "&8[&b&lParty&8] &7You are not the leader of this party!"),
    PARTY_DONOTHAVE("PARTY.DO_NOT_HAVE", "&8[&b&lParty&8] &7You don't have a party!"),
    PARTY_NOTLOBBY("PARTY.NOT_IN_LOBBY", "&8[&b&lParty&8] &7You are not in lobby, please finish your current task!"),
    MATCH_HCF_START_MESSAGE("MATCH.HCF_START_MESSAGE", Arrays.asList("", "&b&lHCF Match&7!", "", "&7Pick between &bBard&7, &bArcher&7, &bRogue&7 and &bDiamond", "&7Kits and Fight to the death to &bWin!", "")),
    END("", "");

    @Getter private final String path;
    @Getter private String value;
    @Getter private List<String> listValue;

    private final BasicConfigurationFile configFile = Array.getInstance().getMessagesConfig();

    Locale(String path, String value) {
        this.path = path;
        this.value = value;
    }

    Locale(String path, List<String> listValue) {
        this.path = path;
        this.listValue = listValue;
    }

    public String toString() {
        Replacement replacement = new Replacement(CC.translate(configFile.getConfiguration().getString(this.path)));
        return replacement.toString().replace("{0}", "\n");
    }

    public List<String> toList() {
        List<String> toReturn = new ArrayList<>();
        for ( String strings : configFile.getConfiguration().getStringList(this.path)) {
            toReturn.add(CC.translate(strings).replace("{0}", "\n"));
        }
        return toReturn;
    }

}

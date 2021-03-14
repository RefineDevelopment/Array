package me.drizzy.practice.settings.meta;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SettingsMeta {
    private boolean showScoreboard=true;
    private boolean receiveDuelRequests=true;
    private boolean allowSpectators=true;
    private boolean lightning=true;
    private boolean usingPingFactor=false;
    private boolean isPingScoreboard=true;
    private boolean allowTournamentMessages=true;
    private boolean isVanillaTab=false;
    private boolean isShowPlayers=true;

    //Party Chat
    private boolean isPartyChat=false;
}
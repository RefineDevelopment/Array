package me.array.ArrayPractice.profile.meta.option;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProfileOptions {
    private boolean showScoreboard=true;
    private boolean receiveDuelRequests=true;
    private boolean allowSpectators=true;
    private boolean lightning=true;
    private boolean usingPingFactor=false;
    private boolean isPingScoreboard=true;
    private boolean allowTournamentMessages=true;
    private boolean playerVisibility=false;
}
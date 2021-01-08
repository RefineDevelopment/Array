package me.array.ArrayPractice.profile.meta.option;

import lombok.Getter;
import lombok.Setter;

public class ProfileOptions {

    @Getter
    @Setter
    private boolean showScoreboard=true;
    @Getter
    @Setter
    private boolean receiveDuelRequests=true;
    @Getter
    @Setter
    private boolean allowSpectators=true;
    @Getter
    @Setter
    private boolean privateMessages=true;
    @Getter
    @Setter
    private boolean lightning=true;
}
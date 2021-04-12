package me.drizzy.practice.util.scoreboard;

import lombok.Getter;

@Getter
public enum AssembleStyle {

    KOHI(true, 15),
    VIPER(true, -1),
    MODERN(false, 1);

    private boolean decending;
    private int startNumber;

    AssembleStyle(boolean decending, int startNumber) {
        this.decending = decending;
        this.startNumber = startNumber;
    }
}

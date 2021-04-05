package me.drizzy.practice.events.types.lms.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LMSPlayerState {

    WAITING("Waiting"),
    ELIMINATED("Eliminated");

    private final String readable;

}

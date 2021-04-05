package me.drizzy.practice.events.types.oitc.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OITCPlayerState {

    WAITING("Waiting"),
    ELIMINATED("Eliminated");

    private final String readable;

}

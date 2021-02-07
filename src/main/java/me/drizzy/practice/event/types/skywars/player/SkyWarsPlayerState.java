package me.drizzy.practice.event.types.skywars.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SkyWarsPlayerState {

    WAITING("Waiting"),
    ELIMINATED("Eliminated");

    private final String readable;

}

package me.drizzy.practice.event.types.parkour.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ParkourPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private String readable;

}

package me.drizzy.practice.event.types.sumo.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SumoPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private String readable;

}

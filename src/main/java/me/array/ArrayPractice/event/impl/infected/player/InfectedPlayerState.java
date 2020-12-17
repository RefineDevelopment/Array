package me.array.ArrayPractice.event.impl.infected.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InfectedPlayerState {

	WAITING("Waiting"),
	INFECTED("Infected"),
	ELIMINATED("Eliminated");

	private String readable;

}

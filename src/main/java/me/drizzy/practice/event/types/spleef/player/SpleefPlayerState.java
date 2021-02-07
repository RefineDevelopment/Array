package me.drizzy.practice.event.types.spleef.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpleefPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private String readable;

}

package me.drizzy.practice.event.types.brackets.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BracketsPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private String readable;

}

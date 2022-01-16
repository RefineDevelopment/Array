package xyz.refinedev.practice.util.timer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Timer {

	protected final String name;
	protected final long defaultCooldown;
}
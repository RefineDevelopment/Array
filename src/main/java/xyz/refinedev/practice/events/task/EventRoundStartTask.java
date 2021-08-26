package xyz.refinedev.practice.events.task;

import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.meta.EventTask;

public class EventRoundStartTask extends EventTask {

	public EventRoundStartTask(Event event) {
		super(event, EventState.ROUND_STARTING);
	}

	@Override
	public void onRun() {

	}
}
